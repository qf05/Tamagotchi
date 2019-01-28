package ru.levspb666.tamagotchi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import ru.levspb666.tamagotchi.adapters.QuickChangePetRVAdapter;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.History;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.utils.AlarmUtils.setRepeatAlarm;
import static ru.levspb666.tamagotchi.utils.NotificationUtils.notificationCancel;
import static ru.levspb666.tamagotchi.utils.PetUtils.ADD_EAT;
import static ru.levspb666.tamagotchi.utils.PetUtils.ADD_HP_EAT;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener, QuickChangePetRVAdapter.ItemClickListener {

    public static final String PREFERENCES_SOUND_OFF = "SOUND_OFF";
    public static final String APP_PREFERENCES = "PREFERENCES";
    public static final String PREFERENCES_SELECTED_PET = "SELECTED";
    private SharedPreferences settings;
    public static Pet SELECTED_PET;
    public static boolean SOUND_OFF = false;
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
    private ImageView illIndicator;
    private ImageView eatIndicator;
    private ImageView sleepIndicator;
    private ImageView walkIndicator;
    private Button eat;
    private Button sleep;
    private Button settingsButton;
    private RewardedVideoAd mRewardedVideoAd;
    private ProgressBar bonusLoadIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        petName = findViewById(R.id.petName);
        petView = findViewById(R.id.petView);
        shitView = findViewById(R.id.shit);
        shitView.setOnClickListener(shitClickListener);
        ill = findViewById(R.id.ill);
        walk = findViewById(R.id.goWalk);
        eat = findViewById(R.id.eat);
        sleep = findViewById(R.id.sleep);
        eatProgressBar = findViewById(R.id.eatProgressBar);
        hpProgressBar = findViewById(R.id.hpProgressBar);
        expProgressBar = findViewById(R.id.expProgressBar);
        illIndicator = findViewById(R.id.illIndicator);
        eatIndicator = findViewById(R.id.eatIndicator);
        walkIndicator = findViewById(R.id.walkIndicator);
        sleepIndicator = findViewById(R.id.sleepIndicator);
        settingsButton = findViewById(R.id.settings);
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
        LinearLayoutManager llm;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            llm = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        }else {
            llm = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, true);
        }
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

        MobileAds.initialize(this, "ca-app-pub-3940256099942544/5224354917");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        bonusLoadIndicator = findViewById(R.id.bonusLoadIndicator);
        bonusLoadIndicator.setVisibility(View.INVISIBLE);
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
        if (PETS.size() == 0) {
            ViewHelper.createDialog(this, this);
        }

        if (SELECTED_PET != null) {
            petName.setText(SELECTED_PET.getName());
            setViewPet();
        }
    }

    private void setViewPet() {
        PETS = db.petDao().getAll();
        int height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        double displayRate;
        if (SELECTED_PET.isLive()) {
            switch (PetsType.valueOf(SELECTED_PET.getType())) {
                case CAT:
                    if (SELECTED_PET.getLvl() < 20) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            displayRate = 3;
                            petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }else {
                            displayRate = 2.5;
                            petView.setScaleType(ImageView.ScaleType.FIT_END);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                        layoutParams.setMargins(0, 0, 0, height / 15);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        petView.setLayoutParams(layoutParams);
                        if (SELECTED_PET.isSlip()) {
                            petView.setImageResource(R.drawable.cat_small_sleep);
                        } else {
                            if (SELECTED_PET.isLive()) {
                                petView.setImageResource(R.drawable.cat_small);
                            } else {
                                petView.setImageResource(R.drawable.cat_small_sleep);
                            }
                        }

                    } else {
                        if (SELECTED_PET.getLvl() < 50) {
                            int marginRight;
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 2.5;
                                marginRight =0;
                                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }else {
                                displayRate = 2;
                                marginRight= height/10;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(0, 0, marginRight, height / 15);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.cat_norm_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.cat_norm);
                                } else {
                                    petView.setImageResource(R.drawable.cat_norm_sleep);
                                }
                            }
                        } else {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 2;
                                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }else {
                                displayRate = 1.8;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(height / 10, 0, 0, height / 30);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.cat_old_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.cat_old);
                                } else {
                                    petView.setImageResource(R.drawable.cat_old_sleep);
                                }
                            }
                        }
                    }
                    break;
                case DOG:
                    if (SELECTED_PET.getLvl() < 20) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            displayRate = 3;
                            petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }else {
                            displayRate = 2.5;
                            petView.setScaleType(ImageView.ScaleType.FIT_END);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                        layoutParams.setMargins(0, 0, 0, height / 10);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        petView.setLayoutParams(layoutParams);
                        if (SELECTED_PET.isSlip()) {
                            petView.setImageResource(R.drawable.dog_small_sleep);
                        } else {
                            if (SELECTED_PET.isLive()) {
                                petView.setImageResource(R.drawable.dog_small);
                            } else {
                                petView.setImageResource(R.drawable.dog_small_sleep);
                            }
                        }
                    } else {
                        if (SELECTED_PET.getLvl() < 50) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 2;
                                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }else {
                                displayRate = 1.7;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(0, 0, 0, height / 10);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.dog_norm_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.dog_norm);
                                } else {
                                    petView.setImageResource(R.drawable.dog_norm_sleep);
                                }
                            }
                        } else {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 1.8;
                                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }else {
                                displayRate = 1.6;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(0, 0, 0, height / 10);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.dog_old_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.dog_old);
                                } else {
                                    petView.setImageResource(R.drawable.dog_old_sleep);
                                }
                            }
                        }
                    }
                    break;
                case CTHULHU:
                    if (SELECTED_PET.getLvl() < 20) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            displayRate = 3;
                            petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }else {
                            displayRate = 2.5;
                            petView.setScaleType(ImageView.ScaleType.FIT_END);
                        }
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                        layoutParams.setMargins(0, 0, 0, height / 10);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        petView.setLayoutParams(layoutParams);
                        if (SELECTED_PET.isSlip()) {
                            petView.setImageResource(R.drawable.cthulhu_small_sleep);
                        } else {
                            if (SELECTED_PET.isLive()) {
                                petView.setImageResource(R.drawable.cthulhu_small);
                            } else {
                                petView.setImageResource(R.drawable.cthulhu_small_sleep);
                            }
                        }
                    } else {
                        if (SELECTED_PET.getLvl() < 50) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 2.5;
                                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }else {
                                displayRate = 2;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(0, 0, 0, height / 10);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.cthulhu_norm_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.cthulhu_norm);
                                } else {
                                    petView.setImageResource(R.drawable.cthulhu_norm_sleep);
                                }
                            }
                        } else {
                            int marginBottom;
                            int marginLeft;
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                displayRate = 1.5;
                                marginBottom = 10;
                                marginLeft = 50;
                                petView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }else {
                                displayRate = 1;
                                marginBottom = 30;
                                marginLeft = 10;
                                petView.setScaleType(ImageView.ScaleType.FIT_END);
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
                            layoutParams.setMargins(-height/marginLeft, 0, 0, height / marginBottom);
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            petView.setLayoutParams(layoutParams);
                            if (SELECTED_PET.isSlip()) {
                                petView.setImageResource(R.drawable.cthulhu_old_sleep);
                            } else {
                                if (SELECTED_PET.isLive()) {
                                    petView.setImageResource(R.drawable.cthulhu_old);
                                } else {
                                    petView.setImageResource(R.drawable.cthulhu_old_sleep);
                                }
                            }
                        }
                    }
                    break;
            }
            changeVisibility();
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                displayRate = 1.8;
                petView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else {
                displayRate = 1.6;
                petView.setScaleType(ImageView.ScaleType.FIT_END);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (height / displayRate));
            layoutParams.setMargins(0, 0, 0, height / 15);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            petView.setLayoutParams(layoutParams);
            petView.setImageResource(R.drawable.die);
            eatProgressBar.setProgress(1);
            ill.setVisibility(View.INVISIBLE);
            ill.setClickable(false);
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
            walk.setClickable(false);
            eat.setClickable(false);
            sleep.setClickable(false);
            ViewHelper.indicatorInvisible(illIndicator);
            ViewHelper.indicatorInvisible(eatIndicator);
            ViewHelper.indicatorInvisible(sleepIndicator);
            ViewHelper.indicatorInvisible(walkIndicator);
            eatProgressBar.setProgress(0);
            hpProgressBar.setProgress(0);
            expProgressBar.setMax((int) (50 + 200 * SELECTED_PET.getLvl() + Math.pow(1.1, SELECTED_PET.getLvl() + 25)) / 6);
            expProgressBar.setProgress(SELECTED_PET.getExperience());
            lvl.setText(SELECTED_PET.getLvl() +" "+ getString(R.string.lvl));
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
        long now = Calendar.getInstance().getTimeInMillis();
        if (SELECTED_PET.getNextShit() < now) {
            shitView.setVisibility(View.VISIBLE);
            shitView.setClickable(true);
        } else {
            shitView.setVisibility(View.INVISIBLE);
            shitView.setClickable(false);
        }
        if (SELECTED_PET.isIll()) {
            ill.setVisibility(View.VISIBLE);
            ill.setClickable(true);
            ViewHelper.indicatorVisible(MainActivity.this, ill, illIndicator);
        } else {
            ViewHelper.indicatorInvisible(illIndicator);
            ill.setVisibility(View.INVISIBLE);
            ill.setClickable(false);
        }
        if (SELECTED_PET.getSatiety() < 5 && !SELECTED_PET.isSlip()) {
            ViewHelper.indicatorVisible(MainActivity.this, eat, eatIndicator);
        }else {
            ViewHelper.indicatorInvisible(eatIndicator);
        }
        if (SELECTED_PET.getNextWalk() <= now && !SELECTED_PET.isSlip()) {
            ViewHelper.indicatorVisible(MainActivity.this, walk, walkIndicator);
        }else {
            ViewHelper.indicatorInvisible(walkIndicator);
        }
        if (SELECTED_PET.getNextSlip() <= now && !SELECTED_PET.isSlip()) {
            ViewHelper.indicatorVisible(MainActivity.this, sleep, sleepIndicator);
        }else {
            ViewHelper.indicatorInvisible(sleepIndicator);
        }
        walk.setVisibility(View.VISIBLE);
        walk.setClickable(true);
        ViewHelper.executeAfterViewHasDrawn(eatProgressBar, new Runnable() {
            @Override
            public void run() {
                eatProgressBar.setProgress(SELECTED_PET.getSatiety());
                hpProgressBar.setProgress(SELECTED_PET.getHp());
                expProgressBar.setMax((int) (50 + 200 * SELECTED_PET.getLvl() + Math.pow(1.1, SELECTED_PET.getLvl() + 25)) / 6);
                expProgressBar.setProgress(SELECTED_PET.getExperience());
                lvl.setText(SELECTED_PET.getLvl() + " " + getString(R.string.lvl));
            }
        });
    }

    public void goWalk(View view) {
        walk.setClickable(false);
        ViewHelper.playClick(getApplicationContext(),ActionType.WALK);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (SELECTED_PET.isSlip()) {
                    Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                            getString(R.string.not_walk_if_sleep), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (SELECTED_PET != null) {
                        ViewHelper.indicatorInvisible(walkIndicator);
                        Intent intent = new Intent(MainActivity.this, WalkActivity.class);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        walk.startAnimation(animation);
    }

    private View.OnClickListener shitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHelper.playClick(MainActivity.this,ActionType.SHIT);
            viewYourPets = false;
            handler.sendEmptyMessage(0);
            SELECTED_PET.setNextShit(AlarmUtils.nextShit());
            PetUtils.addExperience(10, MainActivity.this);
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
        ill.setClickable(false);
        ViewHelper.playClick(MainActivity.this,ActionType.CURE);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                db.historyDao().insert(new History(
                        Calendar.getInstance().getTimeInMillis(),
                        ActionType.CURE.toString(),
                        SELECTED_PET.getId()));
                SELECTED_PET.setIll(false);
                PetUtils.addExperience(11, MainActivity.this);
                db.petDao().update(SELECTED_PET);
                ill.setVisibility(View.INVISIBLE);
                ViewHelper.indicatorInvisible(illIndicator);
                handler.sendEmptyMessage(0);
                AlarmUtils.cancelAlarm(getApplicationContext(), ActionType.ILL, SELECTED_PET);
                notificationCancel(getApplicationContext(), ActionType.ILL, SELECTED_PET);
                settingsButton.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ill.startAnimation(animation);
    }

    public void eat(View view) {
        viewYourPets = false;
        eat.setClickable(false);
        ViewHelper.playClick(MainActivity.this,ActionType.EAT);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (SELECTED_PET.isSlip()) {
                    Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                            getString(R.string.not_eat_if_sleep), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (AlarmUtils.checkAlarm(getApplicationContext(), ActionType.EAT, SELECTED_PET)) {
                        setRepeatAlarm(getApplicationContext(), ActionType.EAT, SELECTED_PET);
                    }
                    ViewHelper.indicatorInvisible(eatIndicator);
                    if (SELECTED_PET.getSatiety() < 100) {
                        SELECTED_PET.setSatiety(SELECTED_PET.getSatiety() + ADD_EAT);
                        if (SELECTED_PET.getSatiety() > 100) {
                            SELECTED_PET.setSatiety(100);
                        }
                        SELECTED_PET.setHp(SELECTED_PET.getHp() + ADD_HP_EAT);
                        if (SELECTED_PET.getHp() > 100) {
                            SELECTED_PET.setHp(100);
                        }
                        PetUtils.addExperience(7,MainActivity.this);
                        db.petDao().update(SELECTED_PET);
                        handler.sendEmptyMessage(0);
                        notificationCancel(getApplicationContext(), ActionType.EAT, SELECTED_PET);
                        AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
                        db.historyDao().insert(new History(
                                Calendar.getInstance().getTimeInMillis(),
                                ActionType.EAT.toString(),
                                SELECTED_PET.getId()));
                    } else {
                        Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                                getString(R.string.not_hunger), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                eat.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        eat.startAnimation(animation);
    }

    public void sleep(View view) {
        viewYourPets = false;
        sleep.setClickable(false);
        ViewHelper.playClick(getApplicationContext(),ActionType.SLEEP);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (SELECTED_PET.isSlip()) {
                    Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                            getString(R.string.is_sleep), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (!SELECTED_PET.isIll()) {
                        ViewHelper.indicatorInvisible(sleepIndicator);
                        SELECTED_PET.setSlip(true);
                        SELECTED_PET.setWakeUp(AlarmUtils.nextWakeUp());
                        SELECTED_PET.setNextSlip(AlarmUtils.nextSleep() + (SELECTED_PET.getWakeUp() - Calendar.getInstance().getTimeInMillis()));
                        PetUtils.addExperience(14,MainActivity.this);
                        db.petDao().update(SELECTED_PET);
                        AlarmUtils.setAlarm(getApplicationContext(), ActionType.WAKEUP, SELECTED_PET);
                        AlarmUtils.setAlarm(getApplicationContext(), ActionType.SLEEP, SELECTED_PET);
                        notificationCancel(getApplicationContext(), ActionType.SLEEP, SELECTED_PET);
                        AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
                        db.historyDao().insert(new History(
                                Calendar.getInstance().getTimeInMillis(),
                                ActionType.SLEEP.toString(),
                                SELECTED_PET.getId()));
                        handler.sendEmptyMessage(0);
                    } else {
                        Toast toast = Toast.makeText(MainActivity.this, SELECTED_PET.getName() + " " +
                                getString(R.string.not_sleep_if_ill), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                sleep.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        sleep.startAnimation(animation);
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

    public void toSettings(View view) {
        settingsButton.setClickable(false);
        ViewHelper.playClick(MainActivity.this,ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                settingsButton.setClickable(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        settingsButton.startAnimation(animation);
    }

    public void bonusExperience(View view) {
        bonusLoadIndicator.setVisibility(View.VISIBLE);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }
    @Override
    public void onRewardedVideoAdOpened() {
        bonusLoadIndicator.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onRewardedVideoStarted() {
    }
    @Override
    public void onRewardedVideoAdClosed() {
    }
    @Override
    public void onRewarded(RewardItem rewardItem) {
        int bonus;
        if (SELECTED_PET.getLvl()<20){
            bonus = 100;
        }else {
            if (SELECTED_PET.getLvl()<50){
                bonus = 200;
            }else {
                bonus = 500;
            }
        }
        PetUtils.addExperience(bonus, getApplicationContext());
        db.petDao().update(SELECTED_PET);
        handler.sendEmptyMessage(0);
        Toast.makeText(this, "Получено "+bonus+ " опыта.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRewardedVideoAdLeftApplication() {
    }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        bonusLoadIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.error_loading, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRewardedVideoCompleted() {
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
