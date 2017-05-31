package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 */

public interface ClazzAlertSettings extends NanoLrsModel{

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    Clazz getClazz();
    void setClazz(Clazz clazz);

    /* The cut off time that the alert will be sent? */
    int getCutOffTime();
    void setCutOffTime(int cutOffTime);
    //default = 1 in hours
}
