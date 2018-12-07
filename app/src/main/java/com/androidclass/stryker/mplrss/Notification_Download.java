package com.androidclass.stryker.mplrss;


import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class Notification_Download extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String NOTIF = "notif";

    private NotificationManagerCompat nm;
    private static final String CHANNEL_ID = "channel";

    public Notification_Download() {
        super("Notification_Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createNotificationChannel();
        nm = NotificationManagerCompat.from(this);
        if (intent != null) {
            final String action = intent.getAction();
            if (NOTIF.equals(action)) {
                final String adresse = intent.getStringExtra("adresse");
                //int n = handleActionNotification(adresse);

                /*
                Intent ii = new Intent(this, AfficheActivity.class); // TODO : créer fragment d'affichage des flux dl avec Fragment
                ii.putExtra("nombre_premier", n );
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);
                */
                nm.notify(1, monBuilder(adresse).build());//setContentIntent(pendingIntent).build());

            }
        }
    }

    /**
     * Handle action premier in the provided background thread with the provided
     * parameters.
     */
    /* TODO :
    private int handleActionNotification(String param1) {
        return 0;

    }
    */

    private NotificationCompat.Builder monBuilder(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        return builder.setSmallIcon(android.R.drawable.ic_dialog_info). setContentTitle("Téléchargement Terminé").setContentText(message);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}