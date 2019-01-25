package ru.levspb666.tamagotchi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

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

                        break;
                    case WALK:

                        break;
                    case ILL:

                        break;
                    case SLEEP:

                        break;
                    case WAKEUP:

                        break;
                    case SHIT:
                        shit(context, pet);
                        break;
                }
            }

        }
    }

    private void shit(final Context context, Pet pet) {

    }
}
