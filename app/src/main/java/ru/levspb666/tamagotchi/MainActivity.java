package ru.levspb666.tamagotchi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.levspb666.tamagotchi.adapters.QuickChangePetRVAdapter;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.utils.NotificationUtils.notificationCancel;
import static ru.levspb666.tamagotchi.utils.PetUtils.ADD_EAT;
import static ru.levspb666.tamagotchi.utils.PetUtils.ADD_HP_EAT;

public class MainActivity extends AppCompatActivity implements QuickChangePetRVAdapter.ItemClickListener {

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
    public static Handler handler;
    private Button ill;
    private Button walk;
    private ProgressBar eatProgressBar;
    private ProgressBar hpProgressBar;
    private ProgressBar expProgressBar;
    private TextView lvl;
    private QuickChangePetRVAdapter adapter;
    private boolean viewYourPets = false;
    private ImageView upDown;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        petName = findViewById(R.id.petName);
        petView = findViewById(R.id.petView);
        shitView = findViewById(R.id.shit);
        shitView.setOnClickListener(shitClickListener);
        ill = findViewById(R.id.ill);
        walk = findViewById(R.id.goWalk);
        eatProgressBar = findViewById(R.id.eatProgressBar);
        hpProgressBar = findViewById(R.id.hpProgressBar);
        expProgressBar = findViewById(R.id.expProgressBar);
        lvl = findViewById(R.id.lvl);
        LinearLayout yourPets = findViewById(R.id.yourPets);
        yourPets.setOnClickListener(changePetClickListener);
        upDown = findViewById(R.id.up_down);
        db = DataBase.getAppDatabase(getApplicationContext());

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(PREFERENCES_SOUND_OFF)) {
            SOUND_OFF = settings.getBoolean(PREFERENCES_SOUND_OFF, false);
        }
        AlarmUtils.checkAllAlarm(getApplicationContext(), PETS);

        rv = findViewById(R.id.new_change_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                llm.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        adapter = new QuickChangePetRVAdapter(MainActivity.this);
        adapter.setClickListener(MainActivity.this);
        rv.setAdapter(adapter);
        viewYourPets = false;
        ImageView imageView = findViewById(R.id.mainBackGround);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.setVisibility(View.INVISIBLE);
                upDown.setRotation(0);
                viewYourPets = false;
                handler.sendEmptyMessage(0);
            }
        });
        checkPet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new MyHandler(this);
    }

    private void checkPet() {
        PETS = db.petDao().getAll();
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
        if (PETS.size()==0){
            ViewHelper.createDialog(this,this);
        }

        if (SELECTED_PET != null) {
            petName.setText(SELECTED_PET.getName());
            setViewPet();
        }
    }

    private void setViewPet() {
        PETS = db.petDao().getAll();
        if (SELECTED_PET.isLive()) {
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
        } else {
            petView.setImageResource(R.drawable.die);
            eatProgressBar.setProgress(1);
            ill.setVisibility(View.INVISIBLE);
            ill.setClickable(false);
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
            walk.setVisibility(View.INVISIBLE);
            walk.setClickable(false);
            eatProgressBar.setProgress(0);
            hpProgressBar.setProgress(0);
            expProgressBar.setMax((int) (50 + 200 * SELECTED_PET.getLvl() + Math.pow(1.1, SELECTED_PET.getLvl() + 25)) / 6);
            expProgressBar.setProgress(SELECTED_PET.getExperience());
            lvl.setText(SELECTED_PET.getLvl() + getString(R.string.lvl));
        }
        if (viewYourPets) {
            rv.setVisibility(View.VISIBLE);
            upDown.setRotation(180);
        } else {
            rv.setVisibility(View.INVISIBLE);
            upDown.setRotation(0);
        }
    }

    private void changeVisibility() {
        if (SELECTED_PET.getNextShit() < Calendar.getInstance().getTime().getTime()) {
            shitView.setVisibility(View.VISIBLE);
            shitView.setClickable(true);
        } else {
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
        }
        if (SELECTED_PET.isIll()) {
            ill.setVisibility(View.VISIBLE);
            ill.setClickable(true);
        } else {
            ill.setVisibility(View.INVISIBLE);
            ill.setClickable(false);
        }
        walk.setVisibility(View.VISIBLE);
        walk.setClickable(true);
        eatProgressBar.setProgress(SELECTED_PET.getSatiety());
        hpProgressBar.setProgress(SELECTED_PET.getHp());
        expProgressBar.setMax((int) (50 + 200 * SELECTED_PET.getLvl() + Math.pow(1.1, SELECTED_PET.getLvl() + 25)) / 6);
        expProgressBar.setProgress(SELECTED_PET.getExperience());
        lvl.setText(SELECTED_PET.getLvl() + " " + getString(R.string.lvl));
    }

    public void goWalk(View view) {
        if (SELECTED_PET.isSlip()) {
            Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                    getString(R.string.not_walk_if_sleep), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            if (SELECTED_PET != null) {
                Intent intent = new Intent(MainActivity.this, WalkActivity.class);
                startActivity(intent);
            }
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
            viewYourPets = false;
            handler.sendEmptyMessage(0);
            SELECTED_PET.setNextShit(AlarmUtils.nextShit());
            PetUtils.addExperience(10);
            db.petDao().update(SELECTED_PET);
            AlarmUtils.setAlarm(getApplicationContext(), ActionType.SHIT, SELECTED_PET);
            AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
            notificationCancel(getApplicationContext(), ActionType.SHIT, SELECTED_PET);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        handler = null;
    }

    public void ill(View view) {
        viewYourPets = false;
        SELECTED_PET.setIll(false);
        PetUtils.addExperience(11);
        db.petDao().update(SELECTED_PET);
        ill.setVisibility(View.INVISIBLE);
        ill.setClickable(false);
        handler.sendEmptyMessage(0);
        AlarmUtils.cancelAlarm(getApplicationContext(), ActionType.ILL, SELECTED_PET);
        notificationCancel(getApplicationContext(), ActionType.ILL, SELECTED_PET);
    }

    public void eat(View view) {
        viewYourPets = false;
        if (SELECTED_PET.isSlip()) {
            Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                    getString(R.string.not_eat_if_sleep), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            if (SELECTED_PET.getSatiety() < 100) {
                SELECTED_PET.setSatiety(SELECTED_PET.getSatiety() + ADD_EAT);
                if (SELECTED_PET.getSatiety() > 100) {
                    SELECTED_PET.setSatiety(100);
                }
                SELECTED_PET.setHp(SELECTED_PET.getHp() + ADD_HP_EAT);
                if (SELECTED_PET.getHp() > 100) {
                    SELECTED_PET.setHp(100);
                }
                PetUtils.addExperience(7);
                db.petDao().update(SELECTED_PET);
                handler.sendEmptyMessage(0);
                notificationCancel(getApplicationContext(), ActionType.EAT, SELECTED_PET);
                AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
            } else {
                Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                        getString(R.string.not_hunger), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public void sleep(View view) {
        viewYourPets = false;
        if (SELECTED_PET.isSlip()) {
            Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                    getString(R.string.is_sleep), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            if (!SELECTED_PET.isIll()) {
                SELECTED_PET.setSlip(true);
                SELECTED_PET.setWakeUp(AlarmUtils.nextWakeUp());
                SELECTED_PET.setNextSlip(AlarmUtils.nextSleep() + (SELECTED_PET.getWakeUp() - Calendar.getInstance().getTimeInMillis()));
                PetUtils.addExperience(14);
                db.petDao().update(SELECTED_PET);
                AlarmUtils.setAlarm(getApplicationContext(), ActionType.WAKEUP, SELECTED_PET);
                AlarmUtils.setAlarm(getApplicationContext(), ActionType.SLEEP, SELECTED_PET);
                notificationCancel(getApplicationContext(), ActionType.SLEEP, SELECTED_PET);
                AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
                handler.sendEmptyMessage(0);
            } else {
                Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                        getString(R.string.not_sleep_if_ill), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    View.OnClickListener changePetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewYourPets = !viewYourPets;
            handler.sendEmptyMessage(0);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(View view, int position) {
        SELECTED_PET = PETS.get(position);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFERENCES_SELECTED_PET, SELECTED_PET.getId());
        editor.apply();
        handler.sendEmptyMessage(0);
        adapter.notifyDataSetChanged();
    }

    private static class MyHandler extends Handler {

        WeakReference<MainActivity> wrActivity;

        public MyHandler(MainActivity activity) {
            wrActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = wrActivity.get();
            if (activity != null) {
                if (SELECTED_PET != null) {
                    SELECTED_PET = db.petDao().findById(SELECTED_PET.getId());
                }
                activity.setViewPet();
            }
        }
    }
}
