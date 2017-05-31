package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

import java.util.Collection;

/**
 * Created by varuna on 5/24/2017.
 */

public interface AlertType extends NanoLrsModel {
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

    /* Trying this for Alert2AlertType: TODO: Check if this is the best way to do m2m */
    /*
    @nanolrs.foreignFieldName=alert
    */
    Collection<? extends Alert2AlertType> getAlerts();
    void setAlerts(Collection <? extends Alert2AlertType> alerts);
}
