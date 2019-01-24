package ru.levspb666.tamagotchi.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Objects;

public class ViewHelper {
    public static void executeAfterViewHasDrawn(final View v, final Runnable cb) {
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                cb.run();
                ViewTreeObserver obs = v.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    public static void fonForDialog(final Dialog dialog, final ImageView fon){
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                final int i = Objects.requireNonNull(dialog.getWindow()).getDecorView().getHeight();
                RelativeLayout.LayoutParams params = new  RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        i-50
                );
                fon.setLayoutParams(params);
            }
        });
    }
}
