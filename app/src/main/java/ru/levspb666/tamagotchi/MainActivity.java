package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button cat;
    Button dog;
    Button cthulhu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cat = findViewById(R.id.buttonCat);
        dog = findViewById(R.id.buttonDog);
        cthulhu = findViewById(R.id.buttonCthulhu);
        cat.setOnClickListener(buttonListener);
        dog.setOnClickListener(buttonListener);
        cthulhu.setOnClickListener(buttonListener);
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonCat:
                    break;
                case R.id.buttonDog:
                    break;
                case R.id.buttonCthulhu:
                    break;
            }
        }
    };

    public void onButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        startActivity(intent);
    }
}
