package com.princemartbd.dealer.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.princemartbd.dealer.R;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SuppressWarnings("deprecation")
public class ApiConfig extends Application {

    public static final String TAG = ApiConfig.class.getSimpleName();
    static ApiConfig mInstance;
    static boolean isDialogOpen = false;
    RequestQueue mRequestQueue;

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!";
            } else if (error instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isProgress) {
        if (ProgressDisplay.mProgressBar != null) {
            ProgressDisplay.mProgressBar.setVisibility(View.GONE);
        }
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);
        progressDisplay.hideProgress();
        if (isProgress)
            progressDisplay.showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (ApiConfig.isConnected(activity))
                callback.onSuccess(true, response);
            if (isProgress)
                progressDisplay.hideProgress();
        }, error -> {
            if (isProgress)
                progressDisplay.hideProgress();
            if (ApiConfig.isConnected(activity))
                callback.onSuccess(false, "");
            String message = VolleyErrorMessage(error);
            if (!message.equals(""))
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params1 = new HashMap<>();
                params1.put(Constant.AUTHORIZATION, "Bearer " + createJWT("PrinceMartBD", "PrinceMart Strong Secure Authentication")); // change
                return params1;
            }
            @Override
            protected Map<String, String> getParams() {
                params.put(Constant.AccessKey, Constant.AccessKeyVal);
                params.put(Constant.API_KEY, createJWT("PrinceMartBD", "PrinceMart Strong Secure Authentication")); //change
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        ApiConfig.getInstance().getRequestQueue().getCache().clear();
        ApiConfig.getInstance().addToRequestQueue(stringRequest);
    }

//    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final Map<String, String> fileParams) {
//        if (isConnected(activity)) {
//            VolleyMultiPartRequest multipartRequest = new VolleyMultiPartRequest(url,
//                    response -> callback.onSuccess(true, response),
//                    error -> callback.onSuccess(false, "")) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> params1 = new HashMap<>();
//                    params1.put(Constant.AUTHORIZATION, "Bearer " + createJWT("eKart", "eKart Authentication"));
//                    return params1;
//                }
//
//                @Override
//                public Map<String, String> getDefaultParams() {
//                    params.put(Constant.AccessKey, Constant.AccessKeyVal);
//                    params.put(Constant.API_KEY, createJWT("eKart", "eKart Authentication"));
//                    return params;
//                }
//
//            };
//
//            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            getInstance().getRequestQueue().getCache().clear();
//            getInstance().addToRequestQueue(multipartRequest);
//        }
//    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String createJWT(String issuer, String subject) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            byte[] apiKeySecretBytes = Constant.JWT_KEY.getBytes();
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);

            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean CheckValidation(String item, boolean isMailValidation, boolean isMobileValidation) {
        boolean result = false;
        if (item.length() == 0) {
            result = true;
        } else if (isMailValidation) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()) {
                result = true;
            }
        } else if (isMobileValidation) {
            if (!android.util.Patterns.PHONE.matcher(item).matches()) {
                result = true;
            }
        }
        return result;
    }

    public static synchronized ApiConfig getInstance() {
        return mInstance;
    }

    public static Boolean isConnected(final Activity activity) {
        boolean check = false;
        try {
            ConnectivityManager ConnectionManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                check = true;
            } else {
                try {
                    if (!isDialogOpen) {
                        @SuppressLint("InflateParams") View sheetView = activity.getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
                        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
                        if (parentViewGroup != null) {
                            parentViewGroup.removeAllViews();
                        }

                        final Dialog mBottomSheetDialog = new Dialog(activity);
                        mBottomSheetDialog.setContentView(sheetView);
                        mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mBottomSheetDialog.show();
                        isDialogOpen = true;
                        Button btnRetry = sheetView.findViewById(R.id.btnRetry);
                        mBottomSheetDialog.setCancelable(false);

                        btnRetry.setOnClickListener(view -> {
                            if (isConnected(activity)) {
                                isDialogOpen = false;
                                mBottomSheetDialog.dismiss();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void hideKeyboard(Activity activity, View root) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void newUpdate(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.lyt_update, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        customLayout.findViewById(R.id.customUpdateCheckerBtnId).setOnClickListener(view -> {
            dialog.dismiss();
            rateMe(activity);
        });
    }

    public static void rateMe(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName() + "&hl=en")));
        }
    }
}