package com.ustadmobile.port.sharedse.persistence;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.nanolrs.core.persistence.PersistenceManager;
import com.ustadmobile.port.sharedse.persistence.manager.ClazzManager;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.manager.SchoolManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Clazz;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;
import com.ustadmobile.port.sharedse.persistence.proxy.School;
//import com.ustadmobile.port.sharedse.persistence.util.PlatformTestUtil;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import sun.reflect.annotation.AnnotatedTypeFactory;

/**
 * Created by varuna on 5/14/2017.
 */

public class ProxyJsonSerializer {

    /**
     * When object is serialized to json this field will be added. When it is deserialized this field
     * is used to determine which class to instantiate
     */
    public static final String PROXY_CLASS_JSON_FIELD = "pCls";

    /**
     * Map of entity names to the proxy class
     */
    private static HashMap<String, Class> proxyNameToClassMap = new HashMap<>();

    private static HashMap<Class, Class> proxyClassToManagerMap = new HashMap<>();

    static {
        proxyNameToClassMap.put(Person.class.getName(), Person.class);
        proxyClassToManagerMap.put(Person.class, PersonManager.class);
        proxyNameToClassMap.put(Clazz.class.getName(), Clazz.class);
        proxyClassToManagerMap.put(Clazz.class, ClazzManager.class);
        proxyNameToClassMap.put(School.class.getName(), School.class);
        proxyClassToManagerMap.put(School.class, SchoolManager.class);
    }

