package com.ustadmobile.port.sharedse.persistence.manager;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.port.sharedse.persistence.proxy.Clazz;
import com.ustadmobile.port.sharedse.persistence.proxy.Person;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by varuna on 5/14/2017.
 */

public interface ClazzManager extends NanoLrsManager{
    List<Clazz> findAllByClassName(Object dbContext, String clazzName) throws SQLException;
    List<Clazz> findAllByTeacherName (Object dbContext, Person teacher) throws SQLException;
    List<Person> findAllStudents(Object dbContext, Clazz clazz) throws SQLException;
    List<Person> findAllTeachers(Object dbContext, Clazz clazz) throws SQLException;
}
