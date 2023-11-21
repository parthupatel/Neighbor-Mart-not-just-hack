package com.apps.neighbormart.customView;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.R;
import com.apps.neighbormart.activities.GalleryActivity;
import com.apps.neighbormart.adapter.lists.ImagesListAdapter;
import com.apps.neighbormart.adapter.lists.OfferListAdapter;
import com.apps.neighbormart.animation.Animation;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Images;
import com.apps.neighbormart.fragments.SlideshowDialogFragment;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.ImagesParser;
import com.apps.neighbormart.parser.tags.Tags;
import com.apps.neighbormart.views.HorizontalView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.apps.neighbormart.utils.NSLog;
import java.util.Map;

import io.realm.RealmList;

public class GalleryStoreCustomView extends HorizontalView implements OfferListAdapter.ClickListener, ImagesListAdapter.ClickListener {

    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ImagesListAdapter adapter;
    private RecyclerView listView;
    private Map<String, Object> optionalParams;
    private ShimmerRecyclerView shimmerRecycler;
    private List<Images> mGalleryList;
    private int int_id;
    private String type;
    private View mainContainer;


    public GalleryStoreCustomView(Context context) {
        super(context);
        mContext = context;
        setRecyclerViewAdapter();
    }

    public GalleryStoreCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;


        //setCustomAttribute(context, attrs);

        setRecyclerViewAdapter();

    }


    public void loadData(int int_id, String type, FragmentActivity mFragmentActivity, boolean fromDatabase) {
        this.int_id = int_id;
        this.type = type;
        this.mFragmentActivity = mFragmentActivity;

        if (!fromDatabase) loadDataFromAPi(int_id, type);
    }


    private void loadDataFromAPi(final int int_id, final String type) {

        Map<String, String> params = new HashMap<String, String>();

        int cols = mContext.getResources().getInteger(R.integer.nbr_gallery_cols);
        int limit = cols * cols;

        params.put("module_id", int_id + "");
        params.put("module", type);
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(1));

        ApiRequest.newPostInstance(Constances.API.API_GET_GALLERY, new ApiRequestListeners() {
            @Override
            public void onSuccess(Parser parser) {

                final ImagesParser mImagesParser = new ImagesParser(parser);

                if (mImagesParser.getSuccess() == 1) {
                    adapter.removeAll();
                    final RealmList<Images> list = mImagesParser.getGallery();
                    for (Images images : list) {
                        adapter.addItem(images);
                    }

                    shimmerRecycler.hideShimmerAdapter();

                    if (adapter.getItemCount() > 0) {
                        mainContainer.setVisibility(VISIBLE);
                    } else {
                        mainContainer.setVisibility(GONE);

                    }
                }

                int cols = mContext.getResources().getInteger(R.integer.nbr_gallery_cols);
                int limit = cols * cols;

                if (limit < mImagesParser.getIntArg(Tags.COUNT)) {
                    Animation.startZoomEffect(getChildAt(0).findViewById(R.id.card_show_more));
                }
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
        inflater.inflate(R.layout.v2_horizontal_list_gallery, this, true);

        mainContainer = getChildAt(0).findViewById(R.id.gallery_container);


        //start showLoading shimmer effect
        shimmerRecycler = getChildAt(0).findViewById(R.id.shimmer_view_container);
        shimmerRecycler.showShimmerAdapter();

        listView = getChildAt(0).findViewById(R.id.list);
        adapter = new ImagesListAdapter(mContext, new ArrayList<Images>(), true);

        listView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (AppController.isRTL())
            mLayoutManager.setReverseLayout(true);

        listView.setLayoutManager(mLayoutManager);
        listView.setAdapter(adapter);
        adapter.setClickListener(this);


        //setup show more view
        TextView showMore = getChildAt(0).findViewById(R.id.card_show_more);
        Drawable arrowIcon = getResources().getDrawable(R.drawable.ic_arrow_forward_white_18dp);

        DrawableCompat.setTint(
                DrawableCompat.wrap(arrowIcon),
                ContextCompat.getColor(mContext, R.color.colorPrimary)
        );

        showMore.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowIcon, null);


    }

    @Override
    public void itemClicked(View view, int position) {
        //test
        if (AppConfig.APP_DEBUG)
            NSLog.e("itemClicked", "position:" + position);
        SlideshowDialogFragment.newInstance().show(mFragmentActivity, adapter.getData(), position);

    }

    @Override
    public void likeClicked(View view, int position) {

    }

    @Override
    public void seeMoreClicked(View view, int position) {
        if (AppConfig.APP_DEBUG)
            NSLog.e("itemClicked", "position:" + position);

        Intent i = new Intent(mContext, GalleryActivity.class);
        i.putExtra("int_id", int_id);
        i.putExtra("type", type);
        mContext.startActivity(i);

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

}
