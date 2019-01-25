package ru.levspb666.tamagotchi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.model.Pet;

import static ru.levspb666.tamagotchi.utils.AlarmUtils.checkAllAlarm;


public class MyBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DataBase db = DataBase.getAppDatabase(context.getApplicationContext());
            List<Pet> pets = db.petDao().getAll();
            checkAllAlarm(context, pets);
        }
    }
}
