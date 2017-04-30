package com.ustadmobile.ustadmobile.ormlite.manager;

import com.ustadmobile.nanolrs.core.manager.NanoLrsManager;
import com.ustadmobile.nanolrs.core.model.NanoLrsModel;

/**
 * Created by Varuna on 4/30/2017.
 */
public class BaseManagerOrmLite<T extends NanoLrsModel, P>  implements NanoLrsManager<T, P> {

    @Override
    public T makeNew() {
        return null;
    }

    @Override
    public void persist(T data) {

    }

    @Override
    public void delete(T data) {

    }

    @Override

    public T findByPrimaryKey(P primaryKey) {
        return null;
    }
}
