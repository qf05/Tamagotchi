package ru.levspb666.tamagotchi.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Calendar;

import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AlarmUtils {

    private static final int SHIT_TIME_REPEAT = 1;
    private static final int SHIT_RANDOM_TIME = 1;

    public static long nextShit() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) (SHIT_TIME_REPEAT + (Math.random()*SHIT_RANDOM_TIME)));
        return calendar.getTimeInMillis();
    }


// https://startandroid.ru/ru/uroki/vse-uroki-spiskom/204-urok-119-pendingintent-flagi-requestcode-alarmmanager.html
        public static void setAlarm(Context context, ActionType action, Pet pet) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(
                    action.toString(),
                    Uri.parse("pet_id:" + pet.getId()),
                    context.getApplicationContext(),
                    ActionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    (int) pet.getId(),
                    intent,
                    //  https://stackoverflow.com/questions/14485368/delete-alarm-from-alarmmanager-using-cancel-android
                    FLAG_UPDATE_CURRENT);
            if (alarmMgr != null) {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, pet.getNextShit(), pendingIntent);
            }
        }

}
