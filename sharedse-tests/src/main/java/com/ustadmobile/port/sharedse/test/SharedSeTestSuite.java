package com.ustadmobile.port.sharedse.test;

import com.ustadmobile.port.sharedse.persistence.proxy.SimpleTest;
import com.ustadmobile.port.sharedse.persistence.proxy.TestPerson;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by varuna on 5/9/2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SimpleTest.class,
        TestPerson.class,
})

public abstract class SharedSeTestSuite {
}
