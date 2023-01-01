package com.princemartbd.dealer.activity.Dealer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.adapter.AllTransactionsAdapter;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.AllTransactionsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransactionsActivity extends AppCompatActivity {

    private Activity activity;
    private Session session;

    private RecyclerView transactionRecyclerView;

    private String getIntentData;

    private AllTransactionsAdapter allTransactionsAdapter;
    private ArrayList<AllTransactionsModel> allTransactionsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        activity = this;
        session = new Session(activity);

        toolbar.setNavigationOnClickListener(v -> finish());

        transactionRecyclerView = findViewById(R.id.transactionRecyclerViewId);

//        Toast.makeText(activity, session.getData(Constant.USER_ID), Toast.LENGTH_SHORT).show();

        getIntentData = getIntent().getStringExtra(Constant.ABMN);

        switch (getIntentData) {
            case Constant.ABMN_ALL_TRAN:
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Transactions");
                }
                break;
            case Constant.ABMN_SUCCESS_TRAN:
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Success Transactions");
                }
                break;
            case Constant.ABMN_PENDING_TRAN:
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Pending Transactions");
                }
                break;
            case Constant.ABMN_NEW_TRAN:
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All New Transactions");
                }
                break;
            case Constant.ABMN_CANCELED_TRAN:
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Canceled Transactions");
                }
                break;
        }

        if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)) {
            session.IS_LOGGED_IN(activity);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            transactionRecyclerView.setLayoutManager(layoutManager);
            allTransactionsModelArrayList = new ArrayList<>();
            allTransactionsAdapter = new AllTransactionsAdapter(activity, allTransactionsModelArrayList);
        }

        getData();
    }

    private void getData() {

        Map<String, String> params = new HashMap<>();

        switch (getIntentData) {
            case Constant.ABMN_ALL_TRAN:
                params.put(Constant.PAGE, Constant.ABMN_ALL_TRAN);
                break;
            case Constant.ABMN_SUCCESS_TRAN:
                params.put(Constant.PAGE, Constant.ABMN_SUCCESS_TRAN);
                break;
            case Constant.ABMN_PENDING_TRAN:
                params.put(Constant.PAGE, Constant.ABMN_PENDING_TRAN);
                break;
            case Constant.ABMN_NEW_TRAN:
                params.put(Constant.PAGE, Constant.ABMN_NEW_TRAN);
                break;
            case Constant.ABMN_CANCELED_TRAN:
                params.put(Constant.PAGE, Constant.ABMN_CANCELED_TRAN);
                break;
        }
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONArray transactions = jsonObject.getJSONArray("transactions");

                        for (int i = 0; i < transactions.length(); i++) {
                            JSONObject data = transactions.getJSONObject(i);

                            String id = data.getString(Constant.ID);
                            String seller_id = data.getString(Constant.SELLER_ID);
                            String dealer_id = data.getString(Constant.DEALER_ID);
                            String employee_id = data.getString(Constant.EMPLOYEE_ID);
                            String trx = data.getString(Constant.TRX);
                            String amount = data.getString(Constant.AMOUNT);
                            String message = data.getString(Constant.MESSAGE);
                            String status = data.getString(Constant.STATUS);
                            String updated_at = data.getString(Constant.UPDATED_AT);
                            String accepted_at = data.getString(Constant.ACCEPTED_AT);
                            String created_id = data.getString(Constant.CREATED_AT);
                            String seller_name = data.getString(Constant.SELLER_NAME);
                            String employee_name = data.getString(Constant.EMPLOYEE_NAME);

                            AllTransactionsModel allTransactionsModel = new AllTransactionsModel(id, seller_id, dealer_id, employee_id, trx, amount, message, status, updated_at, accepted_at, created_id, seller_name, employee_name);
                            allTransactionsModelArrayList.add(allTransactionsModel);
                        }
                        allTransactionsAdapter = new AllTransactionsAdapter(activity, allTransactionsModelArrayList);
                        transactionRecyclerView.setAdapter(allTransactionsAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(Constant.ALL_TRANSACTIONS, response);
        }, activity, Constant.ALL_TRANSACTIONS, params, true);

    }

    @Override
    public void onBackPressed() {
        session.goBackWithEmp(activity);
    }
}