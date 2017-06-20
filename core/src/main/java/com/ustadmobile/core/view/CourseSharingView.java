package com.ustadmobile.core.view;

import com.ustadmobile.core.opds.UstadJSOPDSFeed;

/**
 * <h1>CourseSharingView</h1>
 *
 * This is an interface responsible to handle all operations within CourseSharingActivity.
 *
 * @author kileha3
 *
 * @see com.ustadmobile.core.view.UstadView
 */

public interface CourseSharingView extends UstadView{

    String VIEW_NAME = "CourseSharing";

    /**
     * Set title to be displayed on the toolbar on different events
     * @param title title to set
     */
    void setToolbarTitle(String title);

    /**
     * Method responsible to set the file title when receiving started
     * @param title File title
     */
    void setFileTitle(String title);

    /**
     * Method responsible for setting up progress when receiving file.
     * @param progress file progress
     */
    void setReceivingProgress(int progress,String title);

    /**
     * Method responsible for setting up current connected device name na d address
     * @param deviceName Device name
     * @param deviceAddress Device Wifi MAC Address
     */
    void setConnectedDeviceInfo(String deviceName,String deviceAddress);
    /**
     * Method responsible to acquire an OPDS feed from peer device
     */
    void acquireOPDSFeed();

    /**
     * Show dialog for the user to choose whether to download it or not
     * @param feed OPDS feed contain files to be received
     */
    void showConfirmationDialog(UstadJSOPDSFeed feed);

    /**
     * Method which is responsible to handle view visibility when device
     * is connected/disconnected from its peer
     * @param connected TRUE when connected otherwise FALSE
     */
    void setViewsVisibility(boolean connected);

    /**
     * Set file receiving views visibility (ProgressBar and Course title views)
     * @param visible Visible when TRUE otherwise FALSE
     */
    void setReceivingViewVisibility(boolean visible);

    /**
     * Method responsible to set device sharing mode (Sender or Receiver)
     * @param isReceiverDevice TRUE when receiver FALSE otherwise
     */
    void setDeviceMode(boolean isReceiverDevice);

    /**
     * Method to handle device change states
     * @param isConnected TRUE when WiFi Direct is connected otherwise FALSE
     */
    void handleOnDeviceStateChanged(boolean isConnected);

    /**
     * Refresh device list on addition or remove of a device
     */
    void refreshDeviceList();

    void showErrorMessage(String message);

}
