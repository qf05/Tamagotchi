package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.levspb666.tamagotchi.enums.PetsType;

import static ru.levspb666.tamagotchi.WalkActivity.PET;

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
                    PET = PetsType.CAT;
                    break;
                case R.id.buttonDog:
                    PET = PetsType.DOG;
                    break;
                case R.id.buttonCthulhu:
                    PET = PetsType.CTHULHU;
                    break;
            }
            Intent intent = new Intent(MainActivity.this, WalkActivity.class);
            startActivity(intent);
        }
    };

    public void onButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
        startActivity(intent);
    }
}
