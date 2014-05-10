package com.example.NotificationsDemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class MyActivity extends Activity {
    private static final int OLD_WAY_NOTIFICATION_ID = 0;
    private static final int BUILDER_NOTIFICATION_ID = 1;
    private static final int CUSTOM_NOTIFICATION_ID = 2;

    private NotificationManager manager;
    long[] vibratePattern = {1000, 500, 300, 500, 100, 100};
    private PendingIntent contentIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MyActivity.class), 0);

        initViews();
    }

    private void initViews() {
        Button bShowOldWayNotif = (Button) findViewById(R.id.bShowOldWayNotif);
        bShowOldWayNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationUsingOldWay();
            }
        });
        Button bShowBuilderNotif = (Button) findViewById(R.id.bShowBuilderNotif);
        bShowBuilderNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationUsingBuilder();
            }
        });
        Button bShowNotifCustomLayout = (Button) findViewById(R.id.bShowNotifCustomLayout);
        bShowNotifCustomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationWithCustomLayout();
            }
        });
    }

    /**
     * Shows notification using pre API11 methods.
     */
    private void showNotificationUsingOldWay() {
        // Use this constructor on pre API11 devices.
        Notification notification = new Notification(android.R.drawable.ic_btn_speak_now,
                "Old way notification...", System.currentTimeMillis());

        notification.number++;
        notification.defaults = Notification.DEFAULT_ALL;
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.vibrate = vibratePattern;

        // Enable red led on the device
        notification.ledARGB = Color.RED;
        notification.ledOffMS = 0;
        notification.ledOnMS = 1;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

        notification.setLatestEventInfo(this, "expanded title", "expanded text", contentIntent);

        manager.notify(OLD_WAY_NOTIFICATION_ID, notification);
    }


    /**
     * Shows notification on API11+ using builder.
     */
    private void showNotificationUsingBuilder() {
        final Notification.Builder builder = new Notification.Builder(this);
        final int progress = 30;
        builder.setSmallIcon(android.R.drawable.ic_input_add)
                .setTicker("builder ticket")
                .setContentText("content text")
                        // API16+
                        // .setSubText("sub text")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(vibratePattern)
                .setLights(Color.RED, 0, 1)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentIntent(contentIntent);
        // API14+
        //.setProgress(100, progress, false);

        Notification builderNotification = builder.getNotification();

        manager.notify(BUILDER_NOTIFICATION_ID, builderNotification);
    }

    /**
     * Shows notification with custom layout. Registers listeners to remoteViews components.
     */
    private void showNotificationWithCustomLayout() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Notification")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Progress")
                .setProgress(100, 50, false)
                .setContent(remoteViews);
        Notification notification = builder.getNotification();

        // Set view and intent on pre API11 target versions.
        notification.contentView = remoteViews;
        notification.contentIntent = contentIntent;

        // Change parameters of views inside custom layout (remoteViews).
        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.btn_star_big_on);
        notification.contentView.setTextViewText(R.id.status_text, "Current Progress:");
        notification.contentView.setProgressBar(R.id.status_progress, 100, 50, false);

        Intent newIntent = new Intent(this, StatusProgressActivity.class);
        newIntent.setFlags(newIntent.getFlags() | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent newPendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
        notification.contentView.setOnClickPendingIntent(R.id.status_progress, newPendingIntent);

        manager.notify(CUSTOM_NOTIFICATION_ID, notification);
    }
}
