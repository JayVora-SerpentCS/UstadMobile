package com.ustadmobile.test.sharedse;

import com.ustadmobile.core.controller.CatalogController;
import com.ustadmobile.core.controller.CatalogEntryInfo;
import com.ustadmobile.core.impl.AcquisitionManager;
import com.ustadmobile.core.impl.HTTPResult;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.AcquisitionTaskStatus;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import com.ustadmobile.core.opds.UstadJSOPDSItem;
import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.port.sharedse.impl.UstadMobileSystemImplSE;
import com.ustadmobile.core.networkmanager.AcquisitionListener;
import com.ustadmobile.port.sharedse.networkmanager.AcquisitionTask;
import com.ustadmobile.port.sharedse.networkmanager.AcquisitionTaskHistoryEntry;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManagerListener;
import com.ustadmobile.port.sharedse.networkmanager.NetworkNode;
import com.ustadmobile.port.sharedse.networkmanager.NetworkTask;
import com.ustadmobile.port.sharedse.networkmanager.WifiDirectInfo;
import com.ustadmobile.test.core.buildconfig.TestConstants;
import com.ustadmobile.test.core.impl.ClassResourcesResponder;
import com.ustadmobile.test.core.impl.PlatformTestUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fi.iki.elonen.router.RouterNanoHTTPD;

import static org.hamcrest.CoreMatchers.is;

/**
 * Test the acquisition task. The OPDS feed on which the acquisition is based and the EPUBs are in
 * the resources of the sharedse-tests module.
 *
 * Created by kileha3 on 17/05/2017.
 */
public class TestAcquisitionTask{
    private static final int DEFAULT_WAIT_TIME =20000;
    private static final String FEED_LINK_MIME ="application/dir";
    private static final String ENTRY_ID_PRESENT ="202b10fe-b028-4b84-9b84-852aa766607d";
    private static final String ENTRY_ID_NOT_PRESENT = "b649852e-2bf9-45ab-839e-ec5bb00ca19d";
    public static final String[] ENTRY_IDS = new String[]{ENTRY_ID_PRESENT,ENTRY_ID_NOT_PRESENT};

    private ArrayList<HashMap<String,Integer>> downloadSources=new ArrayList<>();

    private static RouterNanoHTTPD resourcesHttpd;

    private boolean localNetworkEnabled=false;

    private boolean wifiDirectEnabled=false;

    /**
     * The resources server can be used as the "cloud"
     */
    private static String httpRoot;

    @BeforeClass
    public static void startHttpResourcesServer() throws IOException {
        if(resourcesHttpd == null) {
            resourcesHttpd = new RouterNanoHTTPD(0);
            resourcesHttpd.addRoute("/res/(.*)", ClassResourcesResponder.class, "/res/");
            resourcesHttpd.start();
            httpRoot = "http://localhost:" + resourcesHttpd.getListeningPort() + "/res/";
        }
    }

    @AfterClass
    public static void stopHttpResourcesServer() throws IOException {
        if(resourcesHttpd != null) {
            resourcesHttpd.stop();
            resourcesHttpd = null;
        }
    }

