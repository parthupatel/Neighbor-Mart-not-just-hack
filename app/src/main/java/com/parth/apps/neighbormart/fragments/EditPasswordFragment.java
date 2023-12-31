package com.apps.neighbormart.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.apps.neighbormart.utils.NSLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.balysv.materialripple.MaterialRippleLayout;
import com.apps.neighbormart.R;
import com.apps.neighbormart.appconfig.Constances;
import com.apps.neighbormart.classes.User;
import com.apps.neighbormart.controllers.sessions.SessionsController;
import com.apps.neighbormart.databinding.V2FragmentProfilePasswordBinding;
import com.apps.neighbormart.helper.CommunFunctions;
import com.apps.neighbormart.network.VolleySingleton;
import com.apps.neighbormart.network.api_request.SimpleRequest;
import com.apps.neighbormart.parser.api_parser.UserParser;
import com.apps.neighbormart.parser.tags.Tags;
import com.apps.neighbormart.utils.MessageDialog;
import com.apps.neighbormart.utils.Translator;
import com.apps.neighbormart.views.CustomDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import com.apps.neighbormart.utils.NSLog;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.apps.neighbormart.appconfig.AppConfig.APP_DEBUG;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditPasswordFragment extends Fragment {

    @BindView(R.id.old_password)
    TextInputEditText oldPasswordTxt;

    @BindView(R.id.new_password)
    TextInputEditText newPasswordTxt;

    @BindView(R.id.confirm_password)
    TextInputEditText confirmPasswordTxt;

    @BindView(R.id.saveBtn)
    MaterialRippleLayout saveBtn;


    private User mUser;

    private ProgressDialog mPdialog;


    private RequestQueue queue;

    private CustomDialog mDialogError;


    // newInstance constructor for creating fragment with arguments
    public static EditPasswordFragment newInstance(int page, String title) {
        EditPasswordFragment fragmentFirst = new EditPasswordFragment();
        Bundle args = new Bundle();
        args.putInt("id", page);
        args.putString("title", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V2FragmentProfilePasswordBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.v2_fragment_profile_password, container, false);
        View view = binding.getRoot();

        // Inflate the layout for this fragment

        ButterKnife.bind(this, view);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });
        return view;
    }


    private void doSave() {


        if (SessionsController.getSession() != null) {

            //Make sure that the user is connected before executing the api
            mUser = SessionsController.getSession().getUser();
            if (mUser != null) {


                final String idUser = String.valueOf(mUser.getId()).trim();
                final String username = String.valueOf(mUser.getUsername()).trim();


                //Display Popup : Loading
                mPdialog = new ProgressDialog(getActivity());
                mPdialog.setMessage("Loading ...");
                mPdialog.setCancelable(false);
                mPdialog.show();


                SimpleRequest request = new SimpleRequest(Request.Method.POST,
                        Constances.API.API_UPDATE_ACCOUNT_PASSWORD, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mPdialog.dismiss();

                        try {

                            if (APP_DEBUG) {
                                NSLog.e("response", response);
                            }

                            JSONObject js = new JSONObject(response);

                            UserParser mUserParser = new UserParser(js);

                            int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                            if (success == 1) {

                                final List<User> list = mUserParser.getUser();
                                if (list.size() > 0) {

                                    if (APP_DEBUG)
                                        NSLog.e("__", "logged " + list.get(0).getUsername());

                                    SessionsController.logOut();
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            SessionsController.createSession(list.get(0),list.get(0).getToken());
                                            Toast.makeText(getContext(), "Password has successfully changed", Toast.LENGTH_LONG).show();
                                        }
                                    }, 2000);


                                }
                            } else {


                                Map<String, String> errors = mUserParser.getErrors();


                                MessageDialog.newDialog(getActivity()).onCancelClick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MessageDialog.getInstance().hide();
                                    }
                                }).onOkClick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        MessageDialog.getInstance().hide();
                                    }
                                }).setContent(Translator.print(CommunFunctions.convertMessages(errors), "Message showError")).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            Map<String, String> errors = new HashMap<String, String>();
                            errors.put("JSONException:", "Try later \"Json parser\"");

                            MessageDialog.newDialog(getActivity()).onCancelClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MessageDialog.getInstance().hide();
                                }
                            }).onOkClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MessageDialog.getInstance().hide();
                                }
                            }).setContent(Translator.print(CommunFunctions.convertMessages(errors), "Message showError")).show();


                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (APP_DEBUG) {
                            NSLog.e("ERROR", error.toString());
                        }

                        mPdialog.dismiss();
                        Map<String, String> errors = new HashMap<String, String>();

                        errors.put("NetworkException:", getString(R.string.check_nework));
                        mDialogError = CommunFunctions.showErrors(errors, getContext());
                        mDialogError.setTitle(R.string.network_error);

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("username", username);
                        params.put("current_password", oldPasswordTxt.getText().toString().trim());
                        params.put("new_password", newPasswordTxt.getText().toString().trim());
                        //params.put("phone", codeCountryString + "-" + phone.getText().toString().trim());
                        params.put("user_id", idUser);
                        params.put("confirm_password", confirmPasswordTxt.getText().toString().trim());

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


}
