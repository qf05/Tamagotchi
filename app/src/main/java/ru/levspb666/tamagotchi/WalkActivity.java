package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.utils.ViewHelper;

public class WalkActivity extends AppCompatActivity {
    private int height;
    private int width;
    private int thisX;
    private int thisY;
    private int nextX;
    private int nextY;
    private ImageView petView;
    public static PetsType PET;

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
                thisX = (int) petView.getX() + 300;
                thisY = (int) petView.getY() + 300;
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

        Animation translateAnimation = new TranslateAnimation(thisX, nextX, thisY, nextY);
        translateAnimation.setDuration((long) (Math.random() * 1000 + 100));
        translateAnimation.setAnimationListener(animationListener);
        petView.startAnimation(translateAnimation);
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            thisX = nextX;
            thisY = nextY;
            startAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public void goHome(View view) {
        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
