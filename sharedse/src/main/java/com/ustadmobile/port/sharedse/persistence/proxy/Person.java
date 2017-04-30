package com.ustadmobile.port.sharedse.persistence.proxy;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * TODO: This should extend NanoLrs's BaseModel for things like
 * primary key, common fields, last modified, last updated, pushed,
 * etc and common serialised toString fromString methods to be used
 * in Sync API.
 *
 * Created by Varuna on 4/30/2017.
 */
public interface Person extends NanoLrsModel{

    /**
     * Tells the generator that this is the primary key.
     *
     * @nanolrs.primarykey
     *
     * @return
     */
    String getUUID();

    String getFirstName();

    String getLastName();

    void setFirstName(String firstName);

    void setLastName(String lastName);

    void setUUID(String uuid);


}
