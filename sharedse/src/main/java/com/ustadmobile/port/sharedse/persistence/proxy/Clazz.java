package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

import java.util.Collection;

/**
 * Created by varuna on 5/11/2017.
 */

public interface Clazz extends NanoLrsModel {

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    @PrimaryKeyAnnotationClass(str="pk")
    String getUUID();

    void setUUID(String uuid);

    String getName();
    void setName(String allclassName);

    String getDesc();
    void setDesc(String allclassDesc);

    String getLocation();
    void setLocation(String allclassLocation);


    //Person getTeacher();
    //void setTeacher(Person teacher);

    //TODO: These:

    //School getSchool();

    //List<Person> getStudents();

    //List<Person> getTeachers();

    //void setSchool(School school);

    //void setStudents(List<Person> students);

    //void setTeachers(List<Person> teachers);

    /* Clazz2Students m2m : TODO: Check if this is the best way to do m2m */
    /*
    @nanolrs.foreignFieldName=student
     */
    Collection<? extends Clazz2Student> getStudents();
    void setStudents(Collection<? extends Clazz2Student> students);

    /* Clazz2Teachers m2m: TODO: Check if this is the best way to do m2m*/
    /*
    @nanolrs.foreignFieldName=teacher
     */
    Collection<? extends Clazz2Teacher> getTeachers();
    void setTeachers(Collection<? extends Clazz2Teacher> teachers);

    School getSchool();
    void setSchool(School school);

    /*
    @nanolrs.foreignFieldName=day
     */
    Collection<? extends Clazz2Day> getDays();
    void setDays(Collection<? extends Clazz2Day> days);

    /*
    @nanolrs.foreignFieldName=holiday
     */
    Collection<? extends Clazz2UMCalendar> getHolidayCalendars();
    void setHolidayCalendars(Collection<? extends Clazz2UMCalendar> holidayCalendars);


    //Relationships external

    /* Trying this for Alert2School : TODO: Check if this is the best way to do m2m*/
     /*
    @nanolrs.foreignFieldName=alert
     */
    Collection<? extends Alert2Clazz> getAlerts();
    void setAlerts(Collection<? extends Alert2Clazz> alerts);


}
