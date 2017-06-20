package com.ustadmobile.test.sharedse.http;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.port.sharedse.networkmanager.NetworkManager;
import com.ustadmobile.port.sharedse.networkmanager.ResumableHttpDownload;
import com.ustadmobile.port.sharedse.networkmanager.WiFiDirectGroup;
import com.ustadmobile.port.sharedse.networkmanager.WiFiDirectGroupListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by kileha3 on 11/05/2017.
 */

public class RemoteTestServerHttpd extends NanoHTTPD {

    public static final String CMD_SETSUPERNODE_ENABLED = "SUPERNODE";

    public static final String CMD_CREATEGROUP = "CREATEGROUP";

    public static final String CMD_RECEIVE_FILE = "RECEIVEFILE";

    public static final String CMD_ACQUIRE_FILE = "ACQUIREFILE";

    public static final int GROUP_CREATION_TIMEOUT = 60*1000;


    protected NetworkManager networkManager;

    public RemoteTestServerHttpd(int port, NetworkManager networkManager) {
        super(port);
        this.networkManager = networkManager;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, List<String>> decodedParams = decodeParameters(session.getQueryParameterString());
        String command = decodedParams.containsKey("cmd") ? decodedParams.get("cmd").get(0) : null;
        try {
            if(CMD_SETSUPERNODE_ENABLED.equals(command)) {
                boolean enabled = Boolean.parseBoolean(decodedParams.get("enabled").get(0));
                networkManager.setSuperNodeEnabled(networkManager.getContext(), enabled);
                return newFixedLengthResponse("OK");
            }else if(CMD_RECEIVE_FILE.equals(command)){
                boolean enabled = Boolean.parseBoolean(decodedParams.get("enabled").get(0));
                networkManager.setSuperNodeEnabled(this,enabled);
                return newFixedLengthResponse("OK");
            }else if(CMD_ACQUIRE_FILE.equals(command)){
                String srcUrl=decodedParams.get("src").get(0);
                String destUrl=decodedParams.get("dest").get(0);
                if(new ResumableHttpDownload(srcUrl,destUrl).download()){
                    return newFixedLengthResponse("OK");
                }else{
                    return newFixedLengthResponse("500");
                }
            } else if(CMD_CREATEGROUP.equals(command)) {
                int groupStatus = networkManager.getWifiDirectGroupStatus();
                final Object groupLock = new Object();
                WiFiDirectGroup group = null;
                Response response = null;
                WiFiDirectGroupListener groupListener = new WiFiDirectGroupListener() {
                    @Override
                    public void groupCreated(WiFiDirectGroup group, Exception err) {
                        synchronized (groupLock) {
                            groupLock.notify();
                        }
                    }

                    @Override
                    public void groupRemoved(boolean successful, Exception err) {

                    }
                };
                networkManager.addWifiDirectGroupListener(groupListener);

                try {
                    switch(groupStatus) {
                        case NetworkManager.WIFI_DIRECT_GROUP_STATUS_INACTIVE:
                            networkManager.createWifiDirectGroup();
                        case NetworkManager.WIFI_DIRECT_GROUP_STATUS_UNDER_CREATION:
                            synchronized (groupLock){
                                try { groupLock.wait(10000); }
                                catch(InterruptedException e) {}
                            }
                            break;
                    }

                    group = networkManager.getWifiDirectGroup();
                    JSONObject jsonResponse = new JSONObject();
                    if(group != null) {
                        jsonResponse.put("ssid", group.getSsid());
                        jsonResponse.put("passphrase", group.getPassphrase());
                        response = newFixedLengthResponse(Response.Status.OK, "application/json",
                                jsonResponse.toString());
                    }else {
                        response = newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
                                "wifi direct group not created");
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    response = newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
                            "wifi direct group not created: exception" + e.toString());
                }finally {
                    networkManager.removeWifiDirectGroupListener(groupListener);
                }

                return response;
            }
        }catch(Exception e) {
            e.printStackTrace();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(bout));
            e.printStackTrace(writer);
            writer.flush();
            String exceptionMsg = e.toString()  + "" + new String(bout.toByteArray());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain",
                    exceptionMsg);
        }


        return super.serve(session);
    }
}
