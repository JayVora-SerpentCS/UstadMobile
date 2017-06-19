package com.ustadmobile.port.android.view;

import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toughra.ustadmobile.R;
import com.ustadmobile.core.MessageIDConstants;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.AcquisitionListener;
import com.ustadmobile.core.networkmanager.AcquisitionTaskStatus;
import com.ustadmobile.core.networkmanager.NetworkManagerListener;
import com.ustadmobile.core.networkmanager.NetworkNode;
import com.ustadmobile.core.networkmanager.NetworkTask;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.core.view.CourseSharingView;
import com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid;
import com.ustadmobile.port.android.netwokmanager.WifiDirectAutoAccept;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid.PREF_KEY_RECEIVE_CONTENT;

/**
 * <h1>CourseSharingActivity</h1>
 *
 * This is a class responsible for all content sharing operations.
 * In here both content sending and content receiving are being handled.
 * <p>
 *     <b>How it works</b>
 *     We have two sides, one side will be receiving content and
 *     another one will be sending those contents.
 *
 *     The side that will be receiving the content has to advertise
 *     its service and wait for any incoming connection once is connected
 *     will have to confirm shared content and start download them.
 *
 *     The side that will be sending the content has to select content to be shared,
 *     discover all services and choose specific device to share those content with.
 *
 * </p>
 *
 *
 * @see CourseSharingView
 * @see NetworkManagerListener
 * @see UstadBaseActivity
 *
 * @author kileha3
 */

public class CourseSharingActivity extends UstadBaseActivity  implements CourseSharingView,NetworkManagerListener, CompoundButton.OnCheckedChangeListener, AcquisitionListener {

    private RecyclerView deviceRecyclerView;

    private DeviceAdapter deviceAdapter;

    private ProgressBar waitingProgressBar;

    private NetworkManagerAndroid managerAndroid;

    private TextView toolbarTitle,connectedNetworkName,connectedDeviceAddress,receivingFileNameView;

    private CardView connectedDeviceView;

    private ProgressBar fileReceivingProgressBar;

    private UstadMobileSystemImpl impl;

    private NetworkNode connectedNode =null;

    private boolean isReceiverDevice =false;

    private boolean isSharedFileDialogShown=false;

    private List<NetworkNode> deviceList=new Vector<>();

