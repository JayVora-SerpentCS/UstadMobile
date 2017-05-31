package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 */

public interface SchoolAlertSettings extends NanoLrsModel{

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    School getSchool();
    void setSchool(School school);

    int getCutOffTime();
    void setCutOffTime(int cutOffTime); //default 1 in hours
}
