package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 5/31/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

public interface School2UMCalendar extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @return
     * @nanolrs.primarykey
     */
    String getUUID();

    void setUUID(String uuid);

    School getSchool();
    void setSchool(School school);

    UMCalendar getCalendar();
    void setCalendar(UMCalendar calendar);
}
