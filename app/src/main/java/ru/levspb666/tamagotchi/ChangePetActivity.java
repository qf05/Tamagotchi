package ru.levspb666.tamagotchi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.List;

import ru.levspb666.tamagotchi.adapters.ChangeRVAdapter;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.model.Pet;
import ru.levspb666.tamagotchi.utils.PetUtils;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;
import static ru.levspb666.tamagotchi.MainActivity.PREFERENCES_SELECTED_PET;
import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;


public class ChangePetActivity extends AppCompatActivity implements ChangeRVAdapter.ItemClickListener {

    private ChangeRVAdapter adapter;
    private List<Pet> pets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pet_activity);

        final RecyclerView rv = findViewById(R.id.change_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(ChangePetActivity.this);
        rv.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                llm.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        pets = DataBase.getAppDatabase(getApplicationContext()).petDao().getAll();
        adapter = new ChangeRVAdapter(ChangePetActivity.this, pets);
        adapter.setClickListener(ChangePetActivity.this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        SELECTED_PET = adapter.getItem(position);
        Intent intent = new Intent(ChangePetActivity.this, MainActivity.class);
        startActivity(intent);
        Log.i("SELECTED_PET", SELECTED_PET.getName());
        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFERENCES_SELECTED_PET, SELECTED_PET.getId());
        editor.apply();
        finish();
    }

    public void goHome(View view) {
        Intent intent = new Intent(ChangePetActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        Intent intent = new Intent(ChangePetActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
