package com.princemartbd.dealer.activity.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.RanNumGen;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.ui.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText edtMobileVerify;
    private EditText edtResetPass;
    private EditText edtResetCPass;

    public Activity activity;
    public Session session;

    private ScrollView lytVerify;
    private ScrollView lytResetPass;
    private LinearLayout lytOTP;

    boolean resendOTP = false;
    boolean timerOn;

    private PinView pinViewOTP;

    private Button btnVerify;
    private String mobile;

//    CountryCodePicker edtCountryCodePicker;
    TextView tvTimer, tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        activity = this;
        session = new Session(activity);

        lytVerify = findViewById(R.id.lytVerify);
        lytResetPass = findViewById(R.id.lytResetPass);
        lytOTP = findViewById(R.id.lytOTP);

        tvResend = findViewById(R.id.tvResend);
        tvTimer = findViewById(R.id.tvTimer);

//        edtCountryCodePicker = findViewById(R.id.edtCountryCodePicker);
//        edtCountryCodePicker.setCountryForNameCode("BD");

        edtMobileVerify = findViewById(R.id.edtMobileVerify);
        edtResetPass = findViewById(R.id.edtResetPass);
        edtResetCPass = findViewById(R.id.edtResetCPass);
        pinViewOTP = findViewById(R.id.pinViewOTP);

        findViewById(R.id.btnResetPass).setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            ForgotPassword();
        });

        btnVerify = findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            checkNumber();
        });
    }

    public void checkNumber() {
        session.setData(Constant.COUNTRY_CODE, "88");

        mobile = edtMobileVerify.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.VERIFY_USER);
        params.put(Constant.MOBILE, mobile);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    String phoneNumber = ("+" + session.getData(Constant.COUNTRY_CODE) +  mobile);
                    if (!object.getBoolean(Constant.ERROR)) {
                        generateOTP(phoneNumber);
                    } else {
//                        setSnackBar(getString(R.string.alert_register_num1) + " " + getString(R.string.app_name) + getString(R.string.alert_register_num2), getString(R.string.btn_ok), "reset");
                    setSnackBar(getString(R.string.alert_not_register_num1) + " " + getString(R.string.app_name) + getString(R.string.alert_not_register_num2), getString(R.string.btn_ok), "none");
                    }
                } catch (JSONException e) {
                    Log.d("checkNumber", e.getMessage());
                }
            }
        }, activity, Constant.AUTH_LOGIN_URL, params, true);
    }

    public void generateOTP(String oPhoneNumber) {
        String generateOTP = RanNumGen.randomString(6);
        session.setData(Constant.GENERATED_OTP, generateOTP);

        Map<String, String> params = new HashMap<>();
        params.put(Constant.TEXT, "আপনার একাউন্ট যাচাইকরণ OTP: " + session.getData(Constant.GENERATED_OTP));
        params.put(Constant.MOBILE, oPhoneNumber);
        params.put(Constant.SEND_SMS, Constant.GetVal);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(Constant.ERROR)) {

                        JSONObject data = object.getJSONObject(Constant.DATA);

                        if (data.getString("status_code").equals("200")){
                            setSnackBar("প্রিয় গ্রাহক, আপনার মোবাইল নম্বর ভেরিফিকেশনের জন্য আপনার মোবাইল নাম্বারে আমরা একটি OTP পাঠিয়েছি। অনুগ্রহ পূর্বক আপনার মোবাইলের মেসেজ চেক করুন।", getString(R.string.btn_ok), "done");
                            edtMobileVerify.setEnabled(false);
//                            edtCountryCodePicker.setCcpClickable(false);
                            btnVerify.setText(getString(R.string.verify_otp));
                            lytOTP.setVisibility(View.VISIBLE);
                        }else {
                            setSnackBar("Error ", getString(R.string.btn_ok), "done");
                        }

                        if (resendOTP) {
                            Toast.makeText(activity, getString(R.string.otp_resend_alert), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            edtMobileVerify.setEnabled(false);
//                            edtCountryCodePicker.setCcpClickable(false);
                            btnVerify.setText(getString(R.string.verify_otp));
                            lytOTP.setVisibility(View.VISIBLE);

                            btnVerify.setOnClickListener(v -> resetPassword());

                            new CountDownTimer(60000, 1000) {
                                @SuppressLint("SetTextI18n")
                                public void onTick(long millisUntilFinished) {
                                    timerOn = true;
                                    // Used for formatting digit to be in 2 digits only
                                    NumberFormat f = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        f = new DecimalFormat("00");
                                    }
                                    long min = (millisUntilFinished / 60000) % 60;
                                    long sec = (millisUntilFinished / 1000) % 60;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tvTimer.setText(f.format(min) + ":" + f.format(sec));
                                    }
                                }

                                public void onFinish() {
                                    resendOTP = false;
                                    timerOn = false;
                                    tvTimer.setVisibility(View.GONE);
                                    tvResend.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

                                    tvResend.setOnClickListener(v -> {

                                        Map<String, String> params = new HashMap<>();
                                        params.put(Constant.TEXT, "আপনার একাউন্ট যাচাইকরণ OTP: " + session.getData(Constant.GENERATED_OTP));
                                        params.put(Constant.MOBILE, oPhoneNumber);
                                        params.put(Constant.SEND_SMS, Constant.GetVal);
                                        ApiConfig.RequestToVolley((result, response) -> {
                                            if (result) {
                                                try {
                                                    JSONObject object = new JSONObject(response);
                                                    if (!object.getBoolean(Constant.ERROR)) {
                                                        setSnackBar("প্রিয় গ্রাহক, আপনার মোবাইল নম্বর ভেরিফিকেশনের জন্য আপনার মোবাইল নাম্বারে আমরা একটি OTP পাঠিয়েছি। অনুগ্রহ পূর্বক আপনার মোবাইলের মেসেজ চেক করুন।", getString(R.string.btn_ok), "done");
                                                    } else {
                                                        setSnackBar("Something went wrong, Please try again latter", "Retry", "none");
                                                    }
                                                } catch (JSONException e) {
                                                    Log.d("exception", e.getMessage());
                                                }

                                                resendOTP = true;
                                                resendOTP = false;
                                                timerOn = false;
                                                tvTimer.setVisibility(View.GONE);
                                                tvResend.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                                                tvResend.setClickable(false);
                                                Log.d("response", response);
                                            }
                                        }, activity, Constant.SMS_SEND_URL, params, true);
                                    });
                                }
                            }.start();
                        }

                    } else {
                        setSnackBar("Something went wrong, Please try again latter", "Retry", "reset");
                    }
                } catch (JSONException e) {
                    Log.d("exception", e.getMessage());
                }
                Log.d("response", response);
            }
        }, activity, Constant.SMS_SEND_URL, params, true);
    }

    private void resetPassword() {

        String otptext = Objects.requireNonNull(pinViewOTP.getText()).toString().trim();
        if (ApiConfig.CheckValidation(otptext, false, false)) {
            pinViewOTP.requestFocus();
            pinViewOTP.setError(getString(R.string.enter_otp));
        } else if (otptext.isEmpty()){
            pinViewOTP.requestFocus();
            pinViewOTP.setError(getString(R.string.enter_otp));
        }else {
            if (otptext.equals(session.getData(Constant.GENERATED_OTP))){
                lytOTP.setVisibility(View.GONE);
                lytVerify.setVisibility(View.GONE);
                lytResetPass.setVisibility(View.VISIBLE);
            }else {
                setSnackBar("Verification Field, Please try again with right OTP", "OK", "reset");
            }
        }
    }

    public void setSnackBar(String message, String action, String type) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, view -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            if (type.equals("done")){
                intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
                snackbar.dismiss();
                startActivity(intent);
            }
        });

        snackbar.setActionTextColor(Color.RED);
        View snackBarView = snackbar.getView();
        TextView textView = snackBarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        new CountDownTimer(7000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                snackbar.dismiss();
            }
        }.start();
    }

    public void ForgotPassword() {
        String reset_psw = edtResetPass.getText().toString();
        String reset_c_psw = edtResetCPass.getText().toString();

        if (ApiConfig.CheckValidation(reset_psw, false, false)) {
            edtResetPass.requestFocus();
            edtResetPass.setError(getString(R.string.enter_new_pass));
        } else if (ApiConfig.CheckValidation(reset_c_psw, false, false)) {
            edtResetCPass.requestFocus();
            edtResetCPass.setError(getString(R.string.enter_confirm_pass));
        } else if (!reset_psw.equals(reset_c_psw)) {
            edtResetCPass.requestFocus();
            edtResetCPass.setError(getString(R.string.pass_not_match));
        } else {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.PAGE, Constant.FORGOT_PASSWORD_MOBILE);
            params.put(Constant.PASSWORD, reset_c_psw);
            params.put(Constant.MOBILE, mobile);
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            setSnackBar(object.getString(Constant.MESSAGE), "OK", "reset");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("exception", e.getMessage());
                    }
                    Log.d("response", response);
                }
            }, activity, Constant.AUTH_LOGIN_URL, params, true);
        }
    }

    @Override
    public void onBackPressed() {
        session.goBack(activity);
    }
}