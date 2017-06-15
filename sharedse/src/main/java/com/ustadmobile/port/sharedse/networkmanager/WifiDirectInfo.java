package com.ustadmobile.port.sharedse.networkmanager;

/**
 * <h1>WifiDirectInfo</h1>
 *
 * This is a class which define Wi-Fi Connection information in a cross platform way.
 *
 * @author kileha3
 */

public class WifiDirectInfo {
    private boolean isGroupOwner;
    private boolean isDeviceConnected;

    /**
     * Create Wifi Direct Information
     * @param isGroupOwner TRUE when the device is a group owner otherwise FALSE
     * @param isDeviceConnected TRUE when connection made successful otherwise FALSE
     */
    public WifiDirectInfo(boolean isGroupOwner,boolean isDeviceConnected){
        this.isGroupOwner=isGroupOwner;
        this.isDeviceConnected=isDeviceConnected;
    }

    /**
     * Method which is responsible to get group owner status
     * @return boolean: Group owner status
     */
    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    /**
     * Method responsible for determining if the connection was made successfully
     * @return boolean : Connection status
     */
    public boolean isDeviceConnected() {
        return isDeviceConnected;
    }
}
