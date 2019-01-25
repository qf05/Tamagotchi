package ru.levspb666.tamagotchi.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Objects;

import ru.levspb666.tamagotchi.MainActivity;
import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

public class NotificationUtils {

    //http://qaru.site/questions/177262/notificationcompatbuilder-deprecated-in-android-o
    //https://startandroid.ru/ru/uroki/vse-uroki-spiskom/509-android-notifications-osnovy.html
    public static void notification(Context context, Pet pet, ActionType action) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tamagotchi";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Tamagotchi",
                    NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_icon))
                .setContentTitle(pet.getName())
                .setContentIntent(contentIntent);

        switch (action) {
            case EAT:
                builder.setContentText(" проголодался!")
                        .setTicker(pet.getName() + " проголодался!");
                break;
            case WALK:
                builder.setContentText(" хочет гулять!")
                        .setTicker(pet.getName() + " хочет гулять!");
                break;
            case ILL:
                builder.setContentText(" заболел!")
                        .setTicker(pet.getName() + " заболел!");
                break;
            case SLEEP:
                builder.setContentText(" хочет спать!")
                        .setTicker(pet.getName() + " хочет спать!");
                break;
            case WAKEUP:
                builder.setContentText(" проснулся")
                        .setTicker(pet.getName() + " проснулся");
                break;
            case SHIT:
                builder.setContentText(" покакал!")
                        .setTicker(pet.getName() + " покакал!");
                break;
            case CURE:
                builder.setContentText(" может умереть!")
                        .setTicker(pet.getName() + " может умереть!");
                break;
            case DIE:
                builder.setContentText(" умер! ;(")
                        .setTicker(pet.getName() + " умер!");
                break;
        }

        if (notificationManager != null) {
            notificationManager.notify(action.toString(), (int) pet.getId(), builder.build());
        }
    }

    public static void notificationCancel(Context context, ActionType action, Pet pet) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(action.toString(), (int) pet.getId());
    }
}
