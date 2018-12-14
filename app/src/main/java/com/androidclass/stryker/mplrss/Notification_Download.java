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
 */
public class Notification_Download extends IntentService {
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

                int id_flux = Integer.parseInt(intent.getStringExtra("id_flux"))+1;

                Intent ii = new Intent(this, AfficheRSS.class);
                ii.putExtra("id_flux", id_flux );
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder monBuilder = monBuilder(adresse);
                nm.notify(1, monBuilder.setContentIntent(pendingIntent).build());
                monBuilder.setAutoCancel(true);

            }
        }
    }

    private NotificationCompat.Builder monBuilder(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).setAutoCancel(true);
        return builder.setSmallIcon(android.R.drawable.ic_dialog_info). setContentTitle("Téléchargement Terminé").setContentText(message);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}