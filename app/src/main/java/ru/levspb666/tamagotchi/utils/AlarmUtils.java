package ru.levspb666.tamagotchi.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AlarmUtils {

    private static final int SHIT_TIME_ADD = 1;
    private static final int SHIT_RANDOM_TIME = 1;
    private static final int ILL_TIME_ADD = 1;
    private static final int ILL_TIME_REPEAT = 1;
    private static final int EAT_TIME = 1;
    private static final int WALK_TIME_ADD = 1;
    private static final int WALK_RANDOM_TIME = 1;
    private static final int SLEEP_TIME_ADD = 1;
    private static final int SLEEP_RANDOM_TIME = 1;
    private static final int WAKE_UP_TIME_ADD = 1;
    private static final int WAKE_UP_RANDOM_TIME = 1;

    public static long nextShit() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) (SHIT_TIME_ADD + (Math.random() * SHIT_RANDOM_TIME)));
        return calendar.getTimeInMillis();
    }

    public static long nextWalk() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) (WALK_TIME_ADD + (Math.random() * WALK_RANDOM_TIME)));
        return calendar.getTimeInMillis();
    }

    public static long nextSleep() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) (SLEEP_TIME_ADD + (Math.random() * SLEEP_RANDOM_TIME)));
        return calendar.getTimeInMillis();
    }

    public static long nextWakeUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) (WAKE_UP_TIME_ADD + (Math.random() * WAKE_UP_RANDOM_TIME)));
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
            alarmMgr.set(AlarmManager.RTC_WAKEUP, time(ActionType.SHIT, pet), pendingIntent);
        }
    }

    public static void setRepeatAlarm(Context context, ActionType action, Pet pet) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action.toString(),
                Uri.parse("pet_id:" + pet.getId()),
                context.getApplicationContext(),
                ActionReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                (int) pet.getId(),
                intent,
                FLAG_UPDATE_CURRENT);
        if (alarmMgr != null) {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time(ActionType.ILL, pet), repeatTime(ActionType.ILL), pendingIntent);
        }
    }

    public static void cancelAllAlarm(Context context, Pet pet) {
        cancelAlarm(context.getApplicationContext(), ActionType.ILL, pet);
        cancelAlarm(context.getApplicationContext(), ActionType.SHIT, pet);
        cancelAlarm(context.getApplicationContext(), ActionType.EAT, pet);
        cancelAlarm(context.getApplicationContext(), ActionType.WALK, pet);
        cancelAlarm(context.getApplicationContext(), ActionType.SLEEP, pet);
        cancelAlarm(context.getApplicationContext(), ActionType.WAKEUP, pet);
    }

    public static void cancelAlarm(Context context, ActionType action, Pet pet) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(
                action.toString(),
                null,
                context.getApplicationContext(),
                ActionReceiver.class);
        intent.setData(Uri.parse("pet_id:" + pet.getId()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                (int) pet.getId(),
                intent,
                FLAG_UPDATE_CURRENT);
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    // https://ru.stackoverflow.com/questions/662836/Идентификация-alarmmanager
    public static boolean checkAlarm(Context context, ActionType action, Pet pet) {
        Intent intent = new Intent(action.toString(),
                null,
                context.getApplicationContext(),
                ActionReceiver.class);
        intent.setData(Uri.parse("pet_id:" + pet.getId()));
        return (PendingIntent.getBroadcast(
                context.getApplicationContext(),
                (int) pet.getId(),
                intent,
                FLAG_NO_CREATE) == null);
    }

    public static void checkAllAlarmPet(Context context, Pet pet) {
        if (pet.isLive()) {
            if (pet.isIll() && checkAlarm(context.getApplicationContext(), ActionType.ILL, pet)) {
                setRepeatAlarm(context.getApplicationContext(), ActionType.ILL, pet);
            }
            if (checkAlarm(context.getApplicationContext(), ActionType.EAT, pet)) {
                setRepeatAlarm(context.getApplicationContext(), ActionType.EAT, pet);
            }
            if (pet.getNextShit() > Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.SHIT, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.SHIT, pet);
            }
            if (pet.getNextShit() <= Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.ILL, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.ILL, pet);
            }
            if (pet.getNextWalk() > Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.WALK, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.WALK, pet);
            }
            if (pet.getNextWalk() <= Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.ILL, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.ILL, pet);
            }

            if (!pet.isSlip() && pet.getNextSlip() > Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.SLEEP, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.SLEEP, pet);
            }
            if (!pet.isSlip() && pet.getNextSlip() <= Calendar.getInstance().getTimeInMillis()
                    && checkAlarm(context.getApplicationContext(), ActionType.ILL, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.ILL, pet);
            }
            if (pet.isSlip() && checkAlarm(context.getApplicationContext(), ActionType.WAKEUP, pet)) {
                setAlarm(context.getApplicationContext(), ActionType.WAKEUP, pet);
            }
        }
    }

    public static void checkAllAlarm(Context context, List<Pet> pets) {
        if (pets.size() > 0) {
            for (Pet pet : pets) {
                checkAllAlarmPet(context, pet);
            }
        }
    }

    public static boolean allRight(Pet pet) {
        long now = Calendar.getInstance().getTimeInMillis();
        return !pet.isIll()
                && pet.getNextShit() > now
                && pet.getSatiety() > 1
                && pet.getNextWalk() > now
                && pet.getNextSlip() > now;
    }

    public static void cancelAlarmIllWithCheck(Context context, Pet pet) {
        if (allRight(pet)) {
            cancelAlarm(context.getApplicationContext(), ActionType.ILL, pet);
        }
        Log.e("cancelAlarmIll", "NotILL " + !pet.isIll());
        Log.e("cancelAlarmIll", "SHIT " + (pet.getNextShit() > Calendar.getInstance().getTimeInMillis()));
        Log.e("cancelAlarmIll", "EAT " + (pet.getSatiety() > 1));
        Log.e("cancelAlarmIll", "WALK " + (pet.getNextWalk() > Calendar.getInstance().getTimeInMillis()));
        Log.e("cancelAlarmIll", "SLEEP " + (pet.getNextSlip() > Calendar.getInstance().getTimeInMillis()));
    }

    private static long time(ActionType action, Pet pet) {
        Calendar calendar = Calendar.getInstance();
        switch (action) {
            case EAT:
                calendar.add(Calendar.MINUTE, EAT_TIME);
                return calendar.getTimeInMillis();
            case WALK:
                return pet.getNextWalk();
            case ILL:
                calendar.add(Calendar.MINUTE, ILL_TIME_ADD);
                return calendar.getTimeInMillis();
            case SLEEP:
                return pet.getNextSlip();
            case WAKEUP:
                return pet.getWakeUp();
            case SHIT:
                return pet.getNextShit();
        }
        return 1000 * 60;
    }

    public static long repeatTime(ActionType action) {
        switch (action) {
            case EAT:
                return EAT_TIME * 1000 * 60;
            case ILL:
                return ILL_TIME_REPEAT * 1000 * 60;
        }
        return 1000 * 60;
    }
}
