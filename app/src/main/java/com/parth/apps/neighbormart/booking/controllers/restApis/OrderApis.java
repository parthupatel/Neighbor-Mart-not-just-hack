package com.apps.neighbormart.booking.controllers.restApis;

import android.util.Log;
import com.apps.neighbormart.utils.NSLog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Store;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.network.ServiceHandler;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.StoreParser;
import com.apps.neighbormart.parser.api_parser.UserParser;
import com.apps.neighbormart.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

public class OrderApis {


    private final RequestQueue queue;
    public OrderRestAPisDelegate delegate;

    public OrderApis() {
        queue = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();
    }

    public static OrderApis newInstance() {
        return new OrderApis();
    }


    public interface OrderRestAPisDelegate {
        void onStoreSuccess(Store storeData);

        void onError(OrderApis object, Map<String, String> errors);
    }


    public void getStoreDetail(final HashMap<String, String> _params) {

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        NSLog.e("responseStoresString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);
                    final StoreParser mStoreParser = new StoreParser(jsonObject);
                    RealmList<Store> list = mStoreParser.getStore();

                    if (mStoreParser.getSuccess() == 1) {
                        if (delegate != null && list.size() > 0)
                            delegate.onStoreSuccess(list.get(0));

                    } else {
                        if (delegate != null)
                            delegate.onError(OrderApis.this, mStoreParser.getErrors());
                    }

                } catch (JSONException e) {
                    //send a rapport to support
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

                if (_params != null && !_params.isEmpty()) {
                    for (Map.Entry<String, String> entry : _params.entrySet()) {
                        params.put(entry.getKey(), entry.getValue());
                    }
                }


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }




}
