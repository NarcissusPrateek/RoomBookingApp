package com.nineleaps.conferenceroombooking.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncyption {
    private static MessageDigest md;

    public static void cryptWithMD5(String pass){
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            Log.i("----------passcode", sb.toString());
        } catch (NoSuchAlgorithmException ex) {

        }
    }
}

