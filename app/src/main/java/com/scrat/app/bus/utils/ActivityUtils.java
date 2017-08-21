package com.scrat.app.bus.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {
    private ActivityUtils() {
    }

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment,
                                             int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    public static void hideKeyboard(Activity ctx) {
        InputMethodManager imm =
                (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(ctx.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private static class HandlerSingletonHolder {
        private static Handler instance = new Handler(Looper.getMainLooper());
    }

    public static Handler getMainHandler() {
        return HandlerSingletonHolder.instance;
    }

}
