package com.ustadmobile.core.impl;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.port.gwt.impl.UstadMobileSystemImplGWT;

/**
 * Created by varuna on 5/4/2017.
 */

public class UstadMobileSystemImplFactory {

    public static UstadMobileSystemImpl makeSystemImpl() {

        return new UstadMobileSystemImplGWT();
    }
}
