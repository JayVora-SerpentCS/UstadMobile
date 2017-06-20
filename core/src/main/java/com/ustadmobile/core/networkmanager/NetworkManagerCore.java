package com.ustadmobile.core.networkmanager;

import com.ustadmobile.core.opds.UstadJSOPDSFeed;

/**
 * Created by kileha3 on 13/02/2017.
 */

public interface NetworkManagerCore {

    void setSuperNodeEnabled(Object context,boolean enabled);

    UstadJSOPDSFeed requestAcquisition(UstadJSOPDSFeed feed, Object mContext, boolean localNetworkEnabled, boolean wifiDirectEnabled);

    UstadJSOPDSFeed requestAcquisition(UstadJSOPDSFeed feed, Object mContext, boolean localNetworkEnabled, boolean wifiDirectEnabled,boolean isContentSharingEnabled);

    void addAcquisitionTaskListener(AcquisitionListener listener);

    void removeAcquisitionTaskListener(AcquisitionListener listener);

    void shareAppSetupFile(String filePath, String shareTitle);

    long requestFileStatus(String[] entryIds, boolean useBluetooth, boolean useHttp);

    void addNetworkManagerListener(NetworkManagerListener listener);

    void removeNetworkManagerListener(NetworkManagerListener listener);

    EntryCheckResponse getEntryResponseWithLocalFile(String entryId);

    void removeNotification(int notificationType);

    void removeWiFiDirectGroup();

    UstadJSOPDSFeed acquireOPDSFeedFromPeer(String srcURL,String destURL);

    void setSharedFeed(String [] entries);

    void setP2PConnectedNode(NetworkNode node);

    void connectWifiDirect(String wifiMACAddress);

}
