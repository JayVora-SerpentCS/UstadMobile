package com.ustadmobile.port.sharedse.test;

import com.ustadmobile.port.ormlite.persistence.manager.PersonManagerOrmLite;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import static com.ustadmobile.nanolrs.ormlite.persistence.PersistenceManagerORMLite.registerManagerImplementation;

/**
 * Created by varuna on 5/10/2017.
 */

public class RegisterManagersSharedSe {

    public static void registerMe(Class coreManager, Class implementation){
        System.out.println("Registering managers ..");
        //registerManagerImplementation(PersonManager.class, PersonManagerOrmLite.class);
        //TODO: Fix this.
        //Get Implementation Manager from context ?
        //registerManagerImplementation(PersonManager.class,
        //        PersistenceManager.getInstance().getManager(PersonManager.class));

        registerManagerImplementation(coreManager, implementation);

    }

    public static void registerMe(){
        registerManagerImplementation(PersonManager.class, PersonManagerOrmLite.class);
    }
}
