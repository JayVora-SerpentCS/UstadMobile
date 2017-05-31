package com.ustadmobile.port.sharedse.persistence;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.nanolrs.core.persistence.PersistenceManager;
import com.ustadmobile.port.sharedse.persistence.manager.ClazzManager;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Clazz;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;
import com.ustadmobile.port.sharedse.persistence.util.PlatformTestUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by varuna on 5/14/2017.
 */

public class TestProxyJsonSerializer {

    @Before
    public void registerMe() throws Exception{
        //old way:
        //Register Managers using Implementation.
        //UstadMobileSystemImpl.getInstance().registerManagers();

        //we should do it this way:
        //Register Manager using Implementation's init.
        UstadMobileSystemImpl.getInstance().init(PlatformTestUtil.getContext());

    }

    @Test
    public void testSerialize() throws Exception{
        //create some object -
        //give it to ProxyJsonSerializer
        //Check the json
        // Done.
        Object context = PlatformTestUtil.getContext();

        String first_name = "Varuna";
        String last_name = "Singh";
        PersonManager personManager = PersistenceManager.getInstance().getManager(PersonManager.class);

        Person newPerson = (Person) personManager.makeNew(); //just creates a blank object
        newPerson.setUUID(UUID.randomUUID().toString());
        newPerson.setFirstName(first_name);
        newPerson.setLastName(last_name);

        JSONObject objectToJson;
        objectToJson = ProxyJsonSerializer.toJson(newPerson, Person.class);

        Assert.assertEquals("Property First name serialized correctly", "Varuna", objectToJson.getString("firstName"));

        //Try to get the object back

        Person personFromJSON = (Person) ProxyJsonSerializer.toEntity(objectToJson, context);

        Assert.assertEquals("Person object created from JSON", "Varuna", personFromJSON.getFirstName());


        String class_name = "Algebra";
        String class_desc = "Mathematics class";
        ClazzManager classManager = PersistenceManager.getInstance().getManager(ClazzManager.class);

        Clazz newClass = (Clazz) classManager.makeNew(); //just creates a blank object
        newClass.setName(class_name);
        newClass.setDesc(class_desc);
        newClass.setTeacher(newPerson);

        objectToJson = ProxyJsonSerializer.toJson(newClass, Clazz.class);

        Assert.assertEquals("Property Class name serialized correctly", class_name, objectToJson.getString("name"));

        //Try to get the object back


        Clazz clazzFromJSON = (Clazz) ProxyJsonSerializer.toEntity(objectToJson, context);

        Assert.assertEquals("Clazz object created from JSON", class_name, clazzFromJSON.getName());

        //The following will fail because:
        // 1. Previous fields didnt persist
        // 2. New Person created doesnt have attributes assigned.
        // 3. Solution: call persist ?
        //Assert.assertEquals("Clazz teacher set OK in Entity Obj created from JSON",
        //        newPerson.getFirstName(), clazzFromJSON.getTeacher().getFirstName());

    }

}
