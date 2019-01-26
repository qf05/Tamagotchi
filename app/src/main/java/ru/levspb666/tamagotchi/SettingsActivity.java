package ru.levspb666.tamagotchi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;
import static ru.levspb666.tamagotchi.MainActivity.PETS;
import static ru.levspb666.tamagotchi.MainActivity.PREFERENCES_SELECTED_PET;
import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;

public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerCreate;
    private EditText inputName;
    private AlertDialog dialog;
    private DataBase db;
    private SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        db = DataBase.getAppDatabase(getApplicationContext());
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void createPet(View view) {
       ViewHelper.createDialog(this,this);
    }

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.cancel();
        }
    };

    public void changePet(View view) {
        Intent intent = new Intent(SettingsActivity.this, ChangePetActivity.class);
        startActivity(intent);
        finish();
    }

    public void deletePet(View view) {
        Intent intent = new Intent(SettingsActivity.this, DeletePetActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeNamePet(View view) {
        View layout = getLayoutInflater().inflate(R.layout.change_name_dialog, null);
        TextView selectedPetName = layout.findViewById(R.id.selectedPetName);
        selectedPetName.setText(getString(R.string.this_name) + SELECTED_PET.getName());
        inputName = layout.findViewById(R.id.inputChangeName);
        Button ok = layout.findViewById(R.id.okChangeName);
        ok.setOnClickListener(okChangeNameListener);
        Button cancel = layout.findViewById(R.id.cancelChangeName);
        cancel.setOnClickListener(cancelListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout).setCancelable(false);
        dialog = builder.create();
        ViewHelper.fonForDialog(dialog, (ImageView) layout.findViewById(R.id.fon_change_name_dialog));
        dialog.show();
    }

    View.OnClickListener okChangeNameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = inputName.getText().toString().trim();
            if (PetUtils.checkName(SettingsActivity.this, name)) {
                SELECTED_PET.setName(name);
                db.petDao().update(SELECTED_PET);
                PETS = db.petDao().getAll();
                dialog.cancel();
            }
        }
    };

    public void goHome(View view) {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
