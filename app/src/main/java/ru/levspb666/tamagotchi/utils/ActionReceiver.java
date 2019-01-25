package ru.levspb666.tamagotchi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import ru.levspb666.tamagotchi.MainActivity;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

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
                switch (actionType) {
                    case EAT:
                        pet = eat(context, pet);
                        break;
                    case WALK:

                        break;
                    case ILL:
                        pet = ill(context, pet);
                        break;
                    case SLEEP:

                        break;
                    case WAKEUP:

                        break;
                    case SHIT:
                        shit(context, pet);
                        break;
                }
                db.petDao().update(pet);
                if (MainActivity.handler != null) {
                    MainActivity.handler.sendEmptyMessage(0);
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
}
