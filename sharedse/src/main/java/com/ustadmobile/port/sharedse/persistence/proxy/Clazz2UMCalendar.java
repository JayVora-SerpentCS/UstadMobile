package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 6/1/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

interface Clazz2UMCalendar extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @return
     * @nanolrs.primarykey
     */
    String getUUID();

    void setUUID(String uuid);

    Clazz getClazz();
    void setClazz(Class clazz);

    UMCalendar getHoliday();
    void setHoliday(UMCalendar umCalendar);
}
