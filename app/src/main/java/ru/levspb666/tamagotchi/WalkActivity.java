package ru.levspb666.tamagotchi;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class WalkActivity extends AppCompatActivity {
    private int height;
    private int width;
    private int thisX;
    private int thisY;
    private int nextX;
    private int nextY;
    private ImageView petView;
    public static PetsType PET;
    private AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_activity);
        petView = findViewById(R.id.petWalk);
        switch (PET) {
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
        getSize();
    }

    private void getSize() {
        ViewHelper.executeAfterViewHasDrawn(petView, new Runnable() {
            @Override
            public void run() {
                // Получаем размеры
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                height = size.y - petView.getHeight() - 200;
                width = size.x - petView.getWidth();
                petView.setRotation(-90);
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        do {
            nextX = thisX + (int) (Math.random() * width - width / 1.5);
        } while (nextX < 0 || nextX > width);

        do {
            nextY = thisY + (int) (Math.random() * height - height / 1.5);
        } while (nextY < 0 || nextY > height);

        float nextAngle = (float) Math.toDegrees(Math.atan2(thisY - nextY, thisX - nextX));
        int rotationDuration = (int) (Math.random() * 500 + 500);
        int time = (int) (Math.random() * 10000 + 100);

        // https://stackoverflow.com/questions/28352352/change-multiple-properties-with-single-objectanimator
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, nextX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, nextY);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(petView, pvhX, pvhY);
        animator.setDuration(time);
        animator.setStartDelay((long) (rotationDuration - Math.random() * 300));

        ObjectAnimator rotate = ObjectAnimator.ofFloat(petView, View.ROTATION, nextAngle - 90);
        rotate.setDuration(rotationDuration);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotate, animator);
        animatorSet.addListener(animatorListener);
        animatorSet.start();
    }

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
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

    public void goHome(View view) {
        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
