package com.apps.neighbormart.customView;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.apps.neighbormart.activities.LoginV2Activity;
import com.apps.neighbormart.network.api_request.ApiRequest;
import com.apps.neighbormart.network.api_request.ApiRequestListeners;
import com.apps.neighbormart.parser.Parser;
import com.apps.neighbormart.utils.NSLog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.location.GPStracker;
import com.apps.neighbormart.R;
import com.apps.neighbormart.activities.CustomSearchActivity;
import com.apps.neighbormart.activities.MessengerActivity;
import com.apps.neighbormart.activities.PeopleListActivity;
import com.apps.neighbormart.adapter.lists.ListUsersAdapter;
import com.apps.neighbormart.animation.Animation;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Discussion;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.controllers.users.UserController;
import com.apps.neighbormart.network.ServiceHandler;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.UserParser;
import com.apps.neighbormart.utils.Utils;
import com.apps.neighbormart.views.HorizontalView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

public class PeopleCustomView extends HorizontalView implements ListUsersAdapter.ClickListener {

    private Context mContext;
    private ListUsersAdapter adapter;
    private RecyclerView listView;
    private Map<String, Object> optionalParams;
    private View mainContainer;
    private ShimmerRecyclerView shimmerRecycler;


    public PeopleCustomView(Context context) {
        super(context);
        mContext = context;
        setRecyclerViewAdapter();
    }

    public PeopleCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setCustomAttribute(context, attrs);

        setRecyclerViewAdapter();
    }


    public void loadData(boolean fromDatabase) {

        shimmerRecycler.showShimmerAdapter();

        listView.setVisibility(GONE);

        if (!fromDatabase) loadDataFromAPi();
    }


    private void loadDataFromAPi() {

        Map<String, String> params = new HashMap<String, String>();

        GPStracker mGPS = new GPStracker(mContext);
        params.put("lat", String.valueOf(mGPS.getLatitude()));
        params.put("lng", String.valueOf(mGPS.getLongitude()));

        params.put("token", Utils.getToken(mContext));
        params.put("mac_adr", ServiceHandler.getMacAddr());
        params.put("limit", "30");
        params.put("page", 1 + "");

        ApiRequest.newPostInstance(Constances.API.API_GET_USERS, new ApiRequestListeners() {
            @Override
            public void onSuccess(Parser parser) {

                final UserParser mUsersParser = new UserParser(parser);
                RealmList<User> list = mUsersParser.getUser();

                if (list.size() > 0) {
                    UserController.insertUsers(list);
                }
                adapter.removeAll();

                for (int i = 0; i < list.size(); i++) {

                    adapter.addItem(list.get(i));
                }

                if (adapter.getItemCount() > 0) {
                    listView.setVisibility(VISIBLE);
                    mainContainer.setVisibility(VISIBLE);
                    shimmerRecycler.hideShimmerAdapter();
                } else {
                    mainContainer.setVisibility(GONE);
                }

                Animation.startZoomEffect(getChildAt(0).findViewById(R.id.card_show_more));
            }

            @Override
            public void onFail(Map<String, String> errors) {

            }
        },params);


    }

    private void setRecyclerViewAdapter() {

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.v3_horizontal_list_users, this, true);

        mainContainer = getChildAt(0).findViewById(R.id.people_container);


        //layout direction
        getChildAt(0).setLayoutDirection(AppController.isRTL() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        //header setup
        /*if (optionalParams.containsKey("header") && optionalParams.get("header") != null)
            ((TextView) getChildAt(0).findViewById(R.id.card_title)).setText((String) optionalParams.get("header"));*/

        //setup show more view
        TextView showMore = getChildAt(0).findViewById(R.id.card_show_more);

        Drawable arrowIcon = getResources().getDrawable(R.drawable.ic_baseline_chevron_right_24);
        if (AppController.isRTL()) {
            arrowIcon = getResources().getDrawable(R.drawable.ic_baseline_chevron_right_24);
        }

        DrawableCompat.setTint(
                DrawableCompat.wrap(arrowIcon),
                ContextCompat.getColor(mContext, R.color.colorPrimary)
        );

        if (!AppController.isRTL()) {
            showMore.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowIcon, null);
        } else {
            showMore.setCompoundDrawablesWithIntrinsicBounds(arrowIcon, null, null, null);
        }

        showMore.findViewById(R.id.card_show_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, PeopleListActivity.class));

            }
        });


        //list item setup
        listView = getChildAt(0).findViewById(R.id.list);

        //start showLoading shimmer effect
        shimmerRecycler = getChildAt(0).findViewById(R.id.shimmer_view_container);
        shimmerRecycler.showShimmerAdapter();


        adapter = new ListUsersAdapter(mContext, new ArrayList<User>(), true);

        listView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);


        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(adapter);
        adapter.setClickListener(this);


    }


    private void setCustomAttribute(Context context, @Nullable AttributeSet attrs) {

        optionalParams = new HashMap<>();
        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.StoreCustomView, 0, 0);

        try {
            //get the text and colors specified using the names in attrs.xml
            optionalParams.put("strLimit", a.getInteger(R.styleable.StoreCustomView_strLimit, 30));


        } finally {
            a.recycle();
        }
    }

    @Override
    public void itemClicked(int position) {

        if (SessionsController.isLogged()) {
            Intent intent = new Intent(mContext, MessengerActivity.class);
            intent.putExtra("userId", adapter.getItem(position).getId());
            intent.putExtra("type", Discussion.DISCUSION_WITH_USER);
            intent.putExtra("name", adapter.getItem(position).getUsername());
            intent.putExtra("isBlocked", adapter.getItem(position).isBlocked());

            mContext.startActivity(intent);
        } else {
            mContext.startActivity(new Intent(mContext, LoginV2Activity.class));
        }


    }

    @Override
    public void itemOptionsClicked(View view, int position) {

    }
}
