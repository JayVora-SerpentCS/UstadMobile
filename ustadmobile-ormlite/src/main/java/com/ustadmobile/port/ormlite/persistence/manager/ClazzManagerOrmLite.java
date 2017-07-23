package com.ustadmobile.port.ormlite.persistence.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.ustadmobile.nanolrs.ormlite.manager.BaseManagerOrmLite;
import com.ustadmobile.port.ormlite.persistence.generated.model.ClazzEntity;
import com.ustadmobile.port.ormlite.persistence.generated.model.PersonEntity;
import com.ustadmobile.port.sharedse.persistence.manager.ClazzManager;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Clazz;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by varuna on 5/2/2017.
 */

public class ClazzManagerOrmLite extends BaseManagerOrmLite implements ClazzManager {

    public ClazzManagerOrmLite(){
    }

    @Override
    public Class getEntityImplementationClasss() {
        return ClazzEntity.class;
    }

    @Override
    public List<Clazz> findAllByClassName(Object dbContext, String clazzName) throws SQLException {

        Dao<ClazzEntity, String> dao = persistenceManager.getDao(ClazzEntity.class, dbContext);

        QueryBuilder<ClazzEntity, String> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq(ClazzEntity.COLNAME_NAME, clazzName);
        return(List<Clazz>)(Object)dao.query(queryBuilder.prepare());
    }

    @Override
    public List<Clazz> findAllByTeacherName(Object dbContext, Person teacher) throws SQLException {
        return null;
    }

    @Override
    public List<Person> findAllStudents(Object dbContext, Clazz clazz) throws SQLException {
        return null;
    }

    @Override
    public List<Person> findAllTeachers(Object dbContext, Clazz clazz) throws SQLException {
        return null;
    }


}
