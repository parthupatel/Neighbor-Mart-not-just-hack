package com.apps.neighbormart.business_manager.api;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.business_manager.models.BusinessUser;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.ApiRequest;
import com.apps.neighbormart.network.api_request.ApiRequestListeners;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.utils.NSLog;

import java.util.HashMap;
import java.util.Map;

public class BusinessApiRequest extends ApiRequest {
    public BusinessApiRequest(int method, String url, ApiRequestListeners listeners) {
        super(method, url, listeners);
        BusinessUser mBusinessUser = BusinessUser.find();
        if(mBusinessUser != null){
            httpHeaders.put("Session-user-id",String.valueOf(mBusinessUser.user.getId()));
            httpHeaders.put("Authorization","Bearer "+String.valueOf(mBusinessUser.user.getToken()));
        }
    }


    public static void BusinessApiRequest(String url, ApiRequestListeners listeners){
        BusinessApiRequest.newPostInstance(url,listeners,new HashMap<>(),new HashMap<>());
    }

    public static void newPostInstance(String url, ApiRequestListeners listeners, Map<String, String> myParams){
        BusinessApiRequest.newPostInstance(url,listeners,myParams,new HashMap<>());
    }

    public static void newPostInstance(String url, ApiRequestListeners listeners,
                                       Map<String, String> myParams,
                                       Map<String, String> customHeaders){

        if(AppConfig.APP_DEBUG){
            NSLog.e(url,myParams.toString());
        }

        RequestQueue queue  = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();

        BusinessApiRequest request = new BusinessApiRequest(Request.Method.POST,
                url, listeners){
            @Override
            protected Map<String, String> getParams() {
                return myParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                NSLog.e("getHeaders",httpHeaders.toString());
                NSLog.e("getHeaders",httpHeaders.toString());
                return httpHeaders;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }
}
