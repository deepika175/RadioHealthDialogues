package openlab.radiohealthdialoguespush;


/**
 * Created by nkk27 on 09/06/15.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class Register extends AppCompatActivity{


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static String SENDER_ID = "512855476184";
    private GoogleCloudMessaging gcm = null;

    private EditText txt_reg_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_reg_id = (EditText) this.findViewById(R.id.txt_reg_id);

        if (checkPlayServices())
        {
        }
        else
        {
            Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickRegDevice(View view)
    {
        System.out.println("in onClickRegDevice.. view = " + view);

        // get registration id from shared preferences and thus check whether app is already registered or not.
        String regid = getRegistrationId(this);
        Log.i(this.toString(), "registration id from shared pref : " + regid);

        if (regid.isEmpty()) // if blank, then app is not yet registered
        {
            registerInBackground();
        }
        else
        {
            txt_reg_id.setText(regid);
        }

    }

    /**
     * this method will check whether device has google play services apk installed or not. if not then it will display appropriate dialog.
     */
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(this.toString(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * get gcm registration id from shared preferences. once app is registered to gcm and got the registration id, it will store that in shared
     * preferences.
     *
     * returns blank when no registration id found in shared pref. if app was updated, then also it will return blank so that app can be registered
     * again.
     */
    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty())
        {
            Log.i(this.toString(), "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(this.toString(), "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * store registration id and app version in shared preferences.
     */
    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGCMPreferences(context);

        int appVersion = getAppVersion(context);
        Log.i(this.toString(), "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Register.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground()
    {
        new AsyncTask<String, Void, String>()
        {
            private String regId = "";

            @Override
            protected String doInBackground(String... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(Register.this);
                    }
                    regId = gcm.register(SENDER_ID);
                    Log.i(this.toString(), "regId = "+regId);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(Register.this, regId);

                    msg = regId;
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                // setting registration id in edit text.
                txt_reg_id.setText(msg);
            }

        }.execute(null, null, null);
    }



}


