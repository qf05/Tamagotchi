package ru.levspb666.tamagotchi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;
import static ru.levspb666.tamagotchi.MainActivity.PETS;
import static ru.levspb666.tamagotchi.MainActivity.PREFERENCES_SOUND_OFF;
import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;
import static ru.levspb666.tamagotchi.MainActivity.SOUND_OFF;

public class SettingsActivity extends AppCompatActivity {

    private EditText inputName;
    private AlertDialog dialog;
    private DataBase db;
    private SharedPreferences settings;
    private CheckBox sound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();
        SettingsActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        db = DataBase.getAppDatabase(getApplicationContext());
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        sound = findViewById(R.id.soundCheckbox);
        sound.setChecked(!SOUND_OFF);
    }

    public void createPet(View view) {
        ViewHelper.createDialog(this, this);
    }

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.cancel();
        }
    };

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

    public void notification(View view) {
    }

    public void history(View view) {
    }

    public void sound(View view) {
        SOUND_OFF = !SOUND_OFF;
        sound.setChecked(!SOUND_OFF);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFERENCES_SOUND_OFF, SOUND_OFF);
        editor.apply();
    }
}
