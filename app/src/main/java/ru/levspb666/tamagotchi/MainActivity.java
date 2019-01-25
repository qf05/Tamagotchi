package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;

public class MainActivity extends AppCompatActivity {

    public static Pet SELECTED_PET;
    public static boolean SOUND_OFF = false;
    private MenuItem soundCheckbox;
    private TextView petName;
    private ImageView petView;
    public static List<Pet> PETS = new ArrayList<>();
    public static DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        petName = findViewById(R.id.petName);
        petView = findViewById(R.id.petView);
        db = DataBase.getAppDatabase(getApplicationContext());
        PETS = db.petDao().getAll();
        setViewPet();
    }

    private void setViewPet() {
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
        }
    }

    public void goWalk(View view) {
        if (SELECTED_PET != null) {
            Intent intent = new Intent(MainActivity.this, WalkActivity.class);
            startActivity(intent);
        }
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        startActivity(intent);
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
}
