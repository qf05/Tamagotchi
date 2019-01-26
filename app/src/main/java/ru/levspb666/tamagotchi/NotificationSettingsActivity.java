package ru.levspb666.tamagotchi;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.levspb666.tamagotchi.adapters.TimeSilenceListAdapter;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;

public class NotificationSettingsActivity extends AppCompatActivity {

    public static final String FILE_SILENCE_LIST = "silenceList.txt";
    public static final String PREFERENCES_WAKE_UP_NOTIFICATION = "WAKE_UP_NOTIFICATION";
    public static final String PREFERENCES_NOTIFICATION_OFF = "NOTIFICATION";

    private SharedPreferences settings;
    private List<Map<String, Integer>> listTimeSilence;
    private TimeSilenceListAdapter timeSilenceListAdapter;
    private TimePickerDialog timePickerDialogStart;
    private TimePickerDialog timePickerDialogStop;

    private int startHour = 13;
    private int startMinute = 35;
    private int stopHour = 13;
    private int stopMinute = 35;
    private int editItem = -1;
    private boolean wakeUpNotification = true;
    private boolean notificationOff = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();
        NotificationSettingsActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(PREFERENCES_WAKE_UP_NOTIFICATION)) {
            wakeUpNotification = settings.getBoolean(PREFERENCES_WAKE_UP_NOTIFICATION, true);
        }
        if (settings.contains(PREFERENCES_NOTIFICATION_OFF)) {
            notificationOff = settings.getBoolean(PREFERENCES_NOTIFICATION_OFF, false);
        }
        final CheckBox wakeUpCheckBox = findViewById(R.id.wake_up_checkbox);
        wakeUpCheckBox.setChecked(wakeUpNotification);
        wakeUpCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeUpNotification = !wakeUpNotification;
                wakeUpCheckBox.setChecked(wakeUpNotification);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREFERENCES_WAKE_UP_NOTIFICATION, wakeUpNotification);
                editor.apply();
            }
        });
        final CheckBox notificationCheckBox = findViewById(R.id.notification_checkbox);
        notificationCheckBox.setChecked(notificationOff);
        notificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationOff = !notificationOff;
                notificationCheckBox.setChecked(notificationOff);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(PREFERENCES_NOTIFICATION_OFF, notificationOff);
                editor.apply();
            }
        });

        listTimeSilence = fileRead(getApplicationContext());
        ListView listView = findViewById(R.id.silenceList);
        timeSilenceListAdapter = new TimeSilenceListAdapter(this, listTimeSilence);
        listView.setAdapter(timeSilenceListAdapter);
        registerForContextMenu(listView);
    }

    public void addSilence(View view){
        setTimeSilence();
    }

    public void setTimeSilence() {
        Toast t = Toast.makeText(getApplicationContext(), getString(R.string.start_silence).toUpperCase(), Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        timePickerDialogStart = new TimePickerDialog(NotificationSettingsActivity.this, myCallBackStart, startHour, startMinute, true);
        timePickerDialogStart.show();
    }

    TimePickerDialog.OnTimeSetListener myCallBackStart = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
            Toast t2 = Toast.makeText(getApplicationContext(), getString(R.string.stop_silence).toUpperCase(), Toast.LENGTH_SHORT);
            t2.setGravity(Gravity.CENTER, 0, 0);
            t2.show();
            timePickerDialogStart.cancel();

            timePickerDialogStop = new TimePickerDialog(NotificationSettingsActivity.this, myCallBackStop, stopHour, stopMinute, true);
            timePickerDialogStop.show();
        }
    };
    TimePickerDialog.OnTimeSetListener myCallBackStop = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            stopHour = hourOfDay;
            stopMinute = minute;
            timePickerDialogStop.cancel();
            if (editItem >= 0) {
                listTimeSilence.remove(editItem);
                editItem = -1;
            }
            saveList();
            timeSilenceListAdapter.notifyDataSetChanged();
        }
    };

    public static final int IDM_CHANGE = 104;
    public static final int IDM_DELETE = 105;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(android.view.Menu.NONE, IDM_CHANGE, android.view.Menu.NONE, R.string.change);
        menu.add(android.view.Menu.NONE, IDM_DELETE, android.view.Menu.NONE, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case IDM_CHANGE:
                editItem(info.position);
                return true;
            case IDM_DELETE:
                deleteItem(info.position);
                timeSilenceListAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void editItem(int item) {
        editItem = item;
        setTimeSilence();
    }

    public void deleteItem(int item) {
        listTimeSilence.remove(item);
        fileWrite();
    }

    public void saveList() {
        Map<String, Integer> map = new HashMap<>();
        map.put("startH", startHour);
        map.put("startM", startMinute);
        map.put("stopH", stopHour);
        map.put("stopM", stopMinute);
        listTimeSilence.add(map);
        fileWrite();
    }

    public void fileWrite() {
        File file = new File(getApplicationContext().getFilesDir(), FILE_SILENCE_LIST);
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
            objectOutputStream.writeObject(listTimeSilence);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Integer>> fileRead(Context context) {
        List<Map<String, Integer>> listTime = new ArrayList<>();
        File file = new File(context.getApplicationContext().getFilesDir(), FILE_SILENCE_LIST);
        if (file.exists()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                listTime = (List<Map<String, Integer>>) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listTime;
    }

    public void goBack(View view) {
        Intent intent = new Intent(NotificationSettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
