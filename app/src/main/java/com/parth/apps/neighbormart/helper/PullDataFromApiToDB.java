package com.apps.neighbormart.helper;

import android.content.Context;
import android.util.Log;
import com.apps.neighbormart.utils.NSLog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.controllers.banners.BannersController;
import com.apps.neighbormart.controllers.categories.CategoryController;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.BannerParser;
import com.apps.neighbormart.parser.api_parser.CategoryParser;
import com.apps.neighbormart.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

public class PullDataFromApiToDB {


    public static void getCategories(Context context) {

        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();

        SimpleRequest request = new SimpleRequest(Request.Method.GET,
                Constances.API.API_USER_GET_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        NSLog.e("catsResponse", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);
                    // NSLog.e("response", jsonObject.toString());
                    final CategoryParser mCategoryParser = new CategoryParser(jsonObject);
                    int success = Integer.parseInt(mCategoryParser.getStringAttr(Tags.SUCCESS));

                    if (success == 1 && mCategoryParser.getCategories().size() > 0) {
                        CategoryController.removeAll();
                        CategoryController.insertCategories(
                                mCategoryParser.getCategories()
                        );
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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }


    public static void getBanners(Context context) {

        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();

        SimpleRequest request = new SimpleRequest(Request.Method.GET,
                Constances.API.API_GET_SLIDERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        NSLog.e("bannersResponse", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);
                    final BannerParser mBannerParser = new BannerParser(jsonObject);
                    int success = Integer.parseInt(mBannerParser.getStringAttr(Tags.SUCCESS));

                    if (success == 1 && mBannerParser.getBanners().size() > 0) {
                        BannersController.removeAll();
                        BannersController.insertBanners(
                                mBannerParser.getBanners()
                        );
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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }
}
