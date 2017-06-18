package com.ustadmobile.test.sharedse;

import com.ustadmobile.core.impl.HTTPResult;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.port.sharedse.impl.UstadMobileSystemImplSE;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;
import com.ustadmobile.core.networkmanager.NetworkManagerListener;
import com.ustadmobile.core.networkmanager.NetworkNode;
import com.ustadmobile.core.networkmanager.NetworkTask;
import com.ustadmobile.test.core.buildconfig.TestConstants;
import com.ustadmobile.test.core.impl.PlatformTestUtil;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.ustadmobile.test.core.buildconfig.TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by kileha3 on 16/05/2017.
 */

public class TestNetworkManager {
    public static final int NODE_DISCOVERY_TIMEOUT =(2*60 * 1000)+2000;//2min2sec in ms

    @Test
    public void testWifiDirectDiscovery() throws IOException{
        NetworkManager manager= UstadMobileSystemImplSE.getInstanceSE().getNetworkManager();

        Assume.assumeTrue("Network test is enabled: wifi and bluetooth enabled",
            manager.isBluetoothEnabled() && manager.isWiFiEnabled());

        Assert.assertTrue("Bluetooth enabled : required to test discovery", manager.isBluetoothEnabled());
        Assert.assertTrue("WiFi enabled: required to test discovery", manager.isWiFiEnabled());

        final Object nodeDiscoveryLock = new Object();
        NetworkManagerListener responseListener = new NetworkManagerListener() {
            @Override
            public void fileStatusCheckInformationAvailable(List<String> fileIds) {

            }

            @Override
            public void networkTaskCompleted(NetworkTask task) {

            }

            @Override
            public void networkNodeDiscovered(NetworkNode node) {
                if(node.getDeviceBluetoothMacAddress()!=null && node.getDeviceBluetoothMacAddress().equals(
                        TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE)){
                    synchronized (nodeDiscoveryLock){
                        nodeDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void networkNodeUpdated(NetworkNode node) {
                if(node.getDeviceBluetoothMacAddress()!=null && node.getDeviceBluetoothMacAddress().equals(
                        TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE)){
                    synchronized (nodeDiscoveryLock){
                        nodeDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

            }

            @Override
            public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

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

        if(manager  .getNodeByBluetoothAddr(TEST_REMOTE_BLUETOOTH_DEVICE) == null) {
            synchronized (nodeDiscoveryLock) {
                try { nodeDiscoveryLock.wait(NODE_DISCOVERY_TIMEOUT ); }
                catch(InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        //disable supernode mode on the remote test device
        String disableNodeUrl =PlatformTestUtil.getRemoteTestEndpoint()+  "?cmd=SUPERNODE&enabled=false";
        result = UstadMobileSystemImpl.getInstance().makeRequest(disableNodeUrl, null, null);
        Assert.assertEquals("Supernode mode reported as enabled", 200, result.getStatus());

        Assert.assertNotNull("Remote test slave node discovered",
                manager.getNodeByBluetoothAddr(TestConstants.TEST_REMOTE_BLUETOOTH_DEVICE));
        manager.removeNetworkManagerListener(responseListener);

    }


    /**
     * Test using the standard network service discovery.
     *
     * Devices *MUST* be on the same network to be able to receive udp broadcasts from each other.
     *
     * Some older versions of Android have buggy implementations of Network Service Discovery; see
     *  https://issuetracker.google.com/issues/36952181
     *
     * @throws IOException
     */
    @Test
    public void testNetworkServiceDiscovery() throws IOException{
        NetworkManager manager= UstadMobileSystemImplSE.getInstanceSE().getNetworkManager();

        Assume.assumeTrue("Network test wifi and bluetooth enabled",
                manager.isBluetoothEnabled() && manager.isWiFiEnabled());

        //enable supernode mode on the remote test device
        String enableNodeUrl = PlatformTestUtil.getRemoteTestEndpoint() + "?cmd=SUPERNODE&enabled=true";
        HTTPResult result = UstadMobileSystemImpl.getInstance().makeRequest(enableNodeUrl, null, null);
        Assert.assertEquals("Supernode mode reported as enabled", 200, result.getStatus());


        final Object nodeNSDiscoveryLock = new Object();
        NetworkManagerListener responseListener = new NetworkManagerListener() {
            @Override
            public void fileStatusCheckInformationAvailable(List<String> fileIds) {

            }

            @Override
            public void networkTaskCompleted(NetworkTask task) {

            }

            @Override
            public void networkNodeDiscovered(NetworkNode node) {
                if(node.getDeviceIpAddress().equals(SharedSeTestSuite.REMOTE_SLAVE_SERVER) &&
                    (Calendar.getInstance().getTimeInMillis()-node.getNetworkServiceLastUpdated()) < NODE_DISCOVERY_TIMEOUT){
                    synchronized (nodeNSDiscoveryLock){
                        nodeNSDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void networkNodeUpdated(NetworkNode node) {
                if(node.getDeviceIpAddress().equals(SharedSeTestSuite.REMOTE_SLAVE_SERVER) &&
                    (Calendar.getInstance().getTimeInMillis()-node.getNetworkServiceLastUpdated()) < NODE_DISCOVERY_TIMEOUT){
                    synchronized (nodeNSDiscoveryLock){
                        nodeNSDiscoveryLock.notify();
                    }
                }
            }

            @Override
            public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

            }

            @Override
            public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

            }

            @Override
            public void wifiDirectConnected(boolean isDeviceConnected) {

            }



        };
        manager.addNetworkManagerListener(responseListener);

        NetworkNode node =manager.getNodeByIpAddress(SharedSeTestSuite.REMOTE_SLAVE_SERVER);
        long timeSinceNsd =(Calendar.getInstance().getTimeInMillis() - (node != null ? node.getNetworkServiceLastUpdated() : 0));
        if(node == null || timeSinceNsd > NODE_DISCOVERY_TIMEOUT) {
            synchronized (nodeNSDiscoveryLock) {
                try { nodeNSDiscoveryLock.wait(NODE_DISCOVERY_TIMEOUT ); }
                catch(InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        node=manager.getNodeByIpAddress(SharedSeTestSuite.REMOTE_SLAVE_SERVER);
        Assert.assertNotNull("Remote test slave node discovered via Network Service Discovery", node);
        boolean isWithinDiscoveryTimeRange=
                (Calendar.getInstance().getTimeInMillis()-node.getNetworkServiceLastUpdated()) < NODE_DISCOVERY_TIMEOUT;
        Assert.assertThat("Was node discovered withing time range",isWithinDiscoveryTimeRange,is(true));
        manager.removeNetworkManagerListener(responseListener);
    }




}
