package com.ustadmobile.port.gwt.impl;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.impl.UstadMobileSystemImplFactory;

/**
 * Created by varuna on 5/4/2017.
 */

public class UstadMobileSystemImplFactoryGWT extends UstadMobileSystemImplFactory {
    @Override
    public UstadMobileSystemImpl makeUstadSystemImpl() {
        return new UstadMobileSystemImplGWT();
    }
}
