package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import ru.levspb666.tamagotchi.utils.ViewHelper;

public class OtherActivity extends AppCompatActivity {

    private int countAnimationRepeat = 1;
    private Animation translateAnimation;
    private TextView textView;
    private int height;
    private int width;
    private int yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);
        Button home = findViewById(R.id.toHome);
        home.setOnClickListener(onClickListener);
        textView = findViewById(R.id.hello);
        final Animation helloAnimation = AnimationUtils.loadAnimation(this, R.anim.hello);
        final AnimationSet animationSet = new AnimationSet(true);

        // http://poetofcode.ru/programming/2017/06/12/kak-opredelit-nachalnyue-razmeryu-view-v-android.html
        ViewHelper.executeAfterViewHasDrawn(textView, new Runnable() {
            @Override
            public void run() {
                // Получаем размеры
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                height = size.y - textView.getHeight() - 300;
                // -(int)textView.getX() требуется, потому что у textView указано свойство  android:layout_centerHorizontal="true"
                width = size.x / 2 - textView.getWidth() / 2 - (int) textView.getX();

                //создаем анимацию
                translateAnimation = new TranslateAnimation(width, width, 10, height);
                translateAnimation.setDuration(2000);
                translateAnimation.setStartOffset(2000);
                translateAnimation.setAnimationListener(animationListener);
                animationSet.addAnimation(translateAnimation);
                animationSet.addAnimation(helloAnimation);
                textView.startAnimation(animationSet);
            }
        });
    }

    private void startAnimation() {
        translateAnimation.setDuration(2000 / countAnimationRepeat);
        translateAnimation.setAnimationListener(animationListener);
        textView.startAnimation(translateAnimation);
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            countAnimationRepeat++;
            if (countAnimationRepeat < 10) {
                if (countAnimationRepeat % 2 == 0) {
                    yDelta = height - (height / countAnimationRepeat);
                    translateAnimation = new TranslateAnimation(width, width, height, yDelta);
                    startAnimation();
                } else {
                    translateAnimation = new TranslateAnimation(width, width, yDelta, height);
                    startAnimation();
                }
            } else {
                textView.setX(width + (int) textView.getX());
                textView.setY(height);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OtherActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
}