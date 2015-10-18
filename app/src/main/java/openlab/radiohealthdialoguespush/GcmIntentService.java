package openlab.radiohealthdialoguespush;

/**
 * Created by nkk27 on 30/07/15.
 */


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService
{
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static String received_str;

    public GcmIntentService()
    {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty())
        { // has effect of unparcelling Bundle
   /*
    * Filter messages based on message type. Since it is likely that GCM will be extended in the future with new message types, just ignore
    * any message types you're not interested in, or that you don't recognize.
    */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
                sendNotification("Send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                sendNotification("Deleted messages on server: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) // If it's a regular GCM message, do some work.
            {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++)
                {
                    Log.i(this.toString(), "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                Log.i(this.toString(), "Completed work @ " + SystemClock.elapsedRealtime());

                // Post notification of received message.
                sendNotification("Received: " + extras.toString());

                Log.i(this.toString(), "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    public void sendNotification(String msg)
    {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Notification").setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d("before Toast", msg);
        //System.out.println("Anything");
        //ServerMessageHandling pushmessage = new ServerMessageHandling();
        //pushmessage.setString(msg);

        //String[] temp = msg.split(" ");

       //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

   /* public String getPushMsg(){

     return received_str;

    }*/


}

