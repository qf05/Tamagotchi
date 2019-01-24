package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);
        Button home = findViewById(R.id.toHome);
        home.setOnClickListener(onClickListener);
        TextView textView = findViewById(R.id.hello);
        final Animation helloAnimation = AnimationUtils.loadAnimation(this, R.anim.hello);
        textView.startAnimation(helloAnimation);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OtherActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };
}