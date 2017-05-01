package com.ustadmobile.port.sharedse.persistence.proxy;

/**
 * Created by Varuna on 4/27/2017.
 */
public interface MgmtTest {

    /**
     * @nanolrs.primarykey
     *
     * @return
     */
    String getTestId();

    void setTestId(String testId);

    String getCanonicalData();

    void setCanonicalData(String canonicalData);

}