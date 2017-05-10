package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.nanolrs.core.persistence.PersistenceManager;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.util.PlatformTestUtil;


import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by varuna on 5/2/2017.
 */

public class TestPerson {

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
    public void testLifeCycle() throws Exception{
        Object context = PlatformTestUtil.getContext();

        String first_name = "Varuna";
        String last_name = "Singh";
        PersonManager personManager = PersistenceManager.getInstance().getManager(PersonManager.class);

        Person newPerson = (Person) personManager.makeNew(); //just creates a blank object
        newPerson.setUUID(UUID.randomUUID().toString());
        newPerson.setFirstName(first_name);
        newPerson.setLastName(last_name);
        personManager.persist(context, newPerson);

        //Lets get the list of all users with that first name.
        List<Person> firstNameList = personManager.findAllByFirstName(context, first_name);

        Assert.assertEquals(firstNameList.size(), 1);
        Assert.assertEquals(firstNameList.get(0).getFirstName(), first_name);
        personManager.delete(context, newPerson);
        firstNameList = personManager.findAllByFirstName(context, first_name);
        Assert.assertEquals(firstNameList.size(), 0);
    }
}
