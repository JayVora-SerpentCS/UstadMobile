package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 5/31/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

interface Alert2AlertType extends NanoLrsModel {
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

    AlertType getAlertType();
    void setAlertType(AlertType alertType);
}