    /**
     * Converts an Entity Proxy Object to JSON. Serialises the Object.
     * To be used in SyncAPI
     * @param object    The object to be seralised to JSON
     * @param proxyClass    The proxy class of this object
     * @return
     */
    public static JSONObject toJson(NanoLrsModel object, Class proxyClass) {
        Method[] methods = proxyClass.getMethods();
        JSONObject returnVal = new JSONObject();
        for(int i = 0; i < methods.length; i++){
            String methodName = methods[i].getName();
            int prefixLen = 0;
            if(methodName.startsWith("is"))
                prefixLen = 2;
            else if(methodName.startsWith("get"))
                prefixLen = 3;

            if(prefixLen != 0) {
                String propName = Character.toLowerCase(methodName.charAt(prefixLen))
                        + methodName.substring(prefixLen +1);

                Method valGetterMethod = methods[i];
                Object invocationTarget = object;

                if(!(methods[i].getReturnType().isPrimitive() || methods[i].getReturnType().equals(String.class))) {
                    try {
                        invocationTarget = methods[i].invoke(object);
                        Class relatedClass = methods[i].getReturnType();
                        Method[] relatedClassMethods = relatedClass.getMethods();
                        valGetterMethod = null;
                        for(Method relatedMethod : relatedClassMethods) {
                            if(relatedMethod.isAnnotationPresent(PrimaryKeyAnnotationClass.class)) {
                                valGetterMethod = relatedMethod;
                                break;
                            }
                        }

                        if(valGetterMethod == null) {
                            throw new RuntimeException("No Primary Key Annotation Class");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    returnVal.put(propName, valGetterMethod.invoke(invocationTarget));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        returnVal.put(PROXY_CLASS_JSON_FIELD, proxyClass.getName());
        return returnVal;
    }

    /**
     * Convert JSON to Entity Object
     * @param entityJSON
     * @return
     */
    public static NanoLrsModel toEntity(JSONObject entityJSON, Object context ){
        String proxyClassName = entityJSON.getString(PROXY_CLASS_JSON_FIELD);
        Class proxyClass = proxyNameToClassMap.get(proxyClassName);
        Class managerClass = proxyClassToManagerMap.get(proxyClass);
        NanoLrsManager manager = PersistenceManager.getInstance().getManager(managerClass);
        //Object context = PlatformTestUtil.getContext();
        NanoLrsModel newObj = null;
        try {
            newObj = manager.makeNew();

            Iterator<String> keys = entityJSON.keys();
            String keyName;
            while(keys.hasNext()) {
                keyName = keys.next();
                if(keyName.equals(PROXY_CLASS_JSON_FIELD))
                    continue;

                //Check if a relationship or primitive

                Object value = entityJSON.get(keyName);
                Class valueType = value.getClass();
                String methodPropName = Character.toUpperCase(keyName.charAt(0)) +
                        keyName.substring(1);
                String methodName = "set" + methodPropName;

                String getterMethodName = (value instanceof Boolean ? "is" : "get") + methodPropName;
                Method getterMethod = proxyClass.getMethod(getterMethodName);

                if(!(getterMethod.getReturnType().isPrimitive() || getterMethod.getReturnType().equals(String.class)))
                {
                    System.out.println("RELATIONSHIP!");

                    Class relatedProxyClass = proxyNameToClassMap.get(getterMethod.getReturnType().getName());
                    Class realtedManagerClass = proxyClassToManagerMap.get(relatedProxyClass);

                    //Get the manager of the related type
                    NanoLrsManager relatedManager =
                            PersistenceManager.getInstance().getManager(realtedManagerClass);


                    /*
                        LOOK UP. IF NOT THERE>CREATE>ASSIGN ID. ASSIGN
                    */
                    ///*
                    Object entity_id = value;
                    Class relatedObjProxy = getterMethod.getReturnType();

                    //TODO:
                    // Don't look up Just set it. (DB Managers will do an insert)
                    //look up entity by this id
                    NanoLrsModel relatedObj = relatedManager.findByPrimaryKey(context, value);
                    // if doesn't exist, create a blank one.
                    if (relatedObj == null){
                        //create a blank one
                        relatedObj = relatedManager.makeNew();

                        //Assign primary key (entity_id) to addMe
                        //Get Primary Key setter from Primary Key Annotation in the getter of addMe Class
                        //Get relatedObj's proxy FIRST
                        //Loop through methods of the proxy to get the primary key getter first then
                        //get the setter from the name of the getter and then invote it with the value.

                        Method relatedObjProxyPKGetter = null;
                        Method relatedObjProxyPKSetter = null;
                        Method relatedObjEntityPKSetter = null;
                        relatedObj.getClass().getMethods();

                        Method[] relatedObjProxyMethods = relatedObjProxy.getMethods();
                        for (Method everyProxyMethod : relatedObjProxyMethods){
                            if(everyProxyMethod.isAnnotationPresent(PrimaryKeyAnnotationClass.class)){
                                relatedObjProxyPKGetter = everyProxyMethod;
                                break;

                            }
                        }
                        String primaryKeyGetterName = null;
                        String primaryKeySetterName = null;
                        if(relatedObjProxyPKGetter != null){
                            primaryKeyGetterName = relatedObjProxyPKGetter.getName();
                            int prefixLen = 0;
                            if(primaryKeyGetterName.startsWith("is"))
                                prefixLen = 2;
                            else if(primaryKeyGetterName.startsWith("get"))
                                prefixLen = 3;
                            primaryKeySetterName = "set" + Character.toUpperCase(primaryKeyGetterName.charAt(prefixLen))
                                    + primaryKeyGetterName.substring(prefixLen +1);

                            //relatedObjEntityPKSetter = relatedObj.getClass().getMethod(primaryKeySetterName, entity_id.getClass());
                            relatedObj.getClass().getMethod(primaryKeySetterName, entity_id.getClass());
                            valueType = relatedObjProxy;
                        }
                    }
                    value = relatedObj;


                    //*/
                }

                //gets the setter Method on the current methodName with argument of
                // value's type (class)
                Method setterMethod = proxyClass.getMethod(methodName, valueType);
                //Invokes the setter Method that we got and gives it the value to pass
                //to its argument
                setterMethod.invoke(newObj, value);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        return newObj;
    }

}
