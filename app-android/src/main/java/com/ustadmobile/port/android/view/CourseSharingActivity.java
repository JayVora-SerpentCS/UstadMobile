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
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private boolean isSharingFiles=false;

    private boolean isSharedFileDialogShown=false;

    private List<NetworkNode> deviceList=new Vector<>();


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
        managerAndroid.setSharingContent(true);
        switchCompatReceive.setChecked(true);
        new WifiDirectAutoAccept(this).intercept(true);

    }


    @Override
    public void onDestroy() {
        if(managerAndroid!=null){
            managerAndroid.setSharingContent(false);
            if(managerAndroid.isSuperNodeEnabled()){
               managerAndroid.setSuperNodeEnabled(this,true);
            }
        }
        super.onDestroy();
    }

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        handleSwitchSelectionChange(isChecked);
    }



    private void handleSwitchSelectionChange(boolean isReceivingContent){

        if(isReceivingContent){
            refreshDeviceList();
        }
        managerAndroid.startContentSharing(impl.getDeviceName(this),isReceivingContent);
        this.isSharingFiles=isReceivingContent;
        String title=isReceivingContent ? impl.getString(MessageIDConstants.chooseDeviceToShareWith):
                impl.getString(MessageIDConstants.receiveSharedCourse);
        int deviceListVisibility=isReceivingContent ? View.VISIBLE: View.GONE;
        deviceRecyclerView.setVisibility(deviceListVisibility);
        managerAndroid.setSuperNodeEnabled(this,!isReceivingContent);
        managerAndroid.setSharingContent(true);
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
        //visible device view
        int deviceViewVisibility=visible ? View.VISIBLE: View.GONE;
        connectedDeviceView.setVisibility(deviceViewVisibility);
        //Discovered device list
        int deviceListVisibility=visible ? View.GONE:View.VISIBLE;
        deviceRecyclerView.setVisibility(deviceListVisibility);

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
        String deviceName=null,deviceAddress=null;
        if(isDeviceConnected){
            WifiP2pInfo wifiP2pInfo=managerAndroid.getWifiDirectHandler().getWifiP2pInfo();
            if(wifiP2pInfo.isGroupOwner && isSharingFiles){
                Log.d(NetworkManagerAndroid.TAG,"My IP address "+
                        wifiP2pInfo.groupOwnerAddress+" on port "+managerAndroid.getHttpListeningPort());
                //get all connected clients devices
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
                        deviceName=groupOwner.deviceName;
                        deviceAddress=impl.getString(MessageIDConstants.deviceAddressLabel)+" "+groupOwner.deviceAddress;
                        String ipAddress=String.valueOf(managerAndroid.getWifiDirectHandler().getWifiP2pInfo().groupOwnerAddress).replace("/","");
                        connectedNode =new NetworkNode(groupOwner.deviceAddress,ipAddress);
                        connectedNode.setPort(managerAndroid.getHttpListeningPort());
                        connectedNode.setDeviceName(deviceName);
                        managerAndroid.setP2PConnectedNode(connectedNode);
                        acquireOPDSFeed();
                    }
                    setConnectedDeviceInfo(deviceName,deviceAddress);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        }else{
            for(NetworkNode networkNode: deviceList){
                NetworkNode node=getNetworkNodeByMacAddress(networkNode.getDeviceWifiDirectMacAddress());
                node.setStatus(WifiP2pDevice.AVAILABLE);
            }
        }

        refreshDeviceList();
        String title= isDeviceConnected ? impl.getString(MessageIDConstants.deviceConnectedTo) :
                (managerAndroid.isSuperNodeEnabled() ? impl.getString(MessageIDConstants.receiveSharedCourse)
                        : impl.getString(MessageIDConstants.chooseDeviceToShareWith));
        setToolbarTitle(title);
        waitingProgressBar.setVisibility(isDeviceConnected ? View.GONE : View.VISIBLE);
        setViewsVisibility(isDeviceConnected && !isSharingFiles);
    }

    @Override
    public void wifiConnectionChanged(String ssid, boolean connected, boolean connectedOrConnecting) {

    }


    /**
     * Class which handle displaying all peer to peer sharing discovered devices.
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
            String deviceAddress=impl.getString(MessageIDConstants.deviceAddressLabel)
                    +" "+nodeList.get(holder.getAdapterPosition()).getDeviceWifiDirectMacAddress();
            holder.deviceAddress.setText(deviceAddress);
            if(nodeList.get(holder.getAdapterPosition()).getStatus()==WifiP2pDevice.CONNECTED){
                holder.connectionIndicator.setBackgroundResource(R.drawable.connection_connected_bg_shape);
            }else{
                holder.connectionIndicator.setBackgroundResource(R.drawable.connection_available_bg_shape);
            }

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
        private TextView deviceName,deviceAddress;
        private FrameLayout deviceHolder,connectionIndicator;
        DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName= (TextView) itemView.findViewById(R.id.deviceName);
            deviceAddress= (TextView) itemView.findViewById(R.id.deviceMacAddress);
            deviceHolder= (FrameLayout) itemView.findViewById(R.id.deviceHolder);
            connectionIndicator= (FrameLayout) itemView.findViewById(R.id.connection_indicator);
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
