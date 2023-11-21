package com.apps.neighbormart.controllers.users;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.apps.neighbormart.activities.LoginV2Activity;
import com.apps.neighbormart.network.api_request.ApiRequest;
import com.apps.neighbormart.network.api_request.ApiRequestListeners;
import com.apps.neighbormart.parser.Parser;
import com.apps.neighbormart.utils.NSLog;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.R;
import com.apps.neighbormart.activities.CustomSearchActivity;
import com.apps.neighbormart.activities.MainActivity;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.UserParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Droideve on 7/13/2017.
 */

public class UserController {

    private static int nbrOfCheck = 0;

    public static boolean insertUsers(final RealmList<User> list) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
        return true;

    }

    public static void checkUserConnection(final FragmentActivity context) {
        checkUserWithThread(context);
    }

    public static void checkUserWithThread(final FragmentActivity context) {

        if (nbrOfCheck > 0)
            return;

        if(!SessionsController.isLogged())
            return;

        User user = SessionsController.getSession().getUser();
        final String email = user.getEmail();
        final String userid = String.valueOf(user.getId());
        final String username = user.getUsername();

        ApiRequest.newPostInstance(Constances.API.API_USER_CHECK_CONNECTION, new ApiRequestListeners() {
            @Override
            public void onSuccess(Parser parser) {

                UserParser mUserParser = new UserParser(parser);
                if (mUserParser.getSuccess() == 0 || mUserParser.getSuccess() == -1) {
                    userLogoutAlert(context);
                    nbrOfCheck = 0;
                } else {
                    RealmList<User> list = mUserParser.getUser();
                    if (list.size() > 0) {
                        SessionsController.createSession(list.get(0),list.get(0).getToken());
                    }
                    nbrOfCheck++;
                }
            }

            @Override
            public void onFail(Map<String, String> errors) {

            }
        }, Map.of(
                "email", email ,
                "userid", userid,
                "username", username
        ));



    }


    public static void userLogoutAlert(final FragmentActivity activity) {

        new android.app.AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.Logout) + "!")
                .setMessage(activity.getString(R.string.logout_alert))
                .setPositiveButton(activity.getString(R.string.Login), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        SessionsController.logOut();
                        ActivityCompat.finishAffinity(activity);
                        activity.startActivity(new Intent(activity, LoginV2Activity.class));

                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SessionsController.logOut();
                        ActivityCompat.finishAffinity(activity);
                        activity.startActivity(new Intent(activity, MainActivity.class));

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }
}
