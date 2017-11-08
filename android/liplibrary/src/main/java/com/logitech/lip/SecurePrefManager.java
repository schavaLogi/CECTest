/*
 * Copyright (c) 2015 Logitech, Inc. All Rights Reserved
 *
 * This program is a trade secret of LOGITECH, and it is not to be reproduced,
 * published, disclosed to others, copied, adapted, distributed or displayed
 * without the prior written authorization of LOGITECH.
 *
 * Licensee agrees to attach or embed this notice on all copies of the program
 * including partial copies or modified versions thereof.
 */

package com.logitech.lip;


import android.content.Context;
import android.content.SharedPreferences;

import com.logitech.lip.utility.KeyUtility;


/*
* Secure Data Manager to save Data in Preferences
* Responsibility is to create KeyPair with alias key.
* Uses RSA Private key to decrypt Data and Public key to Encrypt data
*
*/
public final class SecurePrefManager {

    private static final String PREF_FILE_NAME = "lipClient";



    private final SharedPreferences sharedpreferences;

    private SharedPreferences.Editor editor;

    public SecurePrefManager(Context ctx) {
        sharedpreferences = ctx.getSharedPreferences(PREF_FILE_NAME, Context.MODE_APPEND);
    }

    private boolean openEditor() {
        if (sharedpreferences != null) {
            editor = sharedpreferences.edit();
            return true;
        }
        return false;
    }

    private void closeEditor() {
        if (editor != null) {
            editor.commit();
        }
    }

    /**
    * Will save data in Preference in Key , value pair utilizes RSA Encryption
    * @param key  key Name
    * @param data data to Save
    * @param isSecure to indicate apply encryption
    * @return true upon success
    */
    public boolean saveData(String key, String data, boolean isSecure) {

        String encData = data;
        if (isSecure) {
            encData = KeyUtility.encryptString(data);
        }
        return saveData(key, encData);
    }

    private boolean saveData(String key, String data) {
        if (openEditor()) {
            editor.putString(key, data);
            closeEditor();
            return true;
        }
        return false;
    }

    /**
    * Will read data from Preference if given Key present
    * @param key  key Name
    * @param defaultData defaultData if key not present
    * @param isSecure to indicate encryption applied while saving data
    * @return saved data in string format
    * @see saveData
    */
    public String getData(String key, String defaultData, boolean isSecure) {
        String result = sharedpreferences.getString(key, defaultData);
        if (result != null && isSecure) {
            result = KeyUtility.decryptString(result);

            // Remove key , Decrypt might be failed due to keystore
            if(result == null){
                clearKey(key);
            }
        }
        return result;
    }

    /**
    * Will clear all preference (all key's)
    * @return true upon success
    */
    public boolean clearAll() {
        if (openEditor()) {
            editor.clear();
            closeEditor();
            return true;
        }
        return false;
    }

    /**
    * Will clear given key from
    * @return true upon success
    */
    public boolean clearKey(String key) {
        if (openEditor()) {
            editor.remove(key);
            closeEditor();
            return true;
        }
        return false;
    }
}
