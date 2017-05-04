package com.ustadmobile.port.ormlite.persistence.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.ustadmobile.nanolrs.ormlite.generated.model.XapiUserEntity;
import com.ustadmobile.nanolrs.ormlite.manager.BaseManagerOrmLite;
import com.ustadmobile.port.ormlite.persistence.generated.model.PersonEntity;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by varuna on 5/2/2017.
 */

public class PersonManagerOrmLite extends BaseManagerOrmLite implements PersonManager {

    public PersonManagerOrmLite(){
    }

    @Override
    public Class getEntityImplementationClasss() {
        return PersonEntity.class;
    }

    @Override
    public List<Person> findAllByFirstName(Object dbContext, String firstName) throws SQLException {
        Dao<PersonEntity, String> dao = persistenceManager.getDao(PersonEntity.class, dbContext);

        QueryBuilder<PersonEntity, String> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq(PersonEntity.COLNAME_FIRST_NAME, firstName);
        return(List<Person>)(Object)dao.query(queryBuilder.prepare());
    }
}
