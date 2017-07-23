package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

import java.util.Collection;

/**
 * TODO: This should extend NanoLrs's BaseModel for things like
 * primary key, common fields, last modified, last updated, pushed,
 * etc and common serialised toString fromString methods to be used
 * in Sync API.
 *
 * Created by Varuna on 4/30/2017.
 */
public interface School extends NanoLrsModel{

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
    void setName(String name);

    String getDesc();
    void setDesc(String desc);

    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    long getLongitude();
    void setLongitude(long longitude);

    long getLattitude();
    void setLattitude(long lattitude);

    boolean isShowLocation();
    void setShowLocation(boolean showLocation);

    boolean isShowExactLocation();
    void setShowExactLocation(boolean showExactLocation);

    /* Trying this for School2Weekends: TODO: Check if this is the best way to do m2m */
    /*
    @nanolrs.foreignFieldName=day
     */
    Collection<? extends School2Weekend> getWeekends();
    void setWeekends(Collection<? extends School2Weekend> weekends);

    /* Trying this for School2Calendar (holiday): TODO: Chck if this is the best way to do m2m*/
    /*
    @nanolrs.foreignFieldName=calendar
     */
    Collection<? extends School2UMCalendar> getHolidayCalendar();
    void setHolidayCalendar(Collection<? extends School2UMCalendar> holidayCalendar);


    //Relationships coming in:

    /* Trying this for Alert2School : TODO: Check if this is the best way to do m2m*/
     /*
    @nanolrs.foreignFieldName=alert
     */
    Collection<? extends Alert2School> getAlerts();
    void setAlerts(Collection<? extends Alert2School> alerts);


    //TODO: School Holidays
    //TODO: School Weekdays
}
