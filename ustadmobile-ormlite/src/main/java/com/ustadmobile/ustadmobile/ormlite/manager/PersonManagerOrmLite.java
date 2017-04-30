package com.ustadmobile.ustadmobile.ormlite.manager;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import java.util.List;

/**
 * Created by Varuna on 4/30/2017.
 */
public class PersonManagerOrmLite<T extends NanoLrsModel, P> extends BaseManagerOrmLite implements PersonManager {

    @Override
    public List<Person> findAllByFirstName(Object dbContext, String firstName) {
        return null;
    }

}
