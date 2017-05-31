package com.ustadmobile.port.sharedse.persistence.proxy;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by varuna on 5/24/2017.
 */

public interface OrganisationCode extends NanoLrsModel{
    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    void setUUID(String uuid);

    Organisation getOrganistaion();
    void setOrganisation(Organisation organisation);

    String getCode();
    void setCode(String code);
}
