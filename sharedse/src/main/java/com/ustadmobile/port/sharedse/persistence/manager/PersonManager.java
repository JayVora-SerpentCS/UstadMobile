package com.ustadmobile.port.sharedse.persistence.manager;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import java.util.List;

/**
 * TODO: This needs to extend BaseManager from NanoLrs for things like
 * persist, makenew, findbyid, etc
 *
 * Created by Varuna on 4/30/2017.
 */
public interface PersonManager extends NanoLrsManager {
    //Person specific methods  to work with.
    List<Person> findAllByFirstName (Object dbContext, String firstName);

}
