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

package com.logitech.lip.utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.logitech.lip.LIPSdk;
import com.logitech.lip.Logger;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class KeyUtility {

    private static final String TAG = KeyUtility.class.getSimpleName();

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String KEYSTORE_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final String ALIAS_KEY = "lipClient";

    private static final String ALGORITHM_AES = "AES";

    private static final String SEED_AES = "1a2s3dfdsf67nsdfsd5sfddf5dfd";

    @TargetApi(18)
    public static boolean createNewKeys(Context ctx) {
        if(Build.VERSION.SDK_INT < 18) {
            return false;
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            // Create new key if needed
            if (keyStore.containsAlias(ALIAS_KEY)) {
                return true;
            }
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 40);

            if (Build.VERSION.SDK_INT >= 23) {
                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(ALIAS_KEY,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

                KeyGenParameterSpec keySpec = builder
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setCertificateSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setCertificateSerialNumber(BigInteger.ONE)
                        .setCertificateNotBefore(start.getTime())
                        .setCertificateNotAfter(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER);
                generator.initialize(keySpec);
                generator.generateKeyPair();

            } else if (Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 23) {
                // API 23 need to Change only Builder approach
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                        .setAlias(ALIAS_KEY)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();


                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER);
                generator.initialize(spec);
                generator.generateKeyPair();
            }
            return true;
        } catch (Exception e) {
            Logger.debug(TAG, "createNewKeys" , "Exception =" + e.getMessage() +
                    "OS Version =" + Build.VERSION.SDK_INT);
        }
        return false;
    }

    public static String encryptString(String actualData) {
        if (actualData == null) {
            return null;
        }
        try {
            if (android.os.Build.VERSION.SDK_INT < 18 || !LIPSdk.isKeyStoreReady()) {
                return simpleAesEncrypt(SEED_AES, actualData);
            }
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(ALIAS_KEY, null);
            if(privateKeyEntry != null) {
                PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

                Cipher input = Cipher.getInstance(KEYSTORE_TRANSFORMATION);
                input.init(Cipher.ENCRYPT_MODE, publicKey);

                byte[] encrypted = blockCipher(input, actualData.getBytes(), Cipher.ENCRYPT_MODE);

                byte[] encode = Base64.encode(encrypted, Base64.NO_PADDING | Base64.NO_WRAP);
                return new String(encode);
            }


            /*  LOGIC for Small Strings will not work for Bigger strings

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    CipherOutputStream cipherOutputStream = new CipherOutputStream(
                            outputStream, input);
                    cipherOutputStream.write(actualData.getBytes("UTF-8"));
                    cipherOutputStream.close();

                    byte[] encRawData = outputStream.toByteArray();
                    return Base64.encodeToString(encRawData, Base64.DEFAULT);

            */
        } catch (Exception e) {
            Logger.debug(TAG, "encryptString" , "Exception =" + e.getMessage());
        }
        return null;
    }


    public static String decryptString(String encData) {
        if (encData == null) {
            return null;
        }
        try {
            if (android.os.Build.VERSION.SDK_INT < 18 || !LIPSdk.isKeyStoreReady()) {
                return simpleAesDecrypt(SEED_AES, encData);
            }

            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(ALIAS_KEY, null);

            if(privateKeyEntry != null) {
                Cipher output = Cipher.getInstance(KEYSTORE_TRANSFORMATION);
                output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

                byte[] encryptedBytes = Base64.decode(encData, Base64.NO_PADDING | Base64.NO_WRAP);
                byte[] decrypted = blockCipher(output, encryptedBytes, Cipher.DECRYPT_MODE);

                return new String(decrypted, "UTF-8").replaceAll("\u0000", "");
            }


            /*  LOGIC for Small Strings will not work for Bigger strings

                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(encData, Base64.DEFAULT)), output);

                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i).byteValue();
                }
                return new String(bytes, 0, bytes.length, "UTF-8");

            */

        } catch (Exception e) {
            Logger.debug(TAG, "decryptString" , "Exception =" + e.getMessage());
        }
        return null;
    }

    private static byte[] blockCipher(Cipher input, byte[] bytes, int mode) throws IllegalBlockSizeException,
            BadPaddingException {
        // string initialize 2 buffers.
        // scrambled will hold intermediate results
        byte[] scrambled = new byte[0];

        // toReturn will hold the total result
        byte[] toReturn = new byte[0];
        // if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
        int length = (mode == Cipher.ENCRYPT_MODE) ? 245 : 256;
        // int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;
        // another buffer. this one will hold the bytes that have to be modified in this step
        byte[] buffer = new byte[length];

        for (int i=0; i< bytes.length; i++){

            // if we filled our buffer array we have our block ready for de- or encryption
            if ((i > 0) && (i % length == 0)){
                //execute the operation
                scrambled = input.doFinal(buffer);
                // add the result to our total result.
                toReturn = append(toReturn,scrambled);
                // here we calculate the length of the next buffer required
                int newlength = length;

                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                if (i + length > bytes.length) {
                    newlength = bytes.length - i;
                }
                // clean the buffer array
                buffer = new byte[newlength];
            }
            // copy byte into our buffer.
            buffer[i%length] = bytes[i];
        }

        // this step is needed if we had a trailing buffer. should only happen when encrypting.
        // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
        scrambled = input.doFinal(buffer);

        // final step before we can return the modified data.
        toReturn = append(toReturn,scrambled);

        return toReturn;
    }

    private static byte[] append(byte[] prefix, byte[] suffix){
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i=0; i< prefix.length; i++){
            toReturn[i] = prefix[i];
        }
        for (int i=0; i< suffix.length; i++){
            toReturn[i+prefix.length] = suffix[i];
        }
        return toReturn;
    }


    // AES Utility functions
    public static String simpleAesEncrypt(String seed, String cleartext) throws Exception {
        if (seed == null || cleartext == null) {
            return cleartext;
        }

        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = simpleEncrypt(rawKey, cleartext.getBytes());
        byte[] actResult = Base64.encode(result, Base64.NO_PADDING|Base64.NO_WRAP);
        return new String(actResult, "UTF-8");
    }

    public static String simpleAesDecrypt(String seed, String encrypted) throws Exception {
        if (seed == null || encrypted == null) {
            return encrypted;
        }

        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] encryptedBytes = Base64.decode(encrypted, Base64.NO_PADDING|Base64.NO_WRAP);
        byte[] result = simpleDecrypt(rawKey, encryptedBytes);
        return new String(result,"UTF-8");
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_AES);
        SecureRandom sr;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        try {
            kgen.init(256, sr);
        } catch (Exception e) {
            try {
                kgen.init(192, sr);
            } catch (Exception e1) {
                kgen.init(128, sr);
            }
        }

        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }


    private static byte[] simpleEncrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM_AES);
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] simpleDecrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM_AES);
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }
}
