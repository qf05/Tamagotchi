package ru.levspb666.tamagotchi.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.levspb666.tamagotchi.MainActivity;
import ru.levspb666.tamagotchi.NotificationSettingsActivity;
import ru.levspb666.tamagotchi.R;
import ru.levspb666.tamagotchi.enums.ActionType;
import ru.levspb666.tamagotchi.model.Pet;

import static ru.levspb666.tamagotchi.MainActivity.APP_PREFERENCES;
import static ru.levspb666.tamagotchi.NotificationSettingsActivity.PREFERENCES_NOTIFICATION_OFF;
import static ru.levspb666.tamagotchi.NotificationSettingsActivity.PREFERENCES_WAKE_UP_NOTIFICATION;

public class NotificationUtils {
    //http://qaru.site/questions/177262/notificationcompatbuilder-deprecated-in-android-o
    //https://startandroid.ru/ru/uroki/vse-uroki-spiskom/509-android-notifications-osnovy.html
    public static void notification(Context context, Pet pet, ActionType action) {
        boolean wakeUpNotification = true;
        boolean notificationOff = false;

        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(PREFERENCES_WAKE_UP_NOTIFICATION)) {
            wakeUpNotification = settings.getBoolean(PREFERENCES_WAKE_UP_NOTIFICATION, true);
        }
        if (settings.contains(PREFERENCES_NOTIFICATION_OFF)) {
            notificationOff = settings.getBoolean(PREFERENCES_NOTIFICATION_OFF, false);
        }
        if (!notificationOff) {
            if (!ActionType.WAKEUP.equals(action) || wakeUpNotification) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = "tamagotchi";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            "Tamagotchi",
                            NotificationManager.IMPORTANCE_NONE);
                    notificationChannel.setDescription("Channel description");
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.GREEN);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);
                    Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

                Intent notificationIntent = new Intent(context, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(),
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(checkSilenceTime(context))
                        .setSmallIcon(R.drawable.small_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.big_icon))
                        .setContentTitle(pet.getName())
                        .setContentIntent(contentIntent);

                switch (action) {
                    case EAT:
                        builder.setContentText(" проголодался!")
                                .setTicker(pet.getName() + " проголодался!");
                        break;
                    case WALK:
                        builder.setContentText(" хочет гулять!")
                                .setTicker(pet.getName() + " хочет гулять!");
                        break;
                    case ILL:
                        builder.setContentText(" заболел!")
                                .setTicker(pet.getName() + " заболел!");
                        break;
                    case SLEEP:
                        builder.setContentText(" хочет спать!")
                                .setTicker(pet.getName() + " хочет спать!");
                        break;
                    case WAKEUP:
                        builder.setContentText(" проснулся")
                                .setTicker(pet.getName() + " проснулся");
                        break;
                    case SHIT:
                        builder.setContentText(" покакал!")
                                .setTicker(pet.getName() + " покакал!");
                        break;
                    case CURE:
                        builder.setContentText(" может умереть!")
                                .setTicker(pet.getName() + " может умереть!");
                        break;
                    case DIE:
                        builder.setContentText(" умер! ;(")
                                .setTicker(pet.getName() + " умер!");
                        break;
                }

                if (notificationManager != null) {
                    notificationManager.notify(action.toString(), (int) pet.getId(), builder.build());
                }
            }
        }
    }

    public static void notificationCancel(Context context, ActionType action, Pet pet) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(action.toString(), (int) pet.getId());
    }

    private static long checkSilenceTime(Context context) {
        List<Map<String, Integer>> listTime = NotificationSettingsActivity.fileRead(context);
        Calendar calendar = Calendar.getInstance();
        if (listTime != null && listTime.size() > 0) {
            int sth;
            int stm;
            int sph;
            int spm;
            for (int i = 0; i < listTime.size(); i++) {
                Map<String, Integer> map = listTime.get(i);
                sth = map.get("startH");
                stm = map.get("startM");
                sph = map.get("stopH");
                spm = map.get("stopM");
                if (sth == sph && stm == spm) {
                    return calendar.getTimeInMillis();
                } else {
                    if (sth < sph) {
                        if (calendar.get(Calendar.HOUR) >= sth && calendar.get(Calendar.HOUR) <= sph) {
                            if (calendar.get(Calendar.HOUR) == sph) {
                                if (calendar.get(Calendar.MINUTE) < spm) {
                                    return lay(sph, spm);
                                } else {
                                    return calendar.getTimeInMillis();
                                }
                            } else {
                                if (calendar.get(Calendar.HOUR) == sth) {
                                    if (calendar.get(Calendar.MINUTE) >= stm) {
                                        return lay(sph, spm);
                                    } else {
                                        return calendar.getTimeInMillis();
                                    }
                                } else {
                                    return lay(sph, spm);
                                }
                            }
                        }
                    } else {
                        if (sth == sph && calendar.get(Calendar.HOUR) == sth) {
                            if (stm > spm) {
                                if (calendar.get(Calendar.MINUTE) >= stm) {
                                    return layToNextDay(sph, spm);
                                } else {
                                    if (calendar.get(Calendar.MINUTE) < spm) {
                                        return lay(sph, spm);
                                    } else {
                                        return calendar.getTimeInMillis();
                                    }
                                }
                            } else {
                                if (calendar.get(Calendar.MINUTE) > stm && calendar.get(Calendar.MINUTE) < spm) {
                                    return lay(sph, spm);
                                } else {
                                    return calendar.getTimeInMillis();
                                }
                            }
                        } else {
                            if (calendar.get(Calendar.HOUR) <= sph || calendar.get(Calendar.HOUR) >= sth) {
                                if (calendar.get(Calendar.HOUR) == sth) {
                                    if (calendar.get(Calendar.MINUTE) >= stm) {
                                        return layToNextDay(sph, spm);
                                    } else {
                                        return calendar.getTimeInMillis();
                                    }
                                } else {
                                    if (calendar.get(Calendar.HOUR) == sph) {
                                        if (calendar.get(Calendar.MINUTE) < spm) {
                                            return lay(sph, spm);
                                        } else {
                                            return calendar.getTimeInMillis();
                                        }
                                    } else {
                                        if (calendar.get(Calendar.HOUR) <= 23 && calendar.get(Calendar.HOUR) > sth) {
                                            return layToNextDay(sph, spm);
                                        } else {
                                            return lay(sph, spm);
                                        }
                                    }
                                }
                            } else {
                                return calendar.getTimeInMillis();
                            }
                        }
                    }
                }
            }
        }
        return calendar.getTimeInMillis();
    }

    private static long lay(int h, int m) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.YEAR);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                h, m);
        return calendar.getTimeInMillis();
    }

    private static long layToNextDay(int h, int m) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.YEAR);
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                h, m);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTimeInMillis();
    }
}
