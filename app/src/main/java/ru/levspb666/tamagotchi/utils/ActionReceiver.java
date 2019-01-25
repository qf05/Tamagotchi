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

public class ActionReceiver extends BroadcastReceiver {

    private static int ILL_TAKE_HP = 1;

    @Override
    public void onReceive(final Context context, Intent intent) {
        DataBase db = DataBase.getAppDatabase(context.getApplicationContext());
        Pet pet = db.petDao().findById(Integer.parseInt(Objects.requireNonNull(intent.getData()).getSchemeSpecificPart()));
        if (pet != null) {
            if (pet.isLive()) {
                ActionType actionType = ActionType.valueOf(intent.getAction());
                switch (actionType) {
                    case EAT:

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
            }else {
                cancelAllAlarm(context,pet);
            }
        }
    }

    private void shit(final Context context, Pet pet) {
        setRepeatAlarm(context, ActionType.ILL, pet);
    }

    private Pet ill(final Context context, Pet pet) {
        if (pet.isIll()) {
            int hp = pet.getHp() - ILL_TAKE_HP;
            if (hp > 0) {
                pet.setHp(hp);
            } else {
                pet.setHp(0);
                pet.setLive(false);
                cancelAllAlarm(context, pet);
            }
        } else {
            pet.setIll(true);
        }
        return pet;
    }
}
