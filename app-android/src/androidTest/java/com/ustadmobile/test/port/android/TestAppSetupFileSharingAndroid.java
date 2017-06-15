package com.ustadmobile.test.port.android;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.test.sharedse.TestAppSetupFileSharing;

import org.junit.BeforeClass;

/**
 * Created by kileha3 on 08/06/2017.
 */

public class TestAppSetupFileSharingAndroid extends TestAppSetupFileSharing{

    @BeforeClass
    public static void testAppFileSharingAndroid() {
        Context context = InstrumentationRegistry.getTargetContext();
        UstadMobileSystemImpl.getInstance().init(context);
    }
}
