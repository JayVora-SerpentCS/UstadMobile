package com.ustadmobile.port.sharedse.persistence.proxy;

import com.j256.ormlite.field.types.DateTimeType;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

/**
 * Created by varuna on 5/24/2017.
 *
 * Model for days of the week and their times
 */

public interface WeekDayTime extends NanoLrsModel{

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

    Day getDay();
    void setDay(Day day);

    long getFromTime();
    void setFromTime(long fromTime);
    //default = 00:00:00

    long getToTime();
    void setToTime(long toTime);
    //default = 23:59:59

}
