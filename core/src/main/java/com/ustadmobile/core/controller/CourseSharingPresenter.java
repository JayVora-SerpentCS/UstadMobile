package com.ustadmobile.core.controller;

import com.ustadmobile.core.MessageIDConstants;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.AcquisitionListener;
import com.ustadmobile.core.networkmanager.AcquisitionTaskStatus;
import com.ustadmobile.core.networkmanager.NetworkManagerCore;
import com.ustadmobile.core.networkmanager.NetworkManagerListener;
import com.ustadmobile.core.networkmanager.NetworkNode;
import com.ustadmobile.core.networkmanager.NetworkTask;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.core.view.CourseSharingView;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by kileha3 on 19/06/2017.
 */

public class CourseSharingPresenter extends BaseCatalogController implements NetworkManagerListener,AcquisitionListener {

    private CourseSharingView mView;

    private Hashtable args;

    private NetworkManagerCore networkManager;

    private String [] entries;

    public static final String ARG_SHARED_ENTRIES="course_entries";

    public static final Character ARRAY_SEPARATOR=',';



    public CourseSharingPresenter(Object context, CourseSharingView view, Hashtable args){
        super(context);
        this.mView =view;
        this.args=args;
    }


    public void onCreate(){
        networkManager= UstadMobileSystemImpl.getInstance().getNetworkManager();
        if(this.args!=null && this.args.containsKey(ARG_SHARED_ENTRIES)) {
            entries= UMFileUtil.splitString(args.get(ARG_SHARED_ENTRIES).toString(),ARRAY_SEPARATOR);
            networkManager.setSharedFeed(entries);
        }
        networkManager.addNetworkManagerListener(this);
        networkManager.addAcquisitionTaskListener(this);

    }

    public void handleClickConnectWiFiDirect(String wifiMACAddress){
        networkManager.connectWifiDirect(wifiMACAddress);
    }


    public void handleUserConfirmation(UstadJSOPDSFeed feed, boolean startDownload,int notificationType){
        if(startDownload){
            networkManager.removeNotification(notificationType);
            networkManager.requestAcquisition(feed,this,false,false,true);
        }else{
            networkManager.removeWiFiDirectGroup();
        }
    }

    public void handleViewVisibilityOnDeviceConnected(boolean visibility){
        mView.setViewsVisibility(visibility);
    }

    public void handleDownloadFeed(final String srcURL, final String destURL){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UstadJSOPDSFeed ustadJSOPDSFeed=networkManager.acquireOPDSFeedFromPeer(srcURL,destURL);
                if(ustadJSOPDSFeed!=null){
                    mView.showConfirmationDialog(ustadJSOPDSFeed);
                }else{
                    mView.showErrorMessage(UstadMobileSystemImpl.getInstance().getString(MessageIDConstants.somethingWentWrong));
                }
            }
        }).start();
    }



    public void handleOnDeviceConnected(){
        mView.acquireOPDSFeed();
    }


    public void handleDeviceModesSelection(){
        mView.setDeviceMode(isReceiverDevice());
    }

    public boolean isReceiverDevice(){
        return entries==null;
    }
    public void onDestroy() {
        networkManager.removeNetworkManagerListener(this);
        networkManager.removeAcquisitionTaskListener(this);
    }

    @Override
    public void setUIStrings() {

    }

    @Override
    protected void onDownloadStarted() {
    }

    @Override
    protected void onEntriesRemoved() {

    }

    @Override
    public void acquisitionProgressUpdate(String entryId, AcquisitionTaskStatus status) {
        int progress=(int)((status.getDownloadedSoFar()*100)/ status.getTotalSize());
        mView.setReceivingProgress(progress,status.getEntryTitle());
        mView.setReceivingViewVisibility(progress >= 0 && progress < 100);
    }

    @Override
    public void acquisitionStatusChanged(String entryId, AcquisitionTaskStatus status) {

    }

    @Override
    public void fileStatusCheckInformationAvailable(List<String> fileIds) {

    }

    @Override
    public void networkTaskCompleted(NetworkTask task) {
        mView.setReceivingViewVisibility(false);
        handleUserConfirmation(null,false, 1);
    }

    @Override
    public void networkNodeDiscovered(NetworkNode node) {
        mView.refreshDeviceList();
    }

    @Override
    public void networkNodeUpdated(NetworkNode node) {

    }

    @Override
    public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

    }

    @Override
    public void wifiDirectConnected(boolean isDeviceConnected) {
        mView.handleOnDeviceStateChanged(isDeviceConnected);
    }

    @Override
    public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

    }
}
