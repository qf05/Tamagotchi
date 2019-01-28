package ru.levspb666.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.levspb666.tamagotchi.adapters.HistoryListAdapter;
import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.History;
import ru.levspb666.tamagotchi.utils.ViewHelper;

public class HistoryActivity extends AppCompatActivity {
    Map<Long, String> names = new HashMap<>();
    private List<History> histories = new ArrayList<>();
    private List<HistoryEntry> historyEntries;
    public static int spinnerPosition = 0;
    private DataBase db;
    private Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        back = findViewById(R.id.backFromHistory);
        db = DataBase.getAppDatabase(HistoryActivity.this);
        histories = db.historyDao().getAll();
        getNamePets();
        ExpandableListView expandableListView = findViewById(R.id.historyListView);
        getMapHistories(0);
        final HistoryListAdapter adapter = new HistoryListAdapter(HistoryActivity.this, historyEntries, names);
        expandableListView.setAdapter(adapter);

        Spinner sortSpinner = findViewById(R.id.historySort);
        String[] stringArray = getResources().getStringArray(R.array.grouping);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_adapter, R.id.spinner_text, stringArray);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition = position;
                getMapHistories(spinnerPosition);
                adapter.setList(historyEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getMapHistories(0);
            }
        });

    }

    public void goBack(View view) {
        back.setClickable(false);
        ViewHelper.playClick(HistoryActivity.this, ActionType.DIE);
        Animation animation = AnimationUtils.loadAnimation(HistoryActivity.this, R.anim.click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(HistoryActivity.this, SettingsActivity.class);
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

    public class HistoryEntry {
        private String parentItem;
        private List<History> list;

        public HistoryEntry(String parentItem, List<History> list) {
            this.parentItem = parentItem;
            this.list = list;
        }

        public String getParentItem() {
            return parentItem;
        }

        public List<History> getList() {
            return list;
        }
    }

    private Map<Long, String> getNamePets() {
        for (History history : histories) {
            String name = db.petDao().findById(history.getPetId()).getName();
            names.put(history.getPetId(), name);
        }
        return names;
    }

    private void getMapHistories(int position) {
        Map<String, List<History>> map = new HashMap<>();
        historyEntries = new ArrayList<>();
        switch (position) {
            case 1:
                for (History history : histories) {
                    List<History> child = map.get(names.get(history.getPetId()));
                    if (child == null) {
                        child = new ArrayList<>();
                    }
                    child.add(history);
                    map.put(names.get(history.getPetId()), child);
                }

                break;
            case 2:
                for (History history : histories) {
                    List<History> child = map.get(history.getAction());
                    if (child == null) {
                        child = new ArrayList<>();
                    }
                    child.add(history);
                    map.put(history.getAction(), child);
                }
                break;
            default:
                for (History history : histories) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.setTimeInMillis(history.getDate());
                    SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    List<History> child = map.get(format.format(calendar.getTime()));
                    if (child == null) {
                        child = new ArrayList<>();
                    }
                    child.add(history);
                    map.put(format.format(calendar.getTime()), child);
                }
        }

        for (Map.Entry<String, List<History>> entry : map.entrySet()) {
            historyEntries.add(new HistoryEntry(entry.getKey(), entry.getValue()));
        }
    }

}
