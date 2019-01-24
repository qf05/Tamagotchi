package ru.levspb666.tamagotchi;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;

public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerCreate;
    private EditText inputName;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public void createPet(View view) {
        View layout = getLayoutInflater().inflate(R.layout.create_pet_dialog, null);
        spinnerCreate = layout.findViewById(R.id.inputTypePet);
        inputName = layout.findViewById(R.id.inputName);
        Button ok = layout.findViewById(R.id.okCreate);
        ok.setOnClickListener(okCreateListener);
        Button cancel = layout.findViewById(R.id.cancelCreate);
        cancel.setOnClickListener(cancelListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setCancelable(false);
        dialog = builder.create();
        ViewHelper.fonForDialog(dialog, (ImageView) layout.findViewById(R.id.fonCreateDialog));
        dialog.show();
    }

    View.OnClickListener okCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = inputName.getText().toString().trim();
            if (PetUtils.checkName(SettingsActivity.this, name)) {
                PetsType[] petsTypes = PetsType.values();
                PetsType petsType = petsTypes[spinnerCreate.getSelectedItemPosition()];
                Log.i("SELECTED_PET", petsType.toString() + "   " + name);
                SELECTED_PET = new Pet(name, petsType);
                dialog.cancel();
                finish();
            }
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.cancel();
        }
    };


    public void changePet(View view) {

    }

    public void deletePet(View view) {

    }

    public void changeNamePet(View view) {

    }
}
