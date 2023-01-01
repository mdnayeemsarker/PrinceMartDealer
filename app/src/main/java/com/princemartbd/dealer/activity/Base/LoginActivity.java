package com.princemartbd.dealer.activity.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Dealer.DealerActivity;
import com.princemartbd.dealer.activity.Employee.EmployeeActivity;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText imgLoginPassword, edtLoginMobile, edtName, edtEmail;

    TextView tvForgotPass;
    Session session;
    Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = LoginActivity.this;
        session = new Session(activity);

        Button btnLogin = findViewById(R.id.btnLogin);

        imgLoginPassword = findViewById(R.id.imgLoginPassword);
        edtLoginMobile = findViewById(R.id.edtLoginMobile);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        tvForgotPass = findViewById(R.id.tvForgotPass);

        edtLoginMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
        imgLoginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);

        Utils.setHideShowPassword(imgLoginPassword);

        onStart();

        btnLogin.setOnClickListener(this::getInput);
        findViewById(R.id.tvForgotPass).setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            Intent intent = new Intent(activity, ForgotPassActivity.class);
            startActivity(intent);
        });

        getSavedData();
    }

    private void getSavedData() {
        if (!session.getData(Constant.MOBILE).equals("")){
            edtLoginMobile.setText(session.getData(Constant.MOBILE));
        }
        if (!session.getData(Constant.PASSWORD).equals("")){
            imgLoginPassword.setText(session.getData(Constant.PASSWORD));
        }
    }

    private void getInput(View view) {
        ApiConfig.hideKeyboard(activity, view);
        String mobile = edtLoginMobile.getText().toString();
        String password = imgLoginPassword.getText().toString();

        if (TextUtils.isEmpty(mobile)) {
            edtLoginMobile.requestFocus();
            edtLoginMobile.setError("Mobile number is mandatory");
        } else if (TextUtils.isEmpty(password)) {
            imgLoginPassword.requestFocus();
            imgLoginPassword.setError("Mobile number is mandatory");
        } else {
            UserLogin(mobile, password);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ApiConfig.isConnected(activity)) {
            if (session.getIsLog()) {
                Intent intent = new Intent();
                if (session.getData(Constant.DEALER_TYPE).equals(Constant.DEALER)) {
                    intent = new Intent(activity, DealerActivity.class);
                } else  if (session.getData(Constant.DEALER_TYPE).equals(Constant.EMPLOYEE)) {
                    intent = new Intent(activity, EmployeeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    public void UserLogin(String mobile, String password) {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.LOGIN);
        params.put(Constant.MOBILE, mobile);
        params.put(Constant.PASSWORD, password);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {

                        String dealer_type = jsonObject.getString(Constant.DEALER_TYPE);

                        JSONObject data;

                        Intent intent;
                        switch (dealer_type) {
                            case Constant.DEALER:
                                data = jsonObject.getJSONObject(Constant.DEALER);
                                getJSONData(data, dealer_type, password);
                                intent = new Intent(activity, DealerActivity.class);
                                break;
                            case Constant.EMPLOYEE:
                                data = jsonObject.getJSONObject(Constant.EMPLOYEE);
                                getJSONData(data, dealer_type, password);
                                intent = new Intent(activity, EmployeeActivity.class);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + dealer_type);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                    Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(Constant.LOGIN, response);
        }, activity, Constant.AUTH_LOGIN_URL, params, true);
    }

    private void getJSONData(JSONObject data, String dealer_type, String password) throws JSONException {

        String user_id = data.getString(Constant.USER_ID);
        String name = data.getString(Constant.NAME);
        String email = data.getString(Constant.EMAIL);
        String profile = data.getString(Constant.PROFILE);
        String country_code = data.getString(Constant.COUNTRY_CODE);
        String mobileDB = data.getString(Constant.MOBILE);
        String balance = data.getString(Constant.BALANCE);
        String fcm_id = data.getString(Constant.FCM_ID);
        String status = data.getString(Constant.STATUS);
        String created_at = data.getString(Constant.CREATED_AT);

        session.setUserData(dealer_type, user_id, name, email, profile, country_code, mobileDB, password, balance, fcm_id, status, created_at);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}