package ru.levspb666.tamagotchi;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Objects;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.History;
import ru.levspb666.tamagotchi.utils.AlarmUtils;
import ru.levspb666.tamagotchi.utils.NotificationUtils;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;
import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;
import static ru.levspb666.tamagotchi.MainActivity.SOUND_OFF;

public class WalkActivity extends AppCompatActivity {
    private int height;
    private int width;
    private int thisX;
    private int thisY;
    private int nextX;
    private int nextY;
    private ImageView petView;
    private MediaPlayer mp;
    private AnimatorSet animatorSet;
    private ProgressBar progressBar;
    private Button home;
    private boolean complete;
    private int indent;
    private DataBase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();
        WalkActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        petView = findViewById(R.id.petWalk);
        progressBar = findViewById(R.id.walkProgressBar);
        progressBar.setMax(9 + SELECTED_PET.getLvl() / 2);
        progressBar.setProgress(0);
        complete = false;
        home = findViewById(R.id.toHomeFromWalk);
        home.setClickable(false);
        home.setAlpha(0.3f);
        complete = false;
        switch (PetsType.valueOf(SELECTED_PET.getType())) {
            case CAT:
                petView.setImageResource(R.drawable.cat);
                break;
            case DOG:
                petView.setImageResource(R.drawable.dog);
                // http://www.cyberforum.ru/android-dev/thread1648514.html
                // получаем параметры
                ViewGroup.LayoutParams params = petView.getLayoutParams();
                // меняем высоту. Если уползёт выравнивание, то imageView.getLayoutParams().width = MyHeight;
                params.height = (int) (petView.getLayoutParams().height * 1.8);
                // меняем параметр
                petView.setLayoutParams(params);
                break;
            case CTHULHU:
                petView.setImageResource(R.drawable.cthulhu);
                break;
        }
        petView.setOnClickListener(onClickListener);
        db = DataBase.getAppDatabase(getApplicationContext());
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("sony-d6633-CB5A25TGZ3")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        getSize();
    }

    private void getSize() {
        ViewHelper.executeAfterViewHasDrawn(petView, new Runnable() {
            @Override
            public void run() {
                // Получаем размеры
                indent = progressBar.getBottom() + 100;
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                height = size.y - petView.getHeight() - 200;
                width = size.x - petView.getWidth();
                nextX = width / 2;
                nextY = height / 2;
                petView.setX(nextX);
                petView.setY(nextY);
                ObjectAnimator rotate = ObjectAnimator.ofFloat(petView, View.ROTATION, -90);
                rotate.setDuration(1);
                rotate.addListener(aListener);
                rotate.start();
            }
        });
    }

    private void startAnimation() {
        do {
            nextX = thisX + (int) (Math.random() * width) - width / 2;
        } while (nextX < 0 || nextX > width);

        do {
            nextY = thisY + (int) (Math.random() * height) - height / 2;
            if (progressBar.getProgress() < progressBar.getMax()) {
                nextY += indent;
            }
        } while (nextY < 0 || nextY > height);

        float nextAngle = (float) Math.toDegrees(Math.atan2(thisY - nextY, thisX - nextX));
        int rotationDuration = (int) (Math.random() * 500 + 100);
        int time = (int) (Math.random() * 1000 + 100);

        // https://stackoverflow.com/questions/28352352/change-multiple-properties-with-single-objectanimator
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, nextX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, nextY);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(petView, pvhX, pvhY);
        animator.setDuration(time);
        animator.setStartDelay((long) (rotationDuration - Math.random() * 200));

        ObjectAnimator rotate = ObjectAnimator.ofFloat(petView, View.ROTATION, nextAngle - 90);
        rotate.setDuration(rotationDuration);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotate, animator);
        animatorSet.addListener(aListener);
        animatorSet.start();
    }

    Animator.AnimatorListener aListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            thisX = nextX;
            thisY = nextY;
            startAnimation();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("WALK", "Click on pet");
            switch (PetsType.valueOf(SELECTED_PET.getType())) {
                case CAT:
                    playSound(R.raw.cat);
                    break;
                case DOG:
                    playSound(R.raw.dog);
                    break;
                case CTHULHU:
                    playSound(R.raw.cthulhu);
                    break;
            }

            progressBar.incrementProgressBy(1);
            if (!complete && progressBar.getProgress() == progressBar.getMax()) {
                complete = true;
                progressBar.setVisibility(View.INVISIBLE);
                home.setClickable(true);
                home.setAlpha(1f);
                db.historyDao().insert(new History(
                        Calendar.getInstance().getTimeInMillis(),
                        ActionType.WALK.toString(),
                        SELECTED_PET.getId()));
                AlarmUtils.cancelAlarm(getApplicationContext(), ActionType.WALK, SELECTED_PET);
                SELECTED_PET.setNextWalk(AlarmUtils.nextWalk());
                PetUtils.addExperience(15,WalkActivity.this);
                db.petDao().update(SELECTED_PET);
                AlarmUtils.setAlarm(getApplicationContext(), ActionType.WALK, SELECTED_PET);
                AlarmUtils.cancelAlarmIllWithCheck(getApplicationContext(), SELECTED_PET);
                NotificationUtils.notificationCancel(getApplicationContext(), ActionType.WALK, SELECTED_PET);
            }
        }
    };

    private void playSound(final int resource) {
        if (!SOUND_OFF) {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
            new Thread() {
                public void run() {
                    mp = MediaPlayer.create(WalkActivity.this, resource);
                    mp.start();
                }
            }.start();
        }
    }

    public void goHome(View view) {
        home.setClickable(false);
        ViewHelper.playClick(WalkActivity.this, ActionType.WALK);
        Animation animation = AnimationUtils.loadAnimation(WalkActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(WalkActivity.this, MainActivity.class);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.cancel();
            animatorSet.end();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }
}