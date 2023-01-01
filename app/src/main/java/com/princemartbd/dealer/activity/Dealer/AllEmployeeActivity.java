package com.princemartbd.dealer.activity.Dealer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.adapter.AllEmployeeAdapter;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.RanNumGen;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.AllEmployeeModel;
import com.princemartbd.dealer.ui.PinView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AllEmployeeActivity extends AppCompatActivity {

    private Activity activity;
    private Session session;

    private RecyclerView allMarketerRecyclerView;
    private AllEmployeeAdapter allEmployeeAdapter;
    private ArrayList<AllEmployeeModel> allEmployeeModelArrayList;

    private EditText edtName;
    private EditText edtEmail;
    private EditText imgLoginPassword;

    private CardView noEmployeeCadView;
    private String mobile;
    private EditText edtMobileVerify;
    private ScrollView lytVerify;
    private LinearLayout lytOTP;

    private LinearLayout addEmployeeLayout;

    boolean resendOTP = false;
    boolean timerOn;

    private PinView pinViewOTP;

    private Button btnVerify;

//    CountryCodePicker edtCountryCodePicker;
    TextView tvTimer, tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_employee);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        activity = this;
        session = new Session(activity);

        toolbar.setNavigationOnClickListener(v -> session.goBackDealer(activity));
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        imgLoginPassword = findViewById(R.id.imgLoginPassword);
        noEmployeeCadView = findViewById(R.id.noEmployeeCadViewId);

        addEmployeeLayout = findViewById(R.id.addEmployeeLayoutId);
        LinearLayout allEmployeeLayout = findViewById(R.id.allEmployeeLayoutId);

        Button btnLogin = findViewById(R.id.btnLogin);
        allMarketerRecyclerView = findViewById(R.id.allMarketerRecyclerViewId);

        session.IS_LOGGED_IN(activity);


        lytVerify = findViewById(R.id.lytVerify);
        lytOTP = findViewById(R.id.lytOTP);

        tvResend = findViewById(R.id.tvResend);
        tvTimer = findViewById(R.id.tvTimer);

//        edtCountryCodePicker = findViewById(R.id.edtCountryCodePicker);
//        edtCountryCodePicker.setCountryForNameCode("BD");

        edtMobileVerify = findViewById(R.id.edtMobileVerify);
        pinViewOTP = findViewById(R.id.pinViewOTP);

        btnVerify = findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            checkNumber();
        });

        String getIntentData = getIntent().getStringExtra(Constant.ABMN);
        switch (getIntentData) {
            case Constant.ABMN_EMP_ADD:
                lytVerify.setVisibility(View.VISIBLE);
                allEmployeeLayout.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Dealer Add Employee");
                }
                if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)){
                    btnLogin.setOnClickListener(v -> {
                        ApiConfig.hideKeyboard(activity, v);
                        validation();
                    });
                }
                break;
            case Constant.ABMN_EMP_ALL:
                lytVerify.setVisibility(View.GONE);
                allEmployeeLayout.setVisibility(View.VISIBLE);
                allMarketerRecyclerView.setVisibility(View.VISIBLE);
                noEmployeeCadView.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Dealer All Employee");
                }
                if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)){
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    allMarketerRecyclerView.setLayoutManager(layoutManager);
                    allEmployeeModelArrayList = new ArrayList<>();
                    allEmployeeAdapter = new AllEmployeeAdapter(activity, allEmployeeModelArrayList);
                    getDataAllEmployee();
                }
                break;
        }
    }

    public void checkNumber() {
        session.setData(Constant.COUNTRY_CODE, "88");

        mobile = edtMobileVerify.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.VERIFY_USER);
        params.put(Constant.MOBILE, "0"+mobile);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    String phoneNumber = ("+" + session.getData(Constant.COUNTRY_CODE) + "0" + mobile);
                    if (!object.getBoolean(Constant.ERROR)) {
                        generateOTP(phoneNumber);
                    } else {
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

                            btnVerify.setOnClickListener(v -> checkOtp());

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

    private void checkOtp() {
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
                addEmployeeLayout.setVisibility(View.VISIBLE);
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

    private void validation() {
        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = imgLoginPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            edtName.requestFocus();
            edtName.setError("Name field is mandatory.!");
        } else if (TextUtils.isEmpty(password)) {
            imgLoginPassword.requestFocus();
            imgLoginPassword.setError("Password field is mandatory.!");
        } else {
            addEmployee(name, email, mobile, password);
        }
    }

    private void addEmployee(String name, String email, String mobile, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.ADD_EMPLOYEES);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.NAME, name);
        params.put(Constant.EMAIL, email);
        params.put(Constant.MOBILE, mobile);
        params.put(Constant.PASSWORD, password);
        params.put(Constant.STATUS, "4");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        startActivity(new Intent(activity, DealerActivity.class));
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
                Log.d(Constant.ADD_EMPLOYEE_URL, response);
            }
        }, activity, Constant.ADD_EMPLOYEE_URL, params, true);
    }

    @Override
    public void onBackPressed() {
        session.goBackDealer(activity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getDataAllEmployee() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.ALL_EMPLOYEES);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONArray data = jsonObject.getJSONArray(Constant.EMPLOYEES);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject affiliaters = data.getJSONObject(i);

                            AllEmployeeModel allEmployeeModel = new AllEmployeeModel(
                                    affiliaters.getString(Constant.ID),
                                    affiliaters.getString(Constant.ADDED_BY),
                                    affiliaters.getString(Constant.ADDER_TYPE),
                                    affiliaters.getString(Constant.NAME),
                                    affiliaters.getString(Constant.EMAIL),
                                    affiliaters.getString(Constant.PROFILE),
                                    affiliaters.getString(Constant.COUNTRY_CODE),
                                    affiliaters.getString(Constant.MOBILE),
                                    affiliaters.getString(Constant.BALANCE),
                                    affiliaters.getString(Constant.FCM_ID),
                                    affiliaters.getString(Constant.ADDRESS),
                                    affiliaters.getString(Constant.AREA_ID),
                                    affiliaters.getString(Constant.CITY_ID),
                                    affiliaters.getString(Constant.PINCODE_ID),
                                    affiliaters.getString(Constant.STATUS),
                                    affiliaters.getString(Constant.TYPE),
                                    affiliaters.getString(Constant.CREATED_AT)
                                );
                            allEmployeeModelArrayList.add(allEmployeeModel);
                        }
                        allEmployeeAdapter = new AllEmployeeAdapter(activity, allEmployeeModelArrayList);
                        allMarketerRecyclerView.setAdapter(allEmployeeAdapter);
                    }else {
                        allMarketerRecyclerView.setVisibility(View.GONE);
                        noEmployeeCadView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(Constant.ALL_EMPLOYEES_URL, response);
        }, activity, Constant.ALL_EMPLOYEES_URL, params, true);
    }
}