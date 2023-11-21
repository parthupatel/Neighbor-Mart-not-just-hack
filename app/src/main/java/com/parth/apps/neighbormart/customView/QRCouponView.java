package com.apps.neighbormart.customView;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.apps.neighbormart.AppController;
import com.apps.neighbormart.R;
import com.apps.neighbormart.activities.CustomSearchActivity;
import com.apps.neighbormart.activities.LoginV2Activity;
import com.apps.neighbormart.activities.StoreDetailActivity;
import com.apps.neighbormart.adapter.lists.StoreListAdapter;
import com.apps.neighbormart.animation.Animation;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Offer;
import com.apps.neighbormart.classes.Store;
import com.apps.neighbormart.controllers.SettingsController;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.controllers.stores.OffersController;
import com.apps.neighbormart.controllers.stores.StoreController;
import com.apps.neighbormart.fragments.CustomSearchFragment;
import com.apps.neighbormart.location.GPStracker;
import com.apps.neighbormart.network.ServiceHandler;
import com.apps.neighbormart.network.api_request.ApiRequest;
import com.apps.neighbormart.network.api_request.ApiRequestListeners;
import com.apps.neighbormart.parser.Parser;
import com.apps.neighbormart.parser.api_parser.StoreParser;
import com.apps.neighbormart.parser.tags.Tags;
import com.apps.neighbormart.utils.NSLog;
import com.apps.neighbormart.utils.NSToast;
import com.apps.neighbormart.utils.QRCodeUtil;
import com.apps.neighbormart.views.HorizontalView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class QRCouponView extends LinearLayout{

    private Context mContext;
    private View mainContainer;
    private int offer_id;

    @BindView(R.id.generating_qr_code_progress)
    ProgressBar generating_qr_code_progress;
    @BindView(R.id.qrcode_image)
    ImageView qrcode_image;
    @BindView(R.id.qrcode_container)
    LinearLayout qrcode_container;

    public QRCouponView(Context context) {
        super(context);
        mContext = context;

    }

    public QRCouponView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public QRCouponView setup(){

        LayoutInflater.from(mContext).inflate(R.layout.v3_qrcoupon_layout, this, true);
        ButterKnife.bind(this);
        mainContainer = getChildAt(0).findViewById(R.id.container);

        return this;
    }

    public void load(int offer_id){

        this.offer_id = offer_id;
        Offer offer =  OffersController.getOffer(offer_id);

        setupCouponButton(offer);

    }

    private boolean setupCouponButton(Offer offer) {

        if(offer.getHasGotCoupon()>0)
            return verifyCoupon(offer);

        if(offer.getCoupon_config().equals("limited") && offer.getCoupon_redeem_limit() == 0 )
            return disableCouponButton();

        //load code
        redeemCouponOrGetExisting();

        return true;
    }

    private boolean verifyCoupon(Offer offer) {

        redeemCouponOrGetExisting();

        return false;
    }

    private boolean disableCouponButton(){

        this.setVisibility(View.VISIBLE);
        return false;
    }


    private void redeemCouponOrGetExisting() {

        //show progressbar
        generating_qr_code_progress.setVisibility(View.VISIBLE);

        //hide qr code presenter
        qrcode_image.setVisibility(View.GONE);

        Offer mOffer = OffersController.getOffer(offer_id);

        //request new qr code token from server
        int user_id = SessionsController.getSession().getUser().getId();


        ApiRequest.newPostInstance(Constances.API.API_OFFER_GET_COUPON_CODE, new ApiRequestListeners() {
                    @Override
                    public void onSuccess(Parser parser) {

                        validateResponse(parser);

                        Offer offer = OffersController.findOfferById(offer_id);
                        //update database
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                offer.setHasGotCoupon(1);
                                realm.copyToRealmOrUpdate(offer);
                            }
                        });

                    }

                    @Override
                    public void onFail(Map<String, String> errors) {
                        //hide progress bar
                        generating_qr_code_progress.setVisibility(View.GONE);
                    }
                }, Map.of(
                        "user_id", String.valueOf(user_id),
                        "offer_id", String.valueOf(mOffer.getId())
                )
        );

    }

    void validateResponse(Parser parser){

        if(!parser.getStringAttr(Tags.RESULT).equals("")){
            //gererate qr code
            setupQRCode( parser.getStringAttr(Tags.RESULT) );
        }

        //hide progress bar
        generating_qr_code_progress.setVisibility(View.GONE);

    }

    private boolean setupQRCode(String code){

        Bitmap bitmap = QRCodeUtil.generate(mContext, "coupon:"+code+":"+SessionsController.getSession().getUser().getId());
        //put bitmap inside view
        qrcode_image.setImageBitmap(bitmap);
        //show qr code presenter
        qrcode_image.setVisibility(View.VISIBLE);
        qrcode_container.setVisibility(VISIBLE);

        ((TextView)findViewById(R.id.coupon_id_field)).setText(code);

        //hide button
        setVisibility(View.VISIBLE);

        return true;
    }

}
