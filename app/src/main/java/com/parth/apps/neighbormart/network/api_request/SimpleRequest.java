package com.apps.neighbormart.network.api_request;


import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.utils.NSLog;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.AppContext;
import com.apps.neighbormart.controllers.sessions.GuestController;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.utils.DateUtils;
import com.apps.neighbormart.utils.NSLog;
import com.apps.neighbormart.utils.Translator;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class SimpleRequest extends StringRequest {

    public static int TIME_OUT = 40000;
    public static Map<String, String> httpHeaders = new HashMap<>();

    public SimpleRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

        //create Headers
        this.prepareHttpHeaders();
    }

    public void prepareHttpHeaders(){

        try {

            httpHeaders = AppController.getTokens();
            httpHeaders.put("Language", Translator.DefaultLang);
            httpHeaders.put("Debug", String.valueOf(AppContext.DEBUG));
            httpHeaders.put("Api-key-android", AppConfig.ANDROID_API_KEY);
            httpHeaders.put("date", DateUtils.getUTC("yyyy-MM-dd HH:mm"));
            httpHeaders.put("Timezone", TimeZone.getDefault().getID());
            httpHeaders.put("Api-app-id", "ns-android");

            if (GuestController.isStored()) {
                httpHeaders.put("Session-Guest-Id", String.valueOf(GuestController.getGuest().getId()));
            }

            if (SessionsController.isLogged()) {
                httpHeaders.put("Session-User-Id", String.valueOf(SessionsController.getSession().getUser().getId()));
                httpHeaders.put("Authorization", "Bearer "+String.valueOf(SessionsController.getSession().getToken()));
            }

            if (AppConfig.APP_DEBUG)
                NSLog.e("getHeaders", httpHeaders.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        if(httpHeaders.size() == 0){
            this.prepareHttpHeaders();
        }

        return httpHeaders;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }


}

