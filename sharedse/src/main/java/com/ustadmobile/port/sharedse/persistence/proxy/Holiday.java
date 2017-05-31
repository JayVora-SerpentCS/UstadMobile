package com.ustadmobile.port.sharedse.persistence.proxy;

import com.j256.ormlite.field.types.DateTimeType;
import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

import java.util.Collection;

/**
 * Created by varuna on 5/23/2017.
 */

public interface Holiday extends NanoLrsModel{
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

    long getDate();
    void setDate(long date);


    /* Trying this for UMCalendar2Holiday: TODO: Check if this is the best way to do m2m */
    /**
     * Not really sure how this method will be useful though..
     * eg: Get all calendars where May 4th is a holiday ??
     */
    /*
    @nanolrs.foreignFieldName=calendar
     */
    Collection<? extends UMCalendar2Holiday> getCalendars();
    void setCalendars(Collection<? extends UMCalendar2Holiday> calendars);

}
