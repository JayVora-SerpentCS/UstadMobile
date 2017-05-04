package com.ustadmobile.port.gwt.impl;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;

/**
 * Created by varuna on 5/4/2017.
 */

public class UstadMobileSystemImplFactory {


    public UstadMobileSystemImpl makeUstadSystemImpl() {
        return new UstadMobileSystemImplGWT();
    }
}
