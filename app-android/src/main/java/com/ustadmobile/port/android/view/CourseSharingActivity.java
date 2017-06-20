package com.ustadmobile.port.android.view;

import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toughra.ustadmobile.R;
import com.ustadmobile.core.MessageIDConstants;
import com.ustadmobile.core.controller.CourseSharingPresenter;
import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.core.networkmanager.NetworkManagerListener;
import com.ustadmobile.core.networkmanager.NetworkNode;
import com.ustadmobile.core.opds.UstadJSOPDSFeed;
import com.ustadmobile.core.util.UMFileUtil;
import com.ustadmobile.core.view.CourseSharingView;
import com.ustadmobile.port.android.netwokmanager.NetworkManagerAndroid;
import com.ustadmobile.port.android.netwokmanager.WifiDirectAutoAccept;
import com.ustadmobile.port.android.util.UMAndroidUtil;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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

public class CourseSharingActivity extends UstadBaseActivity  implements CourseSharingView {

    private RecyclerView deviceRecyclerView;

    private DeviceAdapter deviceAdapter;

    private ProgressBar waitingProgressBar;

    private NetworkManagerAndroid managerAndroid;

    private TextView toolbarTitle,connectedNetworkName,connectedDeviceAddress,receivingFileNameView;

    private CardView connectedDeviceView;
    private ProgressBar fileReceivingProgressBar;

    private UstadMobileSystemImpl impl;

    private NetworkNode connectedNode =null;

    private boolean isSharedFileDialogShown=false;

    private List<NetworkNode> deviceList=new Vector<>();

    private static final String DEVICE_PHONE_TAG="[Phone]";

    private CourseSharingPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_sharing);
        setUMToolbar(R.id.course_sharing_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        impl=UstadMobileSystemImpl.getInstance();

        managerAndroid=((NetworkManagerAndroid)impl.getNetworkManager());

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


        //enable sharing mode
        managerAndroid.setContentSharingEnabled(true);

        Hashtable args = UMAndroidUtil.bundleToHashtable(getIntent().getExtras());
        mPresenter =new CourseSharingPresenter(this,this,args);
        mPresenter.onCreate();
        mPresenter.handleDeviceModesSelection();


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
        mPresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * Method responsible for handling device listing,
     * showing discovered devices and their states.
     */
    @Override
    public void setDeviceMode(boolean isReceiverDevice) {
        if(!isReceiverDevice){
            refreshDeviceList();
        }
        managerAndroid.startContentSharing(impl.getDeviceName(this),isReceiverDevice);
        String title=isReceiverDevice ? impl.getString(MessageIDConstants.receiveSharedCourse)
                :impl.getString(MessageIDConstants.chooseDeviceToShareWith);
        int deviceListVisibility=isReceiverDevice ? View.GONE: View.VISIBLE;
        deviceRecyclerView.setVisibility(deviceListVisibility);
        managerAndroid.setContentSharingEnabled(true);
        setToolbarTitle(title);
        mPresenter.handleUserConfirmation(null,false, NetworkManager.NOTIFICATION_TYPE_SERVER);

    }

    @Override
    public void handleOnDeviceStateChanged(boolean isDeviceConnected) {
        String deviceName=null,deviceStatus=null;
        boolean isGroupInfoAvailable=false;
        if(isDeviceConnected){
            WifiP2pInfo wifiP2pInfo=managerAndroid.getWifiDirectHandler().getWifiP2pInfo();
            if(wifiP2pInfo.isGroupOwner && !mPresenter.isReceiverDevice()){
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
                        mPresenter.handleOnDeviceConnected();
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
        mPresenter.handleViewVisibilityOnDeviceConnected(isDeviceConnected && mPresenter.isReceiverDevice() && isGroupInfoAvailable);
    }

    @Override
    public void refreshDeviceList() {
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
                int visibility=deviceList.size()> 0 ? View.GONE: View.VISIBLE;
                waitingProgressBar.setVisibility(visibility);
                deviceAdapter.setNodeList(deviceList);
                deviceAdapter.notifyDataSetChanged();
                deviceRecyclerView.invalidate();
            }
        });
    }

    @Override
    public void showErrorMessage(final String message) {
        Looper.prepare();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CourseSharingActivity.this,message,Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void setReceivingProgress(final int progress, final String title) {
        setFileTitle(String.format(impl.getString(MessageIDConstants.receivingFileTitle),title));
        fileReceivingProgressBar.setProgress(progress);
    }

    @Override
    public void setConnectedDeviceInfo(String deviceName, String deviceAddress) {
        connectedDeviceAddress.setText(deviceAddress);
        connectedNetworkName.setText(deviceName);
    }



    @Override
    public void acquireOPDSFeed() {

        if(!isSharedFileDialogShown){

            String opdsSrcURL = UMFileUtil.joinPaths(new String[]{"http://" + connectedNode.getDeviceIpAddress() + ":"
                    + connectedNode.getPort(),"/shared/shared.opds"});
            File opdsDestURL = new File(managerAndroid.getContext().getCacheDir(), "shared.opds");
            mPresenter.handleDownloadFeed(opdsSrcURL,opdsDestURL.getAbsolutePath());
            isSharedFileDialogShown=true;
        }

    }

    @Override
    public void showConfirmationDialog(final UstadJSOPDSFeed opdsFeed) {
        Looper.prepare();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(opdsFeed.entries.length>0){
                    String filesList="";
                    String message=opdsFeed.entries.length > 1 ?
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
                    android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(CourseSharingActivity.this);
                    builder.setTitle(impl.getString(MessageIDConstants.shareDialogChoiceTitle));
                    builder.setMessage(Html.fromHtml(String.format(impl.getString(MessageIDConstants.shareDialogChoiceMessage)
                            ,message, connectedNode.getDeviceName()+";-<br/>",filesList+"<br/><br/>")));
                    builder.setNegativeButton(impl.getString(MessageIDConstants.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mPresenter.handleUserConfirmation(opdsFeed,false,NetworkManager.NOTIFICATION_TYPE_SERVER);
                        }
                    });
                    builder.setPositiveButton(impl.getString(MessageIDConstants.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            waitingProgressBar.setVisibility(View.GONE);
                            mPresenter.handleUserConfirmation(opdsFeed,true,NetworkManager.NOTIFICATION_TYPE_SERVER);
                        }
                    });
                    builder.show();
                }else{
                    managerAndroid.removeWiFiDirectGroup();
                    Toast.makeText(CourseSharingActivity.this,impl.getString(MessageIDConstants.somethingWentWrong),Toast.LENGTH_LONG).show();
                }
            }
        });
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
                    mPresenter.handleClickConnectWiFiDirect(connectedNode);
                }
            });

        }

        @Override
        public int getItemCount() {
            return nodeList.size();
        }


    }

    /**
     * Class which is responsible for presenting single device item on the custom adapter
     */
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
