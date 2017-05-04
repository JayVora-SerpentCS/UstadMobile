package com.ustadmobile.port.ormlite.register;

import com.ustadmobile.port.ormlite.persistence.manager.PersonManagerOrmLite;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import static com.ustadmobile.nanolrs.ormlite.persistence.PersistenceManagerORMLite.registerManagerImplementation;

/**
 * Created by varuna on 5/2/2017.
 */

public class RegisterManager {

    private static void register_manager(){
        registerManagerImplementation(PersonManager.class, PersonManagerOrmLite.class);
    }
}
