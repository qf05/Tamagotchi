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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Objects;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
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
    private Button create;
    private Button rename;
    private Button delete;
    private Button notification;
    private Button history;
    private Button home;

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
        create = findViewById(R.id.create);
        rename = findViewById(R.id.rename);
        delete = findViewById(R.id.del);
        notification = findViewById(R.id.notification);
        history = findViewById(R.id.history);
        home = findViewById(R.id.homeFromSettings);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("sony-d6633-CB5A25TGZ3")
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    public void createPet(View view) {
        create.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                create.setClickable(true);
                ViewHelper.createDialog(SettingsActivity.this, SettingsActivity.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        create.startAnimation(animation);
    }

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
            dialog.cancel();
        }
    };

    public void deletePet(View view) {
        delete.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SettingsActivity.this, DeletePetActivity.class);
                startActivity(intent);
                delete.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        delete.startAnimation(animation);
    }

    public void changeNamePet(View view) {
        delete.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View layout = getLayoutInflater().inflate(R.layout.change_name_dialog, null);
                TextView selectedPetName = layout.findViewById(R.id.selectedPetName);
                selectedPetName.setText(getString(R.string.this_name) + SELECTED_PET.getName());
                inputName = layout.findViewById(R.id.inputChangeName);
                Button ok = layout.findViewById(R.id.okChangeName);
                ok.setOnClickListener(okChangeNameListener);
                Button cancel = layout.findViewById(R.id.cancelChangeName);
                cancel.setOnClickListener(cancelListener);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setView(layout).setCancelable(false);
                dialog = builder.create();
                ViewHelper.fonForDialog(dialog, (ImageView) layout.findViewById(R.id.fon_change_name_dialog));
                dialog.show();
                rename.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rename.startAnimation(animation);
    }

    View.OnClickListener okChangeNameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = inputName.getText().toString().trim();
            if (PetUtils.checkName(SettingsActivity.this, name)) {
                SELECTED_PET.setName(name);
                db.petDao().update(SELECTED_PET);
                PETS = db.petDao().getAll();
                ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
                dialog.cancel();
            }
        }
    };

    public void goHome(View view) {
        home.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                home.setClickable(true);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        home.startAnimation(animation);
    }

    public void notification(View view) {
        notification.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SettingsActivity.this, NotificationSettingsActivity.class);
                startActivity(intent);
                notification.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        notification.startAnimation(animation);
    }

    public void history(View view) {
        history.setClickable(false);
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SettingsActivity.this, HistoryActivity.class);
                startActivity(intent);
                history.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        history.startAnimation(animation);
    }

    public void sound(View view) {
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        SOUND_OFF = !SOUND_OFF;
        ViewHelper.playClick(SettingsActivity.this, ActionType.DIE);
        sound.setChecked(!SOUND_OFF);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFERENCES_SOUND_OFF, SOUND_OFF);
        editor.apply();
        sound.setClickable(true);
    }
}