    private static final String DEVICE_PHONE_TAG="[Phone]";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sharing);
        setUMToolbar(R.id.course_sharing_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        impl=UstadMobileSystemImpl.getInstance();

        managerAndroid=((NetworkManagerAndroid)impl.getNetworkManager());

        SwitchCompat switchCompatReceive = (SwitchCompat) findViewById(R.id.receive_share_content);
        deviceRecyclerView = (RecyclerView) findViewById(R.id.all_found_nodes_to_share_course);
        fileReceivingProgressBar = (ProgressBar) findViewById(R.id.fileReceivingProgressBar);
        waitingProgressBar = (ProgressBar) findViewById(R.id.progressBarWaiting);


        toolbarTitle= (TextView) findViewById(R.id.toolbarTitle);
        connectedDeviceAddress= (TextView) findViewById(R.id.device_address);
        connectedNetworkName= (TextView) findViewById(R.id.network_name);
        receivingFileNameView= (TextView) findViewById(R.id.receivingFileNameView);
        connectedDeviceView = (CardView) findViewById(R.id.connected_device_details_holder);

        deviceAdapter=new DeviceAdapter();


        LinearLayoutManager mLayoutManager=new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceRecyclerView.setLayoutManager(mLayoutManager);
        deviceRecyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setNodeList(new ArrayList<NetworkNode>());
        managerAndroid.addNetworkManagerListener(this);
        managerAndroid.addAcquisitionTaskListener(this);
        switchCompatReceive.setOnCheckedChangeListener(this);

        //enable sharing mode
        managerAndroid.setContentSharingEnabled(true);
        //get sharing device mode
        boolean isReceiverDevice = Boolean.parseBoolean(UstadMobileSystemImpl.getInstance().getAppPref(
                PREF_KEY_RECEIVE_CONTENT, "true",this));
        if(isReceiverDevice){
            switchCompatReceive.setChecked(true);
        }else{
            handleDeviceModeSelected(false);
        }

        //Intercept WiFi Direct popup
        new WifiDirectAutoAccept(this).intercept(true);

    }


    @Override
    public void onDestroy() {
        if(managerAndroid!=null){
            managerAndroid.setContentSharingEnabled(false);
            if(managerAndroid.isSuperNodeEnabled()){
               managerAndroid.setSuperNodeEnabled(this,true);
            }
        }
        UstadMobileSystemImpl.getInstance().setAppPref(PREF_KEY_RECEIVE_CONTENT,"true",this);
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                UstadMobileSystemImpl.getInstance().setAppPref(PREF_KEY_RECEIVE_CONTENT,"true",this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method responsible to handle device listing,
     * showing discovered devices and their states.
     */
    private void refreshDeviceList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(NetworkNode networkNode:managerAndroid.getKnownNodes()){
                    NetworkNode node=getNetworkNodeByMacAddress(networkNode.getDeviceWifiDirectMacAddress());
                    if(networkNode.getDeviceName()!=null && node==null){
                        networkNode.setStatus(WifiP2pDevice.AVAILABLE);
                        deviceList.add(networkNode);
                    }
                }
                int visibility=deviceList.size()>0 ? View.GONE: View.VISIBLE;
                waitingProgressBar.setVisibility(visibility);
                deviceAdapter.setNodeList(deviceList);
                deviceAdapter.notifyDataSetChanged();
                deviceRecyclerView.invalidate();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isReceiverDevice) {
        handleDeviceModeSelected(isReceiverDevice);
    }

    /**
     * method to handle device sharing mode selection
     * @param isReceiverDevice TRUE when device is set to receiver and false Otherwise
     */
    private void handleDeviceModeSelected(boolean isReceiverDevice){
        if(!isReceiverDevice){
            refreshDeviceList();
        }
        managerAndroid.startContentSharing(impl.getDeviceName(this),isReceiverDevice);
        this.isReceiverDevice =isReceiverDevice;
        String title=isReceiverDevice ? impl.getString(MessageIDConstants.receiveSharedCourse)
                :impl.getString(MessageIDConstants.chooseDeviceToShareWith);
        int deviceListVisibility=isReceiverDevice ? View.GONE: View.VISIBLE;
        deviceRecyclerView.setVisibility(deviceListVisibility);
        managerAndroid.setContentSharingEnabled(true);
        setToolbarTitle(title);
        handleUserCancelDownloadTask();
    }


    @Override
    public void acquisitionProgressUpdate(String entryId, AcquisitionTaskStatus status) {
        int progress=(int)((status.getDownloadedSoFar()*100)/ status.getTotalSize());
        setReceivingProgress(progress,status.getEntryTitle());
    }

    @Override
    public void acquisitionStatusChanged(String entryId, AcquisitionTaskStatus status) {

    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void setFileTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receivingFileNameView.setText(title);
            }
        });
    }


    @Override
    public void setViewsVisibility(boolean visible) {
        //visibility of connected device view on receiver device
        int deviceViewVisibility=visible ? View.VISIBLE: View.GONE;
        connectedDeviceView.setVisibility(deviceViewVisibility);
        if(!visible){
            Toast.makeText(this,impl.getString(MessageIDConstants.somethingWentWrong),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setReceivingViewVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int visibility=visible ? View.VISIBLE:View.GONE;
                fileReceivingProgressBar.setVisibility(visibility);
                receivingFileNameView.setVisibility(visibility);
            }
        });
    }


    @Override
    public void setReceivingProgress(final int progress, final String title) {
        setFileTitle(String.format(impl.getString(MessageIDConstants.receivingFileTitle),title));
        fileReceivingProgressBar.setProgress(progress);
        setReceivingViewVisibility(progress >= 0 && progress<=100);
    }

    @Override
    public void setConnectedDeviceInfo(String deviceName, String deviceAddress) {
        connectedDeviceAddress.setText(deviceAddress);
        connectedNetworkName.setText(deviceName);
    }

    @Override
    public void handleUserCancelDownloadTask() {
        if(managerAndroid.getWifiDirectHandler().getWifiP2pGroup()!=null){
            managerAndroid.getWifiDirectHandler().removeGroup();
        }
    }

    @Override
    public void handleUserStartReceivingTask(UstadJSOPDSFeed feed) {
        managerAndroid.removeNotification(NetworkManager.NOTIFICATION_TYPE_SERVER);
        managerAndroid.requestAcquisition(feed,this,false,false,true);
    }

    @Override
    public void acquireOPDSFeed() {

        if(!isSharedFileDialogShown){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String opdsSrcURL = UMFileUtil.joinPaths(new String[]{"http://" + connectedNode.getDeviceIpAddress() + ":"
                            + connectedNode.getPort(),"/shared/shared.opds"});
                    File opdsDestURL = new File(managerAndroid.getContext().getCacheDir(), "acquire.opds");
                    Log.d(NetworkManagerAndroid.TAG,"OPDS source "+opdsSrcURL);
                    final UstadJSOPDSFeed feed=managerAndroid.acquireOPDSFeedFromPeer(opdsSrcURL,opdsDestURL.getAbsolutePath());
                    if(feed!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConfirmationDialog(feed);
                            }
                        });
                    }
                }
            }).start();
            isSharedFileDialogShown=true;
        }

    }

    @Override
    public void showConfirmationDialog(final UstadJSOPDSFeed opdsFeed) {
        String filesList="";
        String message=opdsFeed.entries.length >1 ?
                opdsFeed.entries.length+String.format(impl.getString(MessageIDConstants.courseListLabel),"s")
                :opdsFeed.entries.length+String.format(impl.getString(MessageIDConstants.courseListLabel),"");
        if(opdsFeed.entries.length>1){
            for(int position=0;position<opdsFeed.entries.length;position++){

                if(position==0){
                    filesList="<br/>"+(position+1)+". "+opdsFeed.entries[position].title;
                }
                if(position>0 && position<opdsFeed.entries.length){
                    filesList=filesList+"<br/>"+(position+1)+". "+opdsFeed.entries[position].title;
                }
            }

        }else{
            filesList=opdsFeed.entries[0].title;
        }
        filesList="<b>"+filesList+"</b>";
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(impl.getString(MessageIDConstants.shareDialogChoiceTitle));
        builder.setMessage(Html.fromHtml(String.format(impl.getString(MessageIDConstants.shareDialogChoiceMessage)
                ,message, connectedNode.getDeviceName()+";-<br/>",filesList+"<br/><br/>")));
        builder.setNegativeButton(impl.getString(MessageIDConstants.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                handleUserCancelDownloadTask();
            }
        });
        builder.setPositiveButton(impl.getString(MessageIDConstants.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                waitingProgressBar.setVisibility(View.GONE);
                handleUserStartReceivingTask(opdsFeed);
            }
        });
        builder.show();
    }


    @Override
    public void fileStatusCheckInformationAvailable(List<String> fileIds) {

    }

    @Override
    public void networkTaskCompleted(NetworkTask task) {
        setReceivingViewVisibility(false);
        handleUserCancelDownloadTask();
    }


    @Override
    public void networkNodeDiscovered(NetworkNode node) {
        refreshDeviceList();

    }

    @Override
    public void networkNodeUpdated(NetworkNode node) {
        refreshDeviceList();
    }

    @Override
    public void fileAcquisitionInformationAvailable(String entryId, long downloadId, int downloadSource) {

    }


    @Override
    public void wifiDirectConnected(boolean isDeviceConnected) {
        String deviceName=null,deviceStatus=null;
        boolean isGroupInfoAvailable=false;
        if(isDeviceConnected){
            WifiP2pInfo wifiP2pInfo=managerAndroid.getWifiDirectHandler().getWifiP2pInfo();
            if(wifiP2pInfo.isGroupOwner && !isReceiverDevice){
                //List all connected clients devices
                WifiP2pGroup wifiP2pGroup=managerAndroid.getWifiDirectHandler().getWifiP2pGroup();
                if(wifiP2pGroup!=null){
                    for(WifiP2pDevice device: wifiP2pGroup.getClientList()){
                        NetworkNode networkNode=getNetworkNodeByMacAddress(device.deviceAddress);
                        if(networkNode!=null){
                            networkNode.setStatus(device.status);
                        }
                    }
                }

            }else{
                try{
                    WifiP2pDevice groupOwner=managerAndroid.getWifiDirectHandler().getWifiP2pGroup().getOwner();
                    if(groupOwner!=null){
                        isGroupInfoAvailable=true;
                        deviceName= !groupOwner.deviceName.contains(DEVICE_PHONE_TAG) ? groupOwner.deviceName:
                                groupOwner.deviceName.substring(DEVICE_PHONE_TAG.length(),groupOwner.deviceName.length());

                        deviceStatus=impl.getString(MessageIDConstants.deviceConnectionStatus)+": "+impl.getString(MessageIDConstants.deviceConnected);
                        String ipAddress=String.valueOf(managerAndroid.getWifiDirectHandler().getWifiP2pInfo().groupOwnerAddress).replace("/","");
                        connectedNode =new NetworkNode(groupOwner.deviceAddress,ipAddress);
                        connectedNode.setPort(managerAndroid.getHttpListeningPort());
                        connectedNode.setDeviceName(deviceName);
                        managerAndroid.setP2PConnectedNode(connectedNode);
                        acquireOPDSFeed();
                    }else{
                        isGroupInfoAvailable=false;
                    }
                    setConnectedDeviceInfo(deviceName,deviceStatus);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        }else{
            //Update device status on disconnecting WiFi Direct
            for(NetworkNode networkNode: deviceList){
                NetworkNode node=getNetworkNodeByMacAddress(networkNode.getDeviceWifiDirectMacAddress());
                node.setStatus(WifiP2pDevice.AVAILABLE);
            }
        }

        refreshDeviceList();
        String title= isDeviceConnected ? impl.getString(MessageIDConstants.deviceConnected) :
                (managerAndroid.isSuperNodeEnabled() ? impl.getString(MessageIDConstants.receiveSharedCourse)
                        : impl.getString(MessageIDConstants.chooseDeviceToShareWith));
        setToolbarTitle(title);
        waitingProgressBar.setVisibility(isDeviceConnected ? View.GONE : View.VISIBLE);
        setViewsVisibility(isDeviceConnected && isReceiverDevice && isGroupInfoAvailable);
    }

    @Override
    public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

    }


    /**
     * Custom Adapter class which handles displaying of all peer to peer sharing discovered devices.
     */
    private class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder>{
        List<NetworkNode> nodeList;
        void setNodeList(List<NetworkNode> nodeList) {
            this.nodeList = nodeList;
        }

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new DeviceViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_device_to_share_content_with_view,parent,false));
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder holder, int position) {
            holder.deviceName.setText(nodeList.get(holder.getAdapterPosition()).getDeviceName().toUpperCase());

            String connectionStatus=nodeList.get(holder.getAdapterPosition()).getStatus()==WifiP2pDevice.CONNECTED ?
                    impl.getString(MessageIDConstants.deviceConnectionStatus)+": "+
                            impl.getString(MessageIDConstants.deviceConnected):
                    impl.getString(MessageIDConstants.deviceConnectionStatus)+": "+
                    impl.getString(MessageIDConstants.deviceAvailable);

            holder.deviceStatus.setText(connectionStatus);

            holder.deviceHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectedNode =nodeList.get(holder.getAdapterPosition());
                    managerAndroid.setP2PConnectedNode(nodeList.get(holder.getAdapterPosition()));
                    managerAndroid.connectWifiDirect(nodeList.get(holder.getAdapterPosition()).getDeviceWifiDirectMacAddress());
                }
            });

        }

        @Override
        public int getItemCount() {
            return nodeList.size();
        }


    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        private TextView deviceName, deviceStatus;
        private FrameLayout deviceHolder;
        DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName= (TextView) itemView.findViewById(R.id.deviceName);
            deviceStatus = (TextView) itemView.findViewById(R.id.deviceMacAddress);
            deviceHolder= (FrameLayout) itemView.findViewById(R.id.deviceHolder);
        }
    }

    private NetworkNode getNetworkNodeByMacAddress(String deviceMacAddress){
        for(NetworkNode node: deviceList){
            if(node.getDeviceWifiDirectMacAddress().equals(deviceMacAddress)){
                return node;
            }
        }
        return null;
    }

}
