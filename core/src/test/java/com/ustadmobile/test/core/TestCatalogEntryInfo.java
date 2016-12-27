/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */
package com.ustadmobile.test.core;

/* $if umplatform == 2  $
    import j2meunit.framework.TestCase;
 $else$ */
    import junit.framework.TestCase;
/* $endif$ */

import com.ustadmobile.core.controller.CatalogEntryInfo;

/**
 *
 * @author mike
 */
public class TestCatalogEntryInfo extends TestCase {
    
    public void testCatalogEntryInfo() {
        CatalogEntryInfo testInfo = new CatalogEntryInfo();
        testInfo.acquisitionStatus = CatalogEntryInfo.ACQUISITION_STATUS_ACQUIRED;
        testInfo.srcURLs = new String[]{"http://www.server1.com/file.epub",
            "http://www.server2.com/file.epub"};
        testInfo.fileURI = "/some/file/path/file.epub";
        testInfo.mimeType = "application/epub+zip";
        
        String infoStr = testInfo.toString();
        CatalogEntryInfo restoreEntry = CatalogEntryInfo.fromString(infoStr);
        
        assertEquals(testInfo.acquisitionStatus, restoreEntry.acquisitionStatus);
        assertEquals(testInfo.srcURLs[0], restoreEntry.srcURLs[0]);
        assertEquals(testInfo.srcURLs[1], restoreEntry.srcURLs[1]);
        assertEquals(testInfo.fileURI, restoreEntry.fileURI);
        assertEquals(testInfo.mimeType, restoreEntry.mimeType);
    }

    public void runTest(){
        this.testCatalogEntryInfo();
    }
    
}
