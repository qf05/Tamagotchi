package ru.levspb666.tamagotchi.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.regex.Pattern;

public class PetUtils {

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
}
