package com.ustadmobile.port.sharedse.persistence.proxy;
/**
 * Created by varuna on 5/31/2017.
 */

import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

public interface CountryOrganisation extends NanoLrsModel {
    /**
     * Tells the generator that this is the primary key.
     *
     * @return
     * @nanolrs.primarykey
     */
    String getUUID();

    void setUUID(String uuid);

    String getCountryCode();
    void setCountryCode(String countryCode);

    Organisation getOrganisation();
    void setOrganisation(Organisation organisation);

    /* TODO: m2m: CountryOrganisation2Courses*/
}
