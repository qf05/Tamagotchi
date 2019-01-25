package ru.levspb666.tamagotchi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCES_SOUND_OFF = "SOUND_OFF";
    public static final String APP_PREFERENCES = "PREFERENCES";
    public static final String PREFERENCES_SELECTED_PET = "SELECTED";
    private SharedPreferences settings;
    public static Pet SELECTED_PET;
    public static boolean SOUND_OFF = false;
    private MenuItem soundCheckbox;
    private TextView petName;
    private ImageView petView;
    public static List<Pet> PETS = new ArrayList<>();
    private static DataBase db;
    private ImageView shitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        petName = findViewById(R.id.petName);
        petView = findViewById(R.id.petView);
        shitView = findViewById(R.id.shit);
        shitView.setOnClickListener(shitClickListener);
        db = DataBase.getAppDatabase(getApplicationContext());
        PETS = db.petDao().getAll();
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(PREFERENCES_SOUND_OFF)) {
            SOUND_OFF = settings.getBoolean(PREFERENCES_SOUND_OFF, false);
        }
        setViewPet();
    }

    private void setViewPet() {
        if (settings.contains(PREFERENCES_SELECTED_PET)) {
            long petId = settings.getLong(PREFERENCES_SELECTED_PET, -1);
            if (petId >= 0) {
                SELECTED_PET = db.petDao().findById(petId);
            }
        }

        if (PETS.size() > 0 && (SELECTED_PET == null || !PETS.contains(SELECTED_PET))) {
            SELECTED_PET = PETS.get(0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(PREFERENCES_SELECTED_PET, SELECTED_PET.getId());
            editor.apply();
        }

        if (SELECTED_PET != null) {
            petName.setText(SELECTED_PET.getName());
            switch (PetsType.valueOf(SELECTED_PET.getType())) {
                case CAT:
                    petView.setImageResource(R.drawable.cat_small);
                    break;
                case DOG:
                    petView.setImageResource(R.drawable.dog_small);
                    break;
                case CTHULHU:
                    petView.setImageResource(R.drawable.cthulhu_small);
                    break;
            }
            changeVisibility();
        }
    }

    private void changeVisibility(){
        if (SELECTED_PET.getNextShit() < Calendar.getInstance().getTime().getTime()) {
            shitView.setVisibility(View.VISIBLE);
            shitView.setClickable(true);
        } else {
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
        }
    }

    public void goWalk(View view) {
        if (SELECTED_PET != null) {
            Intent intent = new Intent(MainActivity.this, WalkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        soundCheckbox = menu.findItem(R.id.offSound);
        soundCheckbox.setChecked(!SOUND_OFF);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.petSettings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.alarmSettings:

                return true;
            case R.id.showHistory:

                return true;
            case R.id.offSound:
                SOUND_OFF = !SOUND_OFF;
                soundCheckbox.setChecked(!SOUND_OFF);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener shitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
            SELECTED_PET.setNextShit(AlarmUtils.nextShit());
            db.petDao().update(SELECTED_PET);
            AlarmUtils.setAlarm(getApplicationContext(), ActionType.SHIT, SELECTED_PET);
        }
    };
}
