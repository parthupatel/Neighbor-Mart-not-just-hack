package com.apps.neighbormart.controllers.messenger;

import android.content.Context;
import android.util.Log;
import com.apps.neighbormart.utils.NSLog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.Services.BusStation;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.AppContext;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Message;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.dtmessenger.NotificationsManager;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.Parser;
import com.apps.neighbormart.parser.api_parser.MessageParser;
import com.apps.neighbormart.parser.tags.Tags;
import com.apps.neighbormart.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import com.apps.neighbormart.utils.NSLog;
import java.util.Map;

/**
 * Created by Droideve on 12/23/2016.
 */

public class MessengerController {


    public static void loadMessages(final User user) {
        loadMessages(user, null);
    }

    public static void loadMessages(final User user, final Context context) {

        //load from database
        RequestQueue queue = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();
        final int receiverid = user.getId();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_LOAD_MESSAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {


                try {

                    if (AppContext.DEBUG)
                        NSLog.e("MessengerController", response);

                    JSONObject js = new JSONObject(response);
                    final Parser mParser = new Parser(js);
                    int success = Integer.parseInt(mParser.getStringAttr(Tags.SUCCESS));

                    if (success == 1) {

                        MessageParser mMessageParser = new MessageParser(new JSONObject(response));
                        final List<Message> list = mMessageParser.getMessages();

                        if (list.size() > 0) {

                            if (context != null) {

                                if (NotificationUtils.isAppIsInBackground(AppController.getInstance())) {

                                    NotificationsManager.pushNotifnewMessage(context, list);
                                    JSONArray jsonMsgIds = new JSONArray();

                                    for (int i = 0; i < list.size(); i++) {
                                        try {
                                            jsonMsgIds.put(list.get(i).getMessageid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    BusStation.getBus().post(list.get(i));
                                }
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    //show loadToast with showError
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NSLog.e("ERROR", error.toString());

                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("receiver_id", String.valueOf(receiverid));
                params.put("status", "-2");

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }


    public static void inboxMarkAsSeen(final User mUser, final int discussinId) {

        RequestQueue queue = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();

        if (mUser != null) {

            final int user_id = mUser.getId();

            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_INBOX_MARK_AS_SEEN, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {

                        if (AppContext.DEBUG)
                            NSLog.e(MessengerController.class.getName(), response);

                        JSONObject js = new JSONObject(response);
                        final Parser mParser = new Parser(js);
                        int success = Integer.parseInt(mParser.getStringAttr(Tags.SUCCESS));

                        if (success == 1) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        //show loadToast with showError
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (AppConfig.APP_DEBUG)
                        NSLog.e("ERROR", error.toString());

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("user_id", String.valueOf(user_id));
                    params.put("discussionId", String.valueOf(discussinId));

                    return params;
                }

            };


            request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(request);

        }
    }


    public static void inboxMarkAsLoaded(final User mUser, final int discussinId) {

        RequestQueue queue = VolleySingleton.getInstance(AppController.getInstance()).getRequestQueue();

        if (mUser != null) {

            final int user_id = mUser.getId();

            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_INBOX_MARK_AS_LOADED, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {

                        if (AppContext.DEBUG)
                            NSLog.e(MessengerController.class.getName(), response);

                        JSONObject js = new JSONObject(response);
                        final Parser mParser = new Parser(js);
                        int success = Integer.parseInt(mParser.getStringAttr(Tags.SUCCESS));

                        if (success == 1) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        //show loadToast with showError
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (AppConfig.APP_DEBUG)
                        NSLog.e("ERROR", error.toString());

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("user_id", String.valueOf(user_id));
                    params.put("discussionId", String.valueOf(discussinId));

                    return params;
                }

            };


            request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(request);

        }
    }

}
