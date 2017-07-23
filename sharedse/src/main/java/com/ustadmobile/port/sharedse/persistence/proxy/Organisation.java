package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/11/2017.
 */

public interface Organisation extends NanoLrsModel {

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    String getName();
    void setName(String name);

    String getDesc();
    void setDesc(String desc);

    String getCode();
    void setCode(String code);

    AttendanceStatusLabel getStatusLabel();
    void setStatusLabel(AttendanceStatusLabel statusLabel);

    boolean isPublik();
    void setPublik(boolean isPublik);

    /* Organisation's calendar */
    UMCalendar getCalendar();
    void setCalendar(UMCalendar calendar);

    /* The UstadMobile package this org is part of */
    UMPackage getUMPackage();
    void setUMPackage(UMPackage umPackage);

}