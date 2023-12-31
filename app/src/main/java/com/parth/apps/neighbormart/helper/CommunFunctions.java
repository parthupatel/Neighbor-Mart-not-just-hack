package com.apps.neighbormart.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import com.apps.neighbormart.utils.NSLog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apps.neighbormart.R;
import com.apps.neighbormart.appconfig.AppConfig;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.Currency;
import com.apps.neighbormart.classes.Setting;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.controllers.SettingsController;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.OfferCurrencyParser;
import com.apps.neighbormart.parser.api_parser.UserParser;
import com.apps.neighbormart.parser.tags.Tags;
import com.apps.neighbormart.utils.OfferUtils;
import com.apps.neighbormart.utils.Utils;
import com.apps.neighbormart.views.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.apps.neighbormart.utils.NSLog;
import java.util.Map;

import static android.widget.LinearLayout.HORIZONTAL;
import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

public class CommunFunctions {


    public static CustomDialog showErrors(Map<String, String> errors, Context context) {
        final CustomDialog dialog = new CustomDialog(context);

        dialog.setContentView(R.layout.fragment_dialog_costum);
        dialog.setCancelable(false);


        String text = "";
        for (String key : errors.keySet()) {
            if (!text.equals(""))
                text = text + "<br>";


            text = text + "#" + errors.get(key);
        }

        Button ok = dialog.findViewById(R.id.ok);
        Button cancel = dialog.findViewById(R.id.cancel);

        TextView msgbox = dialog.findViewById(R.id.msgbox);

        if (!text.equals("")) {
            msgbox.setText(Html.fromHtml(text));
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel.setVisibility(View.GONE);
        dialog.show();

        return dialog;

    }

    public static float parseExtraFees(LinearLayout view, String fees, Currency currency) throws JSONException {

        float extraFees = 0;

        JSONObject jsonObject = new JSONObject(fees.trim());
        Iterator<String> keys = jsonObject.keys();

        view.removeAllViews();

        while (keys.hasNext()) {
            String key = keys.next();

            LinearLayout total_price_layout = new LinearLayout(view.getContext());
            total_price_layout.setOrientation(HORIZONTAL);
            LinearLayout.LayoutParams layout_379 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_379.gravity = ConstraintLayout.LayoutParams.END;
            total_price_layout.setLayoutParams(layout_379);

            TextView textView_951 = new TextView(view.getContext());
            textView_951.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            textView_951.setTypeface(textView_951.getTypeface(), Typeface.ITALIC);
            textView_951.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            LinearLayout.LayoutParams layout_335 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_335.weight = 1;
            layout_335.leftMargin = (int) view.getContext().getResources().getDimension(R.dimen.spacing_middle);
            textView_951.setLayoutParams(layout_335);
            total_price_layout.addView(textView_951);


            TextView total_price_items = new TextView(view.getContext());
            total_price_items.setTypeface(total_price_items.getTypeface(), Typeface.ITALIC);
            total_price_items.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            total_price_items.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            LinearLayout.LayoutParams layout_991 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_991.leftMargin = (int) view.getContext().getResources().getDimension(R.dimen.spacing_middle);

            total_price_items.setLayoutParams(layout_991);

            total_price_layout.addView(total_price_items);

            //dynamic content
            textView_951.setText(Utils.capitalizeString(key.replace("_", " ")));
            total_price_items.setText(OfferUtils.parseCurrencyFormat(Float.valueOf(jsonObject.get(key).toString()), currency));


            view.addView(total_price_layout);


            extraFees += Float.valueOf(jsonObject.get(key).toString());


        }


        return extraFees;

    }


    public static String convertMessages(Map<String, String> errors) {
        String text = "";
        for (String key : errors.keySet()) {
            if (!text.equals(""))
                text = text + "<br>";


            text = text + "#" + errors.get(key);
        }

        return text;
    }


    public static String createImageFile(Context contxt) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = contxt.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        return image.getAbsolutePath();
    }



}
