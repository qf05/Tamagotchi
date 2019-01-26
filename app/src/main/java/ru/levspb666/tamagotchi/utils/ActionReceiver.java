package ru.levspb666.tamagotchi.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Objects;

import ru.levspb666.tamagotchi.MainActivity;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static ru.levspb666.tamagotchi.utils.AlarmUtils.cancelAllAlarm;
import static ru.levspb666.tamagotchi.utils.AlarmUtils.setRepeatAlarm;
import static ru.levspb666.tamagotchi.utils.PetUtils.ILL_TAKE_HP;
import static ru.levspb666.tamagotchi.utils.PetUtils.TAKE_EAT;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        DataBase db = DataBase.getAppDatabase(context.getApplicationContext());
        Pet pet = db.petDao().findById(Integer.parseInt(Objects.requireNonNull(intent.getData()).getSchemeSpecificPart()));
        if (pet != null) {
            if (pet.isLive()) {
                ActionType actionType = ActionType.valueOf(intent.getAction());
                if (pet.isSlip()
                        && !ActionType.WAKEUP.equals(actionType)
                        && !ActionType.EAT.equals(actionType)) {
                    ifSleepAlarm(context, actionType, pet);
                } else {
                    switch (actionType) {
                        case EAT:
                            pet = eat(context, pet);
                            break;
                        case WALK:
                            walk(context, pet);
                            break;
                        case ILL:
                            pet = ill(context, pet);
                            break;
                        case SLEEP:
                            sleep(context, pet);
                            break;
                        case WAKEUP:
                            pet = wakeUp(context, pet);
                            break;
                        case SHIT:
                            shit(context, pet);
                            break;
                    }
                    db.petDao().update(pet);
                    if (MainActivity.handler != null) {
                        MainActivity.handler.sendEmptyMessage(0);
                    }
                }
            } else {
                cancelAllAlarm(context, pet);
            }
        }
    }

    private void shit(final Context context, Pet pet) {
        if (AlarmUtils.checkAlarm(context, ActionType.ILL, pet)) {
            setRepeatAlarm(context, ActionType.ILL, pet);
        }
        NotificationUtils.notification(context, pet, ActionType.SHIT);
    }

    private Pet ill(final Context context, Pet pet) {
        if (pet.isIll()) {
            int hp = pet.getHp() - ILL_TAKE_HP;
            if (hp > 0) {
                pet.setHp(hp);
                if (hp < 30) {
                    NotificationUtils.notification(context, pet, ActionType.CURE);
                }
            } else {
                pet.setHp(0);
                pet.setLive(false);
                NotificationUtils.notification(context, pet, ActionType.DIE);
                cancelAllAlarm(context, pet);
            }
        } else {
            pet.setIll(true);
            NotificationUtils.notification(context, pet, ActionType.ILL);
        }
        return pet;
    }

    private Pet eat(final Context context, Pet pet) {
        int petSatiety = pet.getSatiety() - TAKE_EAT;
        if (petSatiety <= 1) {
            pet.setSatiety(1);
            if (AlarmUtils.checkAlarm(context, ActionType.ILL, pet)) {
                setRepeatAlarm(context, ActionType.ILL, pet);
            }
            NotificationUtils.notification(context, pet, ActionType.EAT);
        } else {
            pet.setSatiety(petSatiety);
        }
        return pet;
    }

    private void walk(Context context, Pet pet) {
        if (AlarmUtils.checkAlarm(context, ActionType.ILL, pet)) {
            setRepeatAlarm(context, ActionType.ILL, pet);
        }
        NotificationUtils.notification(context, pet, ActionType.WALK);
    }

    private void sleep(Context context, Pet pet) {
        if (AlarmUtils.checkAlarm(context, ActionType.ILL, pet)) {
            setRepeatAlarm(context, ActionType.ILL, pet);
        }
        NotificationUtils.notification(context, pet, ActionType.SLEEP);
    }

    private Pet wakeUp(final Context context, Pet pet) {
        pet.setSlip(false);
//        setAlarm(context,ActionType.SLEEP,pet);
        NotificationUtils.notification(context, pet, ActionType.WAKEUP);
        AlarmUtils.checkAllAlarmPet(context, pet);
        int hp = pet.getHp() + 5;
        if (hp >= 100) {
            pet.setHp(100);
        } else {
            pet.setHp(hp);
        }
        return pet;
    }

    private void ifSleepAlarm(Context context, ActionType action, Pet pet) {
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

        if (action == ActionType.EAT || action == ActionType.ILL) {
            if (alarmMgr != null) {
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, pet.getWakeUp() + 1000, AlarmUtils.repeatTime(action), pendingIntent);
            }
        } else {
            if (alarmMgr != null) {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, pet.getWakeUp() + 1000, pendingIntent);
            }
        }
    }
}
