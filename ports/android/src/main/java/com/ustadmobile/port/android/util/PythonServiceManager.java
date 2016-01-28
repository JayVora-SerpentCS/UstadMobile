package com.ustadmobile.port.android.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.Context;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import android.os.Handler;
import android.database.Cursor;
import java.util.List;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.Context;

//import org.renpy.android.AudioThread;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;
import org.renpy.android.Action;
//import org.renpy.android.AssetExtract;
import org.renpy.android.Configuration;
import org.renpy.android.Hardware;
import org.renpy.android.Project;
import org.renpy.android.PythonService;
import org.renpy.android.ResourceManager;
import org.renpy.android.SDLSurfaceView;

import java.io.*;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.zip.GZIPInputStream;

import android.content.res.AssetManager;

import org.kamranzafar.jtar.*;

/**
 * Created by varuna on 26/11/15.
 */
public class PythonServiceManager {

    private static String TAG = "Python";

    // The audio thread for streaming audio...
    //private static AudioThread mAudioThread = null;

    // The SDLSurfaceView we contain.
    public static SDLSurfaceView mView = null;
    //public static PythonActivity mActivity = null;
    public static ApplicationInfo mInfo = null;

    // Did we launch our thread?
    private boolean mLaunchedThread = false;

    private ResourceManager resourceManager;

    // The path to the directory contaning our external storage.
    private File externalStorage;

    // The path to the directory containing the game.
    private File mPath = null;

    boolean _isPaused = false;

    private static final String DB_INITIALIZED = "db_initialized";


    public Context context;

    public void startThis(Context context){
        this.context = context;
        this.externalStorage = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        this.mPath = this.externalStorage;
        run();
        start_service("Django", "Django is running", "/storage/emulated/0/com.toughra.ustadmobile/lrs-djandro.log");


    }

    public void stop_service() {
        Intent serviceIntent = new Intent(this.context, PythonService.class);
        this.context.stopService(serviceIntent);
    }


    public void start_service(String serviceTitle, String serviceDescription,
                                     String pythonServiceArgument) {
        Intent serviceIntent = new Intent(this.context, PythonService.class);
        String argument = context.getFilesDir().getAbsolutePath();
        //Changes to make it work
        String filesDirectory = this.mPath.getAbsolutePath();
        filesDirectory = context.getFilesDir().getAbsolutePath();
        serviceIntent.putExtra("androidPrivate", argument);
        serviceIntent.putExtra("androidArgument", filesDirectory);
        serviceIntent.putExtra("pythonHome", argument);
        serviceIntent.putExtra("pythonPath", argument + ":" + filesDirectory + "/lib");
        serviceIntent.putExtra("serviceTitle", serviceTitle);
        serviceIntent.putExtra("serviceDescription", serviceDescription);
        serviceIntent.putExtra("pythonServiceArgument", pythonServiceArgument);
        this.context.startService(serviceIntent);
    }

    protected void onCreate(Bundle savedInstanceState) {

        String test="test";
        /*
        //super.onCreate(savedInstanceState);

        //Hardware.context = this;
        ///Action.context = this;
        //this.mActivity = this;

        //context.getWindowManager().getDefaultDisplay().getMetrics(Hardware.metrics);

        resourceManager = new ResourceManager(this);
        externalStorage = new File(Environment.getExternalStorageDirectory(), getPackageName());

        // Figure out the directory where the game is. If the game was
        // given to us via an intent, then we use the scheme-specific
        // part of that intent to determine the file to launch. We
        // also use the android.txt file to determine the orientation.
        //
        // Otherwise, we use the public data, if we have it, or the
        // private data if we do not.
        if (getIntent() != null && getIntent().getAction() != null &&
                getIntent().getAction().equals("org.renpy.LAUNCH")) {
            mPath = new File(getIntent().getData().getSchemeSpecificPart());

            Project p = Project.scanDirectory(mPath);

            if (p != null) {
                if (p.landscape) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

            // Let old apps know they started.
            try {
                FileWriter f = new FileWriter(new File(mPath, ".launch"));
                f.write("started");
                f.close();
            } catch (IOException e) {
                // pass
            }



        } else if (resourceManager.getString("public_version") != null) {
            mPath = externalStorage;
        } else {
            mPath = getFilesDir();
        }

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        // go to fullscreen mode if requested
        try {
            this.mInfo = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);
            Log.v("python", "metadata fullscreen is" + this.mInfo.metaData.get("fullscreen"));
            if ( (Integer)this.mInfo.metaData.get("fullscreen") == 1 ) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        if ( Configuration.use_billing ) {
            mBillingHandler = new Handler();
        }

        // Start showing an SDLSurfaceView.
        mView = new SDLSurfaceView(
                this,
                mPath.getAbsolutePath());

        Hardware.view = mView;
        setContentView(mView);

        // Force the background window color if asked
        if ( this.mInfo.metaData.containsKey("android.background_color") ) {
            getWindow().getDecorView().setBackgroundColor(
                    this.mInfo.metaData.getInt("android.background_color"));
        }
        //start_service("Django","Django is running","/storage/emulated/0/com.toughra.ustadmobile/djandro.log");
        */
    }

