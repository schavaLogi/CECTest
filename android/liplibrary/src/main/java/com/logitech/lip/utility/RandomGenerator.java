package com.logitech.lip.utility;

import android.util.Base64;

import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Responsible to generate Secure Random key
 */
public class RandomGenerator {

    public RandomGenerator() {

    }

    /**
     * Get's Secure Random Key of 16 bytes
     * @return random generated key
     */
    public String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public String getHMACSHA256String(String key, String value) {

        final String format = "UTF-8";
        final String hmacAlgo = "HmacSHA256";
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(format), "");
            Mac mac = Mac.getInstance(hmacAlgo);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(value.getBytes(format));

            return Base64.encodeToString(rawHmac , Base64.DEFAULT);

        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred ", ex);
        }
    }
}
