package com.ustadmobile.test.sharedse;

import com.ustadmobile.core.controller.CatalogController;
import com.ustadmobile.core.impl.HTTPResult;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.NetworkManagerListener;
import com.ustadmobile.core.networkmanager.NetworkNode;
import com.ustadmobile.core.networkmanager.NetworkTask;
import com.ustadmobile.port.sharedse.impl.UstadMobileSystemImplSE;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;
import com.ustadmobile.test.core.impl.PlatformTestUtil;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.ustadmobile.test.core.buildconfig.TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE;

/**
 * Created by kileha3 on 18/06/2017.
 */

public class TestP2PShareFileTask {
    public static final String ENTRY_ID="a1d9f486-312a-41d5-bf13-bc83127ffc7f";

    private final Object nodeDiscoveryLock=new Object();

    private final Object wifiConnectionLock=new Object();

    private boolean isWiFiDirectConnected=false;


    @Test
    public void testP2pFileSharing() throws IOException, InterruptedException {
        NetworkManager manager= UstadMobileSystemImplSE.getInstanceSE().getNetworkManager();

        Assume.assumeTrue("Network test is enabled: wifi and bluetooth enabled",
                manager.isBluetoothEnabled() && manager.isWiFiEnabled());

        Assert.assertTrue("Bluetooth enabled : required to test discovery", manager.isBluetoothEnabled());
        Assert.assertTrue("WiFi enabled: required to test discovery", manager.isWiFiEnabled());

        NetworkManagerListener managerListener=new NetworkManagerListener() {
            @Override
            public void fileStatusCheckInformationAvailable(List<String> fileIds) {

            }

            @Override
            public void networkTaskCompleted(NetworkTask task) {

            }

            @Override
            public void networkNodeDiscovered(NetworkNode node) {
                if(node.getDeviceBluetoothMacAddress().equals(TEST_REMOTE_BLUETOOTH_DEVICE)){
                    synchronized (nodeDiscoveryLock){
                        nodeDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void networkNodeUpdated(NetworkNode node) {

            }

            @Override
            public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

            }

            @Override
            public void wifiDirectConnected(boolean isDeviceConnected) {
                isWiFiDirectConnected=isDeviceConnected;
                synchronized (wifiConnectionLock){
                    wifiConnectionLock.notify();
                }
            }

            @Override
            public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

            }
        };

        manager.addNetworkManagerListener(managerListener);

        //Switch ON client mode, prepare remote device to receive file
        String enableNodeUrl = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=RECEIVEFILE&enabled=true";
        HTTPResult result = UstadMobileSystemImpl.getInstance().makeRequest(enableNodeUrl, null, null);
        Assert.assertEquals("Client mode switched on ready to receive files", 200, result.getStatus());

        manager.setSharedFeed(new String[]{ENTRY_ID});
        manager.startContentSharing(UstadMobileSystemImpl.getInstance().getDeviceName(manager.getContext()),false);

        if(manager.getNodeByBluetoothAddr(TEST_REMOTE_BLUETOOTH_DEVICE)==null){
            synchronized (nodeDiscoveryLock){
                nodeDiscoveryLock.wait(TestNetworkManager.NODE_DISCOVERY_TIMEOUT);
            }
        }

        Assert.assertNotNull("Client node to receive file was discovered", manager.getNodeByBluetoothAddr(TEST_REMOTE_BLUETOOTH_DEVICE));
        manager.connectWifiDirect(manager.getNodeByBluetoothAddr(TEST_REMOTE_BLUETOOTH_DEVICE).getDeviceWifiDirectMacAddress());
        synchronized (wifiConnectionLock){
            wifiConnectionLock.wait(TestNetworkManager.NODE_DISCOVERY_TIMEOUT);
        }
        Assert.assertTrue("Client node was connected successfully", isWiFiDirectConnected);

        //Send request to the remote device to download a file
        String srcURL="http://192.168.49.1:"+manager.getHttpListeningPort()+"/catalog/entry/"+ENTRY_ID;
        String destURL=UstadMobileSystemImpl.getInstance().getStorageDirs(
                CatalogController.SHARED_RESOURCE, PlatformTestUtil.getTargetContext())[0].getDirURI()+"/"+ENTRY_ID;
        String acquireFile = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=ACQUIREFILE&src="+srcURL+"&dest="+destURL;
        HTTPResult acquireResults = UstadMobileSystemImpl.getInstance().makeRequest(acquireFile, null, null);
        Assert.assertEquals("File was acquired successfully", 200, acquireResults.getStatus());

        //Switch OFF client mode, no file will be received
        String disableURL = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=RECEIVEFILE&enabled=false";
        HTTPResult disableResult = UstadMobileSystemImpl.getInstance().makeRequest(disableURL, null, null);
        Assert.assertEquals("Client mode switched OFF, no file can be received", 200, disableResult.getStatus());

    }
}
