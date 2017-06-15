package com.ustadmobile.core.view;

import com.ustadmobile.core.opds.UstadJSOPDSFeed;

/**
 * Created by kileha3 on 10/06/2017.
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
    void setReceivingProgress(int progress);

    /**
     * Method responsible for setting up current connected device name na d address
     * @param deviceName Device name
     * @param deviceAddress Device Wifi MAC Address
     */
    void setConnectedDeviceInfo(String deviceName,String deviceAddress);

    /**
     * Method to handle when user terminate file sharing process
     */
    void handleUserCancelDownloadTask();
    void handleUserStartReceivingTask(UstadJSOPDSFeed feed);
    void handleAcquireOPDSFeed();

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

}
