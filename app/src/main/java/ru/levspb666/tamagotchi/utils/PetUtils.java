package ru.levspb666.tamagotchi.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import ru.levspb666.tamagotchi.db.DataBase;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.History;
import ru.levspb666.tamagotchi.model.Pet;

import static ru.levspb666.tamagotchi.MainActivity.SELECTED_PET;

public class PetUtils {

    public static int ILL_TAKE_HP = 3;
    public static int TAKE_EAT = 7;
    public static int ADD_EAT = 13;
    public static int ADD_HP_EAT = 4;

    public static boolean checkName(Context context, String name) {
        if (name.isEmpty() || name.replace(" ", "").isEmpty()) {
            Toast toast = Toast.makeText(context,
                    "Имя не должно быть пустым!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else {
            if (name.trim().length() > 15) {
                Toast toast = Toast.makeText(context,
                        "Имя не должно быть длиннее 15 символов", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return false;
            } else {
                if (!Pattern.matches("\\w+", name.replace(" ", ""))) {
                    Toast toast = Toast.makeText(context,
                            "Некорректное имя!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public static List<Pet> sort(List<Pet> pets, int position) {
        switch (position) {
            case 1:
                Collections.sort(pets, new Comparator<Pet>() {
                    @Override
                    public int compare(Pet o1, Pet o2) {
                        if (o1.getLvl() != o2.getLvl()) {
                            return Integer.compare(o1.getLvl(), o2.getLvl()) * -1;
                        } else {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        }
                    }
                });
                break;
            default: {
                Collections.sort(pets, new Comparator<Pet>() {
                    @Override
                    public int compare(Pet o1, Pet o2) {
                        if (o1.getName().compareToIgnoreCase(o2.getName()) != 0) {
                            return o1.getName().compareToIgnoreCase(o2.getName());
                        } else {
                            return Integer.compare(o1.getLvl(), o2.getLvl());
                        }
                    }
                });
            }
        }
        return pets;
    }

    public static void addExperience(int add, Context context) {
        int exp = SELECTED_PET.getExperience();
        exp += add;
        if (exp >= (int) (50 + 200 * SELECTED_PET.getLvl() + Math.pow(1.1, SELECTED_PET.getLvl() + 25)) / 6) {
            lvlUp(exp, context);
        } else {
            SELECTED_PET.setExperience(exp);
        }
    }

    private static void lvlUp(int exp,Context context) {
        exp -= SELECTED_PET.getExperience();
        SELECTED_PET.setLvl(SELECTED_PET.getLvl() + 1);
        SELECTED_PET.setExperience(exp);
        ViewHelper.playClick(context, ActionType.LVLUP);
        DataBase.getAppDatabase(context).historyDao().insert(new History(
                Calendar.getInstance().getTimeInMillis(),
                ActionType.LVLUP.toString(),
                SELECTED_PET.getId()));
    }

}
