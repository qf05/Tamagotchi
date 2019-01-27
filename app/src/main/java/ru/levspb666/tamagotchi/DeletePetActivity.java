package ru.levspb666.tamagotchi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.levspb666.tamagotchi.adapters.DeleteRVAdapter;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.AlarmUtils;
import ru.levspb666.tamagotchi.utils.PetUtils;
import ru.levspb666.tamagotchi.utils.ViewHelper;

import static ru.levspb666.tamagotchi.MainActivity.PETS;

public class DeletePetActivity extends AppCompatActivity implements DeleteRVAdapter.ItemClickListener {

    private DeleteRVAdapter adapter;
    private Map<Integer, View> deleteMap = new HashMap<>();
    private List<Pet> pets;
    private static DataBase db;
    private Button back;
    private Button delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_pet_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();
        DeletePetActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        back = findViewById(R.id.backFromDel);
        delete = findViewById(R.id.delPet);
        RecyclerView rv = findViewById(R.id.delete_rv);
        rv.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager llm = new LinearLayoutManager(DeletePetActivity.this);
            rv.setLayoutManager(llm);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                    llm.getOrientation());
            rv.addItemDecoration(dividerItemDecoration);
        }else {
            GridLayoutManager glm = new GridLayoutManager(DeletePetActivity.this,2);
            rv.setLayoutManager(glm);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                    glm.getOrientation());
            rv.addItemDecoration(dividerItemDecoration);
        }


        db = DataBase.getAppDatabase(getApplicationContext());
        pets = db.petDao().getAll();
        Spinner sortSpinner = findViewById(R.id.deleteSort);
        String[] stringArray = getResources().getStringArray(R.array.sort);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_adapter,R.id.spinner_text,stringArray);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PetUtils.sort(pets, position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PetUtils.sort(pets, 0);
            }
        });

        adapter = new DeleteRVAdapter(this, pets);
        adapter.setClickListener(DeletePetActivity.this);
        rv.setAdapter(adapter);
    }

    private void sureDialog() {
        View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
        Button ok = layout.findViewById(R.id.okDelete);
        Button cancel = layout.findViewById(R.id.cancelDelete);
        final ImageView fon = layout.findViewById(R.id.fon_delete_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
        ViewHelper.fonForDialog(dialog, fon);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteMap.size() > 0) {
                    List<Pet> deleteList = new ArrayList<>();
                    for (int i : deleteMap.keySet()) {
                        deleteList.add(pets.get(i));
                        CheckBox checkBox = (CheckBox) deleteMap.get(i);
                        checkBox.setChecked(false);
                    }
                    for (Pet pet : deleteList) {
                        db.petDao().delete(pet);
                        pets.remove(pet);
                        PETS.remove(pet);
                        AlarmUtils.cancelAllAlarm(getApplicationContext(),pet);
                    }
                }
                ViewHelper.playClick(DeletePetActivity.this, ActionType.DIE);
                dialog.cancel();
                Toast toast = Toast.makeText(DeletePetActivity.this,
                        "Удалено " + deleteMap.size() + " питомцев", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                deleteMap.clear();
                adapter.notifyDataSetChanged();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.playClick(DeletePetActivity.this, ActionType.DIE);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            deleteMap.put(position, view);
            Log.i("CHECKBOX", position + "");
        } else {
            if (deleteMap.containsKey(position)) {
                deleteMap.remove(position);
                Log.i("CHECKBOX", "remove " + position);
            }
        }
    }

    public void delete(View view) {
        delete.setClickable(false);
        ViewHelper.playClick(DeletePetActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(DeletePetActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                delete.setClickable(true);
                sureDialog();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        delete.startAnimation(animation);

    }

    public void goBack(View view) {
        back.setClickable(false);
        ViewHelper.playClick(DeletePetActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(DeletePetActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(DeletePetActivity.this, SettingsActivity.class);
                startActivity(intent);
                back.setClickable(true);
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        back.startAnimation(animation);
    }
}
