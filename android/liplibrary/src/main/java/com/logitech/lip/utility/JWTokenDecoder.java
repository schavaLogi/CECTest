package com.logitech.lip.utility;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.logitech.lip.ILogger;
import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;
import com.logitech.lip.R;
import com.logitech.lip.account.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

public class JWTokenDecoder {
    /**
     * Will decode ID token which is in JWT standard format
     *
     * @param idToken JWT format
     * @return UserInfo
     */
    public static UserInfo getUserInfo(String idToken) {
        if(idToken != null) {
            StringTokenizer tokenizer = new StringTokenizer(idToken, ".");
            int partCounts = tokenizer.countTokens();
            if(partCounts == 3){
                tokenizer.nextToken();
                String part2 = tokenizer.nextToken();
                tokenizer.nextToken();
                if(part2 != null) {
                    // 1. Replace any "-" sign to "+"
                    // 2. Replace any "_" sign to "/"
                    // 3. Add extra "=" padding to the string based on modulo 4

                    // Flag URL_SAFE supports all the above 3 points
                    byte[] decodeData = android.util.Base64.decode(part2, android.util.Base64.URL_SAFE);
                    String decodedString = new String(decodeData);
                    UserInfo info = null;
                    try {
                        info = new Gson().fromJson(decodedString, UserInfo.class);
                        info.setActualObject(new JSONObject(decodedString));
                    } catch (JSONException | JsonSyntaxException e) {
                        Logger.error(ILogger.LIP_E001, "JWTokenDecoder", "getUserInfo", "JsonException", e);
                    }
                    return info;
                }
            }
        }
        return null;
    }

    public static boolean isTouAccepted(String idToken) {
        boolean touAccepted = false;
        UserInfo claimsInfo = JWTokenDecoder.getUserInfo(idToken);
        if (claimsInfo != null && claimsInfo.getActualObject() != null) {
            if(LIPSdk.getAppConfiguration() != null) {
                String touKey = LIPSdk.getAppConfiguration().getAppName() +
                        LIPSdk.getContext().getString(R.string.tou_accepted);

                touAccepted = claimsInfo.getActualObject().optBoolean(touKey);
            }
        }
        return touAccepted;
    }
}
