package com.apps.neighbormart.modules.store.api;

import android.os.Build;
import android.util.Log;
import com.apps.neighbormart.utils.NSLog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Store;
import com.apps.neighbormart.controllers.ErrorsController;
import com.apps.neighbormart.controllers.stores.StoreController;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.StoreParser;
import com.apps.neighbormart.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import com.apps.neighbormart.utils.NSLog;
import java.util.Map;

import io.realm.RealmList;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

public class StoreApiCall {

    private RequestQueue queue;

    public StoreApiCall(){
        queue = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();
    }

    public static StoreApiCall newInstance(){
        StoreApiCall instance = new StoreApiCall();

        return  instance;
    }

    public StoreApiCallDelegate delegate;

    public void getStores(HashMap params) {


        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        NSLog.e("responseStoresString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);

                    //NSLog.e("response",response);

                    final StoreParser mStoreParser = new StoreParser(jsonObject);

                    if(mStoreParser.getSuccess() == 1){
                        if(delegate != null){
                            delegate.onSuccess(mStoreParser.getStore());
                        }
                    }else{
                        delegate.onError(StoreApiCall.this,mStoreParser.getErrors());
                    }


                } catch (JSONException e) {
                    //send a rapport to support
                    if (APP_DEBUG)
                        e.printStackTrace();


                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    NSLog.e("ERROR", error.toString());
                }


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }





    interface StoreApiCallDelegate{

        public void onSuccess(List<Store> list);
        public void onError(StoreApiCall object,Map<String,String> errors);

    }

}
