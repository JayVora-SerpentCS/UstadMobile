package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.PrimaryKeyAnnotationClass;

import java.util.Collection;

/**
 * Created by varuna on 5/23/2017.
 */

public interface UMCalendar extends NanoLrsModel {

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

    /*TODO: Orgnaisation has a calendar field. So Maybe delete this one after all checks? */
    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    String getName();
    void setName(String name);

    /* Trying this for UMCalendar2Holiday: TODO: Check if this is the best way to do m2m */
    /*
    @nanolrs.foreignFieldName=holiday
     */
    Collection<? extends UMCalendar2Holiday> getHolidays();
    void setHolidays(Collection<? extends UMCalendar2Holiday> holidays);

}
