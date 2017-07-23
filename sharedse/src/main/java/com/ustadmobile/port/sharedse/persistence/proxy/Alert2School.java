package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 5/30/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

public interface Alert2School extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @return
     * @nanolrs.primarykey
     */
    String getUUID();

    void setUUID(String uuid);

    Alert getAlert();
    void setAlert(Alert alert);

    School getSchool();
    void setSchool(School school);
}
