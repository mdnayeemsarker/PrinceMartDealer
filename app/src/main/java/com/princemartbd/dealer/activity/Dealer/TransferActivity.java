package com.princemartbd.dealer.activity.Dealer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransferActivity extends AppCompatActivity {

    private Activity activity;
    private Session session;

    private CheckBox chPrivacy;
    private EditText transferAmount;
    private EditText messageInput;

    private LinearLayout transferTranLayout;
    private LinearLayout manageTranLayout;

    private TextView transactionType;
    private TextView transactionId;
    private TextView amountTV;
    private TextView messageTV;
    private TextView shopName;
    private TextView shopEmail;
    private TextView shopPhone;
    private TextView shopBalance;
    private Button btnManageTran;

    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        activity = this;
        session = new Session(activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(v -> goBackActivity());

        String getIntentData = getIntent().getStringExtra(Constant.ABMN_QR_RESULT);

        if (!getIntentData.isEmpty()) {
            getSellerData(getIntentData);
        }


        chPrivacy = findViewById(R.id.chPrivacy);
        transferAmount = findViewById(R.id.transferAmountId);
        messageInput = findViewById(R.id.messageInputId);

        manageTranLayout = findViewById(R.id.manageTranLayoutId);
        transferTranLayout = findViewById(R.id.transferTranLayoutId);

        transactionType = findViewById(R.id.transactionTypeId);
        transactionId = findViewById(R.id.transactionIdId);
        amountTV = findViewById(R.id.amountId);
        messageTV = findViewById(R.id.messageId);
        shopName = findViewById(R.id.shopNameId);
        shopEmail = findViewById(R.id.shopEmailId);
        shopPhone = findViewById(R.id.shopPhoneId);
        shopBalance = findViewById(R.id.shopBalanceId);
        btnManageTran = findViewById(R.id.btnManageTranId);

        CardView transfer = findViewById(R.id.transferId);

        transfer.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);

            String amount = transferAmount.getText().toString();
            String message = messageInput.getText().toString();

            if (!balance.isEmpty()) {
                if (amount.isEmpty()) {
                    transferAmount.requestFocus();
                    transferAmount.setError(getString(R.string.err_amount_empty));
                } else {
                    int intBalance = Integer.parseInt(balance);
                    int intTransferAmount = Integer.parseInt(amount);

                    if (intBalance > intTransferAmount) {
                        if (chPrivacy.isChecked()) {
                            transferBalance(getIntentData, amount, message);
                        } else {
                            Toast.makeText(activity, getString(R.string.alert_privacy_msg), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.alert_transfer_msg), Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
    }

    private void getSellerData(String getIntentData) {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SELLER_DATA, Constant.GET_CON_VAL_1);
        params.put(Constant.SLUG, getIntentData);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        boolean hasSeller = false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            if (data.getString("slug").equals(getIntentData)) {

                                hasSeller = true;

                                String id = data.getString(Constant.ID);
                                String name = data.getString(Constant.NAME);
                                String store_name = data.getString(Constant.STORE_NAME);
                                String slug = data.getString(Constant.SLUG);
                                String email = data.getString(Constant.EMAIL);
                                String mobile = data.getString(Constant.MOBILE);
                                balance = data.getString(Constant.BALANCE);
                                String seller_address = data.getString(Constant.SELLER_ADDRESS);

                                shopName.setText(store_name);
                                shopEmail.setText(email);
                                shopPhone.setText(mobile);
                                shopBalance.setText(balance);
                            }
                        }

                        if (!hasSeller) {
                            getDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("exception", e.getMessage());
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d("GET_SELLER_DATA_URL", response);
        }, activity, Constant.GET_SELLER_DATA_URL, params, true);
    }

    private void getDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Attention");
        alertDialog.setMessage("Seller Not Found");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            session.goBackWithEmp(activity);
        });
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void transferBalance(String slugData, String amount, String message) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.TRANSFER);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.SLUG, slugData);
        params.put(Constant.AMOUNT, amount);
        params.put(Constant.MESSAGE, message);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
//                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();

                        if (jsonObject.has("transaction")) {
                            JSONObject object = jsonObject.getJSONObject("transaction");
                            transferTranLayout.setVisibility(View.GONE);
                            manageTranLayout.setVisibility(View.VISIBLE);
                            transactionType.setText("Pending Transaction");
                            transactionType.setTextColor(ContextCompat.getColor(activity, R.color.white));
                            transactionType.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                            transactionId.setText("Trx.Id: " + jsonObject.getString("transaction_id"));
                            amountTV.setText("৳: " + object.getString("amount"));
                            messageTV.setText("Message: " + object.getString("message"));
                        } else {
                            transferTranLayout.setVisibility(View.GONE);
                            manageTranLayout.setVisibility(View.VISIBLE);

                            transactionType.setText("New Transaction");
                            transactionType.setTextColor(ContextCompat.getColor(activity, R.color.white));
                            transactionType.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_green));
                            transactionId.setText("Trx.Id: " + jsonObject.getString("transaction_id"));
                            amountTV.setText("৳: " + amount);
                            messageTV.setText("Message: " + message);
                        }

                        btnManageTran.setOnClickListener(v -> startActivity(new Intent(activity, TransactionsActivity.class).putExtra(Constant.ABMN, Constant.ABMN_PENDING_TRAN)));
                    } else {
                        Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("exception", e.getMessage());
                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d("ALL_TRANSACTIONS", response);
        }, activity, Constant.ALL_TRANSACTIONS, params, true);
    }

    @Override
    public void onBackPressed() {
        goBackActivity();
    }

    private void goBackActivity() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Attention");
        alertDialog.setMessage("If you get back then the Balance Transfer will be Correspond");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            session.goBackWithEmp(activity);
        });
        alertDialog.show();
    }
}