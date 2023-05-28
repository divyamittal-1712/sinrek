/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.app.appsinrek.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.app.appsinrek.R;
import com.app.appsinrek.utils.countrypicker.Country;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;


public final class CommonUtils {

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getTimestamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static String setPhoneNumberHint(Country mSelectedCountry,PhoneNumberUtil mPhoneUtil){
        if (mSelectedCountry != null) {
            Phonenumber.PhoneNumber phoneNumber =
                    mPhoneUtil.getExampleNumberForType(mSelectedCountry.getCode().toUpperCase(),
                            PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                String format = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                if (format.length() > mSelectedCountry.getDialCode().length())
                    return format.substring((mSelectedCountry.getDialCode().length() + 1));
                else
                    return "";
            }
            return "";
        }
        return "";
    }
    public static boolean isValid(String value,Country mSelectedCountry,PhoneNumberUtil mPhoneUtil) {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber(value,mSelectedCountry,mPhoneUtil);
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    public static Phonenumber.PhoneNumber getPhoneNumber(String value,Country mSelectedCountry,PhoneNumberUtil mPhoneUtil) {
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                iso = mSelectedCountry.getCode().toUpperCase();
            }
            return mPhoneUtil.parse(value.trim(), iso);
        } catch (NumberParseException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void print(String s) {
        System.out.println(s);
    }
    public static String getFileTypeFromURL(String url){
        String[] splitedArray = url.split("\\.");
        String lastValueOfArray = splitedArray[splitedArray.length-1];
        if(lastValueOfArray.equals("mp4") || lastValueOfArray.equals("flv") || lastValueOfArray.equals("m4a") || lastValueOfArray.equals("3gp") || lastValueOfArray.equals("mkv")){
            return "video";
        }else if(lastValueOfArray.equals("mp3") || lastValueOfArray.equals("ogg")){
            return "audio";
        }else if(lastValueOfArray.equals("jpg") || lastValueOfArray.equals("png") || lastValueOfArray.equals("gif")){
            return "photo";
        }else{
            return "";
        }
    }
    @Nullable
    public static Uri createFilePath(){
        File newFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CroppedImage");

        if (!newFile.exists() && !newFile.mkdir()) {
            return null;
        }

        newFile = new File(newFile.getPath() + File.separator + "cropped_image.jpg");
        Uri destinationUri = Uri.fromFile(newFile);
        print(destinationUri.getPath());
        return destinationUri;
    }
    public static String encodeEmoji (String message) {
        try {
            return URLEncoder.encode(message,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }


    public static String decodeEmoji (String message) {
        String myString= null;
        try {
            return URLDecoder.decode(message, "UTF-8");
        } catch (Exception e) {
            return message;
        }
    }

    /**Method to convert an string into camelcase view**/
    public static String toCamelCase(String s){
        if(s.length() == 0){
            return s;
        }
        String[] parts = s.split(" ");
        String camelCaseString = "";
        for (String part : parts){
            camelCaseString = camelCaseString + toProperCase(part) + " ";
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

}
