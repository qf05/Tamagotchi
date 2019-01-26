package ru.levspb666.tamagotchi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.Objects;

import ru.levspb666.tamagotchi.MainActivity;
import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;
import static ru.levspb666.tamagotchi.MainActivity.PETS;
import static ru.levspb666.tamagotchi.MainActivity.PREFERENCES_SELECTED_PET;

public class ViewHelper {
    public static void executeAfterViewHasDrawn(final View v, final Runnable cb) {
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                cb.run();
                ViewTreeObserver obs = v.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    public static void fonForDialog(final Dialog dialog, final ImageView fon) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                final int i = Objects.requireNonNull(dialog.getWindow()).getDecorView().getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        i - 50
                );
                fon.setLayoutParams(params);
            }
        });
    }

    public static void createDialog(final Activity activity, final Context context) {
        View layout = activity.getLayoutInflater().inflate(R.layout.create_pet_dialog, null);
        final Spinner spinnerCreate = layout.findViewById(R.id.inputTypePet);
        String[] stringArray = context.getResources().getStringArray(R.array.pets);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, R.layout.spinner_adapter,R.id.spinner_text,stringArray);
        spinnerCreate.setAdapter(spinnerAdapter);
        final EditText inputName = layout.findViewById(R.id.inputName);
        Button ok = layout.findViewById(R.id.okCreate);
        final Button cancel = layout.findViewById(R.id.cancelCreate);
        final DataBase db = DataBase.getAppDatabase(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout).setCancelable(false);
        final Dialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString().trim();
                if (PetUtils.checkName(context, name)) {
                    PetsType[] petsTypes = PetsType.values();
                    PetsType petsType = petsTypes[spinnerCreate.getSelectedItemPosition()];
                    Log.i("SELECTED_PET", petsType.toString() + "   " + name);
                    long id = db.petDao().insert(new Pet(name, petsType));
                    PETS = db.petDao().getAll();
                    MainActivity.SELECTED_PET = db.petDao().findById(id);

                    AlarmUtils.checkAllAlarm(context.getApplicationContext(), PETS);
                    SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong(PREFERENCES_SELECTED_PET, id);
                    editor.apply();
                    if (activity.getLocalClassName().equalsIgnoreCase("MainActivity")) {
                        MainActivity.handler.sendEmptyMessage(0);
                    }
                    dialog.cancel();

                }
            }
        });
        if (activity.getLocalClassName().equalsIgnoreCase("MainActivity")) {
            cancel.setClickable(false);
            cancel.setVisibility(View.INVISIBLE);
        } else {
            cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    }
            );
        }
        ViewHelper.fonForDialog(dialog, (ImageView) layout.findViewById(R.id.fonCreateDialog));
        dialog.show();
    }
}
