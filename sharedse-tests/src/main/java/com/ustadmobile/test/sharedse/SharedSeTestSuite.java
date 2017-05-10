package com.ustadmobile.test.sharedse;

import com.ustadmobile.port.sharedse.persistence.proxy.SimpleTest;
import com.ustadmobile.port.sharedse.persistence.proxy.TestPerson;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by mike on 5/10/17.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        BasicTest.class,
        BluetoothTest.class,
        SimpleTest.class,
        TestPerson.class
})
public abstract class SharedSeTestSuite {
}