    /**
     * Show an error using a toast. (Only makes sense from non-UI
     * threads.)
     */
    /*
    public void toastError(final String msg) {

        final Activity thisActivity = this;

        runOnUiThread(new Runnable () {
            public void run() {
                Toast.makeText(thisActivity, msg, Toast.LENGTH_LONG).show();
            }
        });

        // Wait to show the error.
        synchronized (this) {
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
            }
        }
    }
    */


    public class AssetExtract {
        private AssetManager mAssetManager = null;
        //private Activity mActivity = null;

        AssetExtract(Context act) {
            //mActivity = act;
            mAssetManager = act.getAssets();
        }

        public boolean extractTar(String asset, String target) {

            byte buf[] = new byte[1024 * 1024];

            InputStream assetStream = null;
            TarInputStream tis = null;

            try {
                assetStream = mAssetManager.open(asset, AssetManager.ACCESS_STREAMING);
                tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(assetStream, 8192)), 8192));
            } catch (IOException e) {
                Log.e("python", "opening up extract tar", e);
                return false;
            }

            while (true) {
                TarEntry entry = null;

                try {
                    entry = tis.getNextEntry();
                } catch (java.io.IOException e) {
                    Log.e("python", "extracting tar", e);
                    return false;
                }

                if (entry == null) {
                    break;
                }

                Log.i("python", "extracting " + entry.getName());

                if (entry.isDirectory()) {

                    try {
                        new File(target + "/" + entry.getName()).mkdirs();
                    } catch (SecurityException e) {
                    }
                    ;

                    continue;
                }

                OutputStream out = null;
                String path = target + "/" + entry.getName();

                try {
                    out = new BufferedOutputStream(new FileOutputStream(path), 8192);
                } catch (FileNotFoundException e) {
                } catch (SecurityException e) {
                }
                ;

                if (out == null) {
                    Log.e("python", "could not open " + path);
                    return false;
                }

                try {
                    while (true) {
                        int len = tis.read(buf);

                        if (len == -1) {
                            break;
                        }

                        out.write(buf, 0, len);
                    }

                    out.flush();
                    out.close();
                } catch (java.io.IOException e) {
                    Log.e("python", "extracting zip", e);
                    return false;
                }
            }

            try {
                tis.close();
                assetStream.close();
            } catch (IOException e) {
                // pass
            }

            return true;
        }
    }

    public void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File r : f.listFiles()) {
                recursiveDelete(r);
            }
        }
        f.delete();
    }

    /**
     * This determines if unpacking one the zip files included in
     * the .apk is necessary. If it is, the zip file is unpacked.
     */
    public void unpackData(final String resource, File target) {

        // The version of data in memory and on disk.
        //String data_version = resourceManager.getString(resource + "_version");
        String data_version = "1448183454.66";
        String disk_version = null;

        // If no version, no unpacking is necessary.
        if (data_version == null) {
            return;
        }

        // Check the current disk version, if any.

        String filesDir = target.getAbsolutePath();
        String disk_version_fn = filesDir + "/" + resource + ".version";

        try {
            byte buf[] = new byte[64];
            InputStream is = new FileInputStream(disk_version_fn);
            int len = is.read(buf);
            disk_version = new String(buf, 0, len);
            is.close();
        } catch (Exception e) {
            disk_version = "";
        }

        // If the disk data is out of date, extract it and write the
        // version file.
        if (! data_version.equals(disk_version)) {
            Log.v(TAG, "Extracting " + resource + " assets.");

            recursiveDelete(target);
            target.mkdirs();

            AssetExtract ae = new AssetExtract(this.context);
            if (!ae.extractTar(resource + ".mp3", target.getAbsolutePath())) {
                //toastError("Could not extract " + resource + " data.");
                System.out.println("Could not extract " + resource + " datta.");
            }

            try {
                // Write .nomedia.
                new File(target, ".nomedia").createNewFile();

                // Write version file.
                FileOutputStream os = new FileOutputStream(disk_version_fn);
                os.write(data_version.getBytes());
                os.close();
            } catch (Exception e) {
                Log.w("python", e);
            }
        }

    }


    public void run() {
        /*

        libjpeg.so
        libpython2.7.so
        libsdl_image.so
        libsdl_ttf.so
        libsqlite3.so
        libvudroid.so
         */

        unpackData("private", context.getFilesDir());
        unpackData("public", externalStorage);

        System.loadLibrary("sdl");
        System.loadLibrary("sdl_image");
        System.loadLibrary("sdl_ttf");
        System.loadLibrary("sdl_mixer");
        System.loadLibrary("python2.7");
        System.loadLibrary("application");
        System.loadLibrary("sdl_main");

        System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/_io.so");
        System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/unicodedata.so");

        try {
            System.loadLibrary("sqlite3");
            System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/_sqlite3.so");
        } catch(UnsatisfiedLinkError e) {
        }

        try {
            System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/_imaging.so");
            System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/_imagingft.so");
            System.load(context.getFilesDir() + "/lib/python2.7/lib-dynload/_imagingmath.so");
        } catch(UnsatisfiedLinkError e) {
        }


        /*runOnUiThread(new Runnable () {
            public void run() {
                mView.start();
            }
        });*/
    }


}
