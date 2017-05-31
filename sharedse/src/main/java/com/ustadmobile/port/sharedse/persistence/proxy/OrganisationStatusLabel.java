package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 */

public interface OrganisationStatusLabel extends NanoLrsModel {

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    AttendanceStatusLabel getStatusLabel();
    void setStatusLabel(AttendanceStatusLabel statusLabel);
}
