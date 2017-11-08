package com.logitech.lip.ui.login;

import android.view.View;

public interface KeyboardCommandListener {
    /**
     * Used to close keyboard in case if is open
     */
    void hideKeyboard();

    /**
     * Show keypad with provided view as focused
     * @param view
     */
    void showKeyboard(View view);

}
