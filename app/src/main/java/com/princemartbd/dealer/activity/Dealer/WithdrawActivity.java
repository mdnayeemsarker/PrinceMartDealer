package com.princemartbd.dealer.activity.Dealer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.adapter.WithdrawAdapter;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.WithdrawModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WithdrawActivity extends AppCompatActivity {

    private Activity activity;
    private Session session;

    private double main_balance;
    private double instant_balance;
    private Button btnRequest;
    private TextView tvTotalBalance;

    private LinearLayout allWithdrawLayout;
    private LinearLayout addWithdrawLayout;

    private FrameLayout rootFrameLayout;

    private WithdrawAdapter withdrawAdapter;
    private ArrayList<WithdrawModel> withdrawModelArrayList;
    private RecyclerView withdrawRecyclerView;

    private EditText edtAmount;
    private EditText edtMessage;

    private String balance;

    private CheckBox chPrivacy;

    private CardView noWithdrawCadView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        activity = this;
        session = new Session(activity);

        toolbar.setNavigationOnClickListener(v -> session.goBackWithEmp(activity));

        session.IS_LOGGED_IN(activity);

        edtAmount = findViewById(R.id.edtAmount);
        edtMessage = findViewById(R.id.edtMessage);
        btnRequest = findViewById(R.id.btnRequest);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);

        allWithdrawLayout = findViewById(R.id.allWithdrawLayoutId);
        addWithdrawLayout = findViewById(R.id.addWithdrawLayoutId);
        rootFrameLayout = findViewById(R.id.rootFrameLayoutId);

        noWithdrawCadView = findViewById(R.id.noWithdrawCadViewId);
        withdrawRecyclerView = findViewById(R.id.allWithdrawRecyclerViewId);
        chPrivacy = findViewById(R.id.chPrivacy);

        String getIntentData = getIntent().getStringExtra(Constant.ABMN);
        getIntentDataValidation(getIntentData);

        balance = session.getData(Constant.TOTAL_BALANCE);
        if (!balance.equals("null")) {
            main_balance = Double.parseDouble(balance);
            tvTotalBalance.setText("Balance: " + main_balance);
        }
    }

    private void getIntentDataValidation(String getIntentData) {
        switch (getIntentData) {
            case Constant.ABMN_WITH_REQUEST:
                addWithdrawLayout.setVisibility(View.VISIBLE);
                allWithdrawLayout.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Dealer Withdraw Request");
                }
                if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)) {
                    edtAmount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (!s.toString().equals("")) {
                                try {
                                    instant_balance = Double.parseDouble(s.toString());
                                    double tempBalance = main_balance - instant_balance;
                                    tvTotalBalance.setText("Total Balance: " + tempBalance);
                                    if (instant_balance >= 10) {
                                        btnRequest.setVisibility(View.VISIBLE);
                                        btnRequest.setOnClickListener(v -> {
                                            ApiConfig.hideKeyboard(activity, v);

                                            if (chPrivacy.isChecked()){
                                                gettingAmount();
                                            }
                                            else {
                                                Toast.makeText(activity, getResources().getText(R.string.alert_privacy_msg), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        btnRequest.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(activity, "You can not use special Character \"" + s + "\" here.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                tvTotalBalance.setText("Total Balance: " + balance);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
                break;
            case Constant.ABMN_WITH_HISTORY:
                addWithdrawLayout.setVisibility(View.GONE);
                allWithdrawLayout.setVisibility(View.VISIBLE);
                withdrawRecyclerView.setVisibility(View.VISIBLE);
                noWithdrawCadView.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Dealer Withdraw History");
                }
                if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    withdrawRecyclerView.setLayoutManager(layoutManager);
                    withdrawModelArrayList = new ArrayList<>();
                    withdrawAdapter = new WithdrawAdapter(activity, withdrawModelArrayList);
                    getDataAllWithHistory();
                }
                rootFrameLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                break;
        }
    }

    private void gettingAmount() {

        String message = edtMessage.getText().toString().trim();

        if (!message.isEmpty()){
            Map<String, String> params = new HashMap<>();
            params.put(Constant.PAGE, Constant.SEND_REQUEST);
            params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
            params.put(Constant.AMOUNT, String.valueOf(instant_balance));
            params.put(Constant.MESSAGE, message);

            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean(Constant.ERROR)) {
                            setSnackBar(jsonObject.getString(Constant.MESSAGE));
                            startActivity(new Intent(activity, DealerActivity.class));
                        }else {
                            setSnackBar(getResources().getString(R.string.api_error_msg));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setSnackBar(getResources().getString(R.string.api_error_msg));
                    }
                }
                Log.d(Constant.WITHDRAW_REQUEST, response);
            }, activity, Constant.WITHDRAW_REQUEST, params, true);
        }else {
            edtMessage.requestFocus();
            edtMessage.setError(getString(R.string.message_error));
        }
    }

    private void setSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.RED);
        View snackBarView = snackbar.getView();
        TextView textView = snackBarView.findViewById(R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();

        new CountDownTimer(15000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                snackbar.dismiss();
            }
        }.start();
    }

    private void getDataAllWithHistory() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.GET_REQUEST);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONArray dataArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);

                            WithdrawModel withdrawModel = new WithdrawModel(
                                    data.getString(Constant.ID),
                                    data.getString(Constant.TYPE),
                                    data.getString(Constant.TYPE_ID),
                                    data.getString(Constant.AMOUNT),
                                    data.getString(Constant.MESSAGE),
                                    data.getString(Constant.STATUS),
                                    data.getString(Constant.LAST_UPDATED),
                                    data.getString(Constant.DATE_CREATED)
                            );
                            withdrawModelArrayList.add(withdrawModel);
                        }
                        withdrawAdapter = new WithdrawAdapter(activity, withdrawModelArrayList);
                        withdrawRecyclerView.setAdapter(withdrawAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(Constant.WITHDRAW_REQUEST, response);
        }, activity, Constant.WITHDRAW_REQUEST, params, true);
    }

    @Override
    public void onBackPressed() {
      session.goBackWithEmp(activity);
    }
}