/*
    This file is part of Ustad Mobile.

    Ustad Mobile Copyright (C) 2011-2014 UstadMobile Inc.

    Ustad Mobile is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version with the following additional terms:

    All names, links, and logos of Ustad Mobile and Toughra Technologies FZ
    LLC must be kept as they are in the original distribution.  If any new
    screens are added you must include the Ustad Mobile logo as it has been
    used in the original distribution.  You may not create any new
    functionality whose purpose is to diminish or remove the Ustad Mobile
    Logo.  You must leave the Ustad Mobile logo as the logo for the
    application to be used with any launcher (e.g. the mobile app launcher).

    If you want a commercial license to remove the above restriction you must
    contact us.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Ustad Mobile is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */

package com.ustadmobile.port.android.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.toughra.ustadmobile.R;

import com.ustadmobile.core.impl.UstadMobileSystemImpl;
import com.ustadmobile.port.android.impl.UstadMobileSystemImplAndroid;


public class SplashScreenActivity extends UstadBaseActivity implements DialogInterface.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    UstadMobileSystemImplAndroid impl;

    public static final int EXTERNAL_STORAGE_REQUESTED = 1;

    public static final String[] REQUIRED_PERMISSIONS = new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        impl = UstadMobileSystemImplAndroid.getInstanceAndroid();
        checkPermissions();
    }

    public void checkPermissions() {
        boolean hasRequiredPermissions = true;
        for(int i = 0; i < REQUIRED_PERMISSIONS.length; i++) {
            hasRequiredPermissions &= ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED;
        }

        if(!hasRequiredPermissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //show an alert
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("File permissions required").setMessage("This app requires file permissions on the SD card to download and save content");
                builder.setPositiveButton("OK", this);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, EXTERNAL_STORAGE_REQUESTED);
                return;
            }
        }

        impl.startUI(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermissions();
    }

    @Override
    public void onStart() {
        super.onStart();
        UstadMobileSystemImpl.getInstance();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leavecontainer) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
