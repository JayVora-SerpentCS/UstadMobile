package com.ustadmobile.test.sharedse;

import com.ustadmobile.port.sharedse.impl.UstadMobileSystemImplSE;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by kileha3 on 08/06/2017.
 */

public class TestAppSetupFileSharing {

    @Test
    public void testAppFileSharing(){
        UstadMobileSystemImplSE impl = UstadMobileSystemImplSE.getInstanceSE();
        Assert.assertNotNull(impl);
        File appSetupFile=new File(impl.getAppSetupFile(impl.getNetworkManager().getContext()));
        Assert.assertEquals("Setup file to be shared reported exits", true, appSetupFile.exists());
    }
}
