package testing.sendingsmsapplication.Reciever;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import testing.sendingsmsapplication.Activity.LaunchActivity;
import testing.sendingsmsapplication.R;

/**
 * Created by deveshbatra on 6/14/16.
 */
public class SMSReceiver extends BroadcastReceiver {

//    private static HashMap<String, Integer> map = new HashMap<String, Integer>();
//    private static int id = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        SmsMessage shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);


        Log.d("SMSReceiver", "SMS message sender: " +
                shortMessage.getOriginatingAddress());
        Log.d("SMSReceiver", "SMS message text: " +
                shortMessage.getDisplayMessageBody());
        Log.d("SMSReceiver", "SMS message icc: " +
                shortMessage.getIndexOnIcc());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        int value = 0, maxValue = 0;
        value = prefs.getInt(shortMessage.getDisplayOriginatingAddress(), 0);
        if (value == 0) {
            maxValue = prefs.getInt("MAXVALUE", 0);
            maxValue++;
            editor.putInt("MAXVALUE", maxValue);
            value = maxValue;
            editor.putInt(shortMessage.getDisplayOriginatingAddress(), maxValue);
        }
        editor.commit();

        Log.d("SMSReceiver", "SMS message value " +
                value);
        Log.d("SMSReceiver", "SMS message maxvalue " +
                maxValue);


        NotificationManager nm = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, LaunchActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(shortMessage.getOriginatingAddress())
                        .setContentText(shortMessage.getDisplayMessageBody())
                        .setContentIntent(pendingIntent)
                        .setGroup(shortMessage.getOriginatingAddress())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));
        Notification notification = mBuilder.build();
// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(context, LaunchActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(LaunchActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


//        if (map.get(shortMessage.getDisplayOriginatingAddress()) == 0)
//            map.put(shortMessage.getDisplayOriginatingAddress(), id++);


        mNotificationManager.notify(value, notification);


    }

}

