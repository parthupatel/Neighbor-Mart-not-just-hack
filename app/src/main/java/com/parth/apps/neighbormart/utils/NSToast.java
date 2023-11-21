package com.apps.neighbormart.utils;

import android.util.Log;
import android.widget.Toast;

import com.apps.neighbormart.AppController;

public class NSToast {

    public static void show(String msg) {
        Toast.makeText(AppController.getInstance(),msg,Toast.LENGTH_LONG).show();
    }
}
