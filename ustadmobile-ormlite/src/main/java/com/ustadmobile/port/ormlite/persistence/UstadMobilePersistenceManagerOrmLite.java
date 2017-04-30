package com.ustadmobile.port.ormlite.persistence;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;
import com.ustadmobile.port.sharedse.persistence.UstadMobilePersistenceManager;
import com.ustadmobile.port.sharedse.persistence.manager.PersonManager;
import com.ustadmobile.ustadmobile.ormlite.manager.PersonManagerOrmLite;

import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by Varuna on 4/30/2017.
 */
public class UstadMobilePersistenceManagerOrmLite extends UstadMobilePersistenceManager {

    protected WeakHashMap<Class, ? extends NanoLrsManager<? extends NanoLrsManager, ?>> managersCache;

    public static HashMap<Class, Class<>> MANAGER_IMPL_MAP;

    static {
        MANAGER_IMPL_MAP = new HashMap<>();
        MANAGER_IMPL_MAP.put(PersonManager.class, PersonManagerOrmLite.class);
    }

    public UstadMobilePersistenceManagerOrmLite() {
        managersCache = new WeakHashMap<>();
    }

    @Override
    public <M extends NanoLrsManager<? extends NanoLrsModel, ?>> M getManager(Class<M> managerType) {
        NanoLrsManager<? extends NanoLrsModel, ?> manager = managersCache.get(managerType);
        if(manager == null) {
            try {
                manager = MANAGER_IMPL_MAP.get(managerType).newInstance();
            }catch(Exception e) {

            }
        }

        return (M)manager;
    }
}
