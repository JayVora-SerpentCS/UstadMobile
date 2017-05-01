package com.ustadmobile.port.sharedse.persistence;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by Varuna on 4/30/2017.
 * Abstract Persistence Manager for UstadMobile
 */
public abstract class UstadMobilePersistenceManager {

    public abstract <M extends NanoLrsManager<? extends NanoLrsModel, ?>> M getManager(Class<M> managerType);

}