    @Test
    public void testAcquisition() throws IOException, InterruptedException, XmlPullParserException {
        final NetworkManager manager= UstadMobileSystemImplSE.getInstanceSE().getNetworkManager();
        Assume.assumeTrue("Network test wifi and bluetooth enabled",
                manager.isBluetoothEnabled() && manager.isWiFiEnabled());

        final boolean[] fileAvailable = new boolean[ENTRY_IDS.length];
        final Object nodeDiscoveryLock = new Object();
        final Object statusRequestLock=new Object();
        final Object acquireSameNetworkLock=new Object();
        final Object acquireDifferentNetworkLock=new Object();

        //make sure we don't have any of the entries in question already
        CatalogController.removeEntry(ENTRY_ID_PRESENT, CatalogController.SHARED_RESOURCE,
            PlatformTestUtil.getTargetContext());
        CatalogController.removeEntry(ENTRY_ID_NOT_PRESENT, CatalogController.SHARED_RESOURCE,
                PlatformTestUtil.getTargetContext());

        NetworkManagerListener responseListener = new NetworkManagerListener() {
            @Override
            public void fileStatusCheckInformationAvailable(List<String> fileIds) {
                for(int i = 0; i < ENTRY_IDS.length; i++){
                    if(fileIds.contains(ENTRY_IDS[i])) {
                        fileAvailable[i] = manager.isFileAvailable(ENTRY_IDS[i]);
                    }
                }
            }

            @Override
            public void entryStatusCheckCompleted(NetworkTask task) {
                synchronized (statusRequestLock){
                    statusRequestLock.notify();
                }
            }

            @Override
            public void networkNodeDiscovered(NetworkNode node) {
                if(node.getDeviceBluetoothMacAddress() != null &&
                        node.getDeviceBluetoothMacAddress().equals(
                                TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE)){

                    synchronized (nodeDiscoveryLock){
                        nodeDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void networkNodeUpdated(NetworkNode node) {

            }

            @Override
            public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int sources) {
                HashMap<String,Integer> sourceData=new HashMap<>();
                sourceData.put(entryId,sources);
                if(!downloadSources.contains(sourceData)){
                    downloadSources.add(sourceData);
                }
            }

            @Override
            public void wifiConnectionChanged(String ssid) {

            }

            @Override
            public void wifiDirectConnected(boolean isDeviceConnected) {

            }
        };
        manager.addNetworkManagerListener(responseListener);
        //enable supernode mode on the remote test device
        String enableNodeUrl = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=SUPERNODE&enabled=true";
        HTTPResult result = UstadMobileSystemImpl.getInstance().makeRequest(enableNodeUrl, null, null);
        Assert.assertEquals("Supernode mode reported as enabled", 200, result.getStatus());
        if(manager.getNodeByBluetoothAddr(TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE)==null){
            synchronized (nodeDiscoveryLock){
                nodeDiscoveryLock.wait(TestNetworkManager.NODE_DISCOVERY_TIMEOUT);
            }
        }

        final NetworkNode networkNode=manager.getNodeByBluetoothAddr(TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE);
        Assert.assertNotNull("Remote test slave node discovered", networkNode);

        List<NetworkNode> nodeList=new ArrayList<>();
        nodeList.add(networkNode);

        List<String> entryLIst=new ArrayList<>();
        Collections.addAll(entryLIst, ENTRY_IDS);
        manager.requestFileStatus(entryLIst,manager.getContext(),nodeList, true, false);
        synchronized (statusRequestLock){
            statusRequestLock.wait(DEFAULT_WAIT_TIME*2);
        }


        Assert.assertTrue("Available entry reported as locally available", fileAvailable[0]);
        Assert.assertFalse("Unavailable entry reported as not available",  fileAvailable[1]);

        //Create a feed manually
        String catalogUrl = UMFileUtil.joinPaths(new String[]{
                httpRoot, "com/ustadmobile/test/sharedse/test-acquisition-task-feed.opds"});
        UstadJSOPDSFeed feed = CatalogController.getCatalogByURL(catalogUrl,
            CatalogController.SHARED_RESOURCE, null, null, 0, PlatformTestUtil.getTargetContext());

        String destinationDir= UstadMobileSystemImpl.getInstance().getStorageDirs(
            CatalogController.SHARED_RESOURCE, PlatformTestUtil.getTargetContext())[0].getDirURI();
        feed.addLink(AcquisitionManager.LINK_REL_DOWNLOAD_DESTINATION,
            FEED_LINK_MIME, destinationDir);
        feed.addLink(UstadJSOPDSItem.LINK_REL_SELF_ABSOLUTE, UstadJSOPDSItem.TYPE_ACQUISITIONFEED,
            catalogUrl);

        AcquisitionListener acquisitionListener =new AcquisitionListener() {
            @Override
            public void acquisitionProgressUpdate(String entryId, AcquisitionTaskStatus status) {

            }

            @Override
            public void acquisitionStatusChanged(String entryId, AcquisitionTaskStatus status) {
                if (status.getStatus() == UstadMobileSystemImpl.DLSTATUS_SUCCESSFUL) {
                    //The entries are downloaded in the order in which they are requested -
                    // which is ENTRY_ID, ENTRY_ID_NOT_PRESENT
                    //Therefor when we receive the complete event for the latter we can notify the
                    //thread to continue.
                    if (localNetworkEnabled && entryId.equals(ENTRY_ID_NOT_PRESENT)) {
                        synchronized (acquireSameNetworkLock) {
                            acquireSameNetworkLock.notifyAll();
                        }
                    }

                }
            }
        };

        manager.addAcquisitionTaskListener(acquisitionListener);


        localNetworkEnabled=true;
        wifiDirectEnabled=false;
        manager.requestAcquisition(feed,manager.getContext(),localNetworkEnabled,wifiDirectEnabled);
        AcquisitionTask task = manager.getAcquisitionTaskByEntryId(ENTRY_ID_PRESENT);
        Assert.assertNotNull("Task created for acquisition", task);
        synchronized (acquireSameNetworkLock){
            acquireSameNetworkLock.wait(DEFAULT_WAIT_TIME* 6);
        }

        List<AcquisitionTaskHistoryEntry> entryHistoryList = task.getAcquisitionHistoryByEntryId(ENTRY_ID_PRESENT);
        for(AcquisitionTaskHistoryEntry entryHistory : entryHistoryList) {
            Assert.assertTrue("Task reported as being downloaded from same network",
                    entryHistory.getMode() == NetworkManager.DOWNLOAD_FROM_PEER_ON_SAME_NETWORK);
        }

        CatalogEntryInfo localEntryInfo = CatalogController.getEntryInfo(ENTRY_ID_PRESENT,
                CatalogController.SHARED_RESOURCE, PlatformTestUtil.getTargetContext());
        Assert.assertEquals("File was downloaded successfully from node on same network",
            CatalogController.STATUS_ACQUIRED, localEntryInfo.acquisitionStatus);
        Assert.assertTrue("File downloaded via local network is present",
                new File(localEntryInfo.fileURI).exists());

        CatalogEntryInfo cloudEntryInfo = CatalogController.getEntryInfo(ENTRY_ID_NOT_PRESENT,
                CatalogController.SHARED_RESOURCE, PlatformTestUtil.getTargetContext());
        Assert.assertEquals("File was downloaded successfully from cloud",
                CatalogController.STATUS_ACQUIRED, cloudEntryInfo.acquisitionStatus);
        Assert.assertTrue("File downloaded via cloud is present",
                new File(cloudEntryInfo.fileURI).exists());

        CatalogController.removeEntry(ENTRY_ID_PRESENT, CatalogController.SHARED_RESOURCE,
                PlatformTestUtil.getTargetContext());
        CatalogController.removeEntry(ENTRY_ID_NOT_PRESENT, CatalogController.SHARED_RESOURCE,
                PlatformTestUtil.getTargetContext());


        /*
        localNetworkEnabled=false;
        wifiDirectEnabled=true;
        manager.requestAcquisition(feed,manager.getContext(),localNetworkEnabled,wifiDirectEnabled);
        AcquisitionTask task = manager.getAcquisitionTaskByEntryId(ENTRY_ID_PRESENT);
        synchronized (acquireDifferentNetworkLock){
            acquireDifferentNetworkLock.wait(DEFAULT_WAIT_TIME*10);
        }

        List<AcquisitionTaskHistoryEntry> entryHistoryList = task.getAcquisitionHistoryByEntryId(ENTRY_ID_PRESENT);
        for(AcquisitionTaskHistoryEntry entryHistory : entryHistoryList) {
            Assert.assertTrue("Task reported as being downloaded from same network",
                    entryHistory.getMode() == NetworkManager.DOWNLOAD_FROM_PEER_ON_DIFFERENT_NETWORK);
        }

        CatalogEntryInfo localEntryInfo = CatalogController.getEntryInfo(ENTRY_ID_PRESENT,
                CatalogController.SHARED_RESOURCE, PlatformTestUtil.getTargetContext());
        Assert.assertEquals("File was downloaded successfully from node on same network",
                CatalogController.STATUS_ACQUIRED, localEntryInfo.acquisitionStatus);
        Assert.assertTrue("File downloaded via local network is present",
                new File(localEntryInfo.fileURI).exists());

        //Assert.assertThat("File was downloaded successfully from node on different network", fileDownloadedFromPeer,is(true));
        //Assert.assertThat("File was downloaded successfully from cloud", fileDownloadedFromCloud,is(true));
        */


        String disableNodeUrl = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=SUPERNODE&enabled=false";
        result = UstadMobileSystemImpl.getInstance().makeRequest(disableNodeUrl, null, null);
        Assert.assertEquals("Supernode mode reported as enabled", 200, result.getStatus());
    }
}
