package pl.basistam.wloczykij.components.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {
    public static void hide(Context context, View view) {
        if (view != null) {
            InputMethodManager inputMethodManager
                    = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            }
        }
    }

    public static void show(Context context) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
}
