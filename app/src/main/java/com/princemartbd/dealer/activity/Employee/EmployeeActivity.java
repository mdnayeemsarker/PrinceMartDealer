package com.princemartbd.dealer.activity.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.princemartbd.dealer.BuildConfig;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Base.ScannerActivity;
import com.princemartbd.dealer.activity.Base.WebViewActivity;
import com.princemartbd.dealer.activity.Dealer.DeaProfileActivity;
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

public class EmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Session session;
    private Activity activity;

    private LinearLayout transactionLayout;

    private Boolean isTran = false;

    private RecyclerView transactionRecyclerView;

    private AllTransactionsAdapter allTransactionsAdapter;
    private ArrayList<AllTransactionsModel> allTransactionsModelArrayList;

    String getIntentData;

    private final String[] permission = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.CAMERA"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        activity = this;
        session = new Session(activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PrinceMart Employee");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        transactionRecyclerView = findViewById(R.id.transactionRecyclerViewId);

        CardView transactionCardView = findViewById(R.id.transactionCardViewId);

        CardView allTranCardView = findViewById(R.id.allTranCardViewId);
        CardView successTranCardView = findViewById(R.id.successTranCardViewId);
        CardView pendingTranCardView = findViewById(R.id.pendingTranCardViewId);
        CardView newTranCardView = findViewById(R.id.newTranCardViewId);
        CardView cancelTranCardView = findViewById(R.id.cancelTranCardViewId);

        CardView transferCardView = findViewById(R.id.transferCardViewId);

        transactionLayout = findViewById(R.id.transactionLayoutId);

        transactionRecyclerView = findViewById(R.id.transactionRecyclerViewId);

        if (ApiConfig.isConnected(activity) && session.IS_LOGGED_IN(activity)){
            session.IS_LOGGED_IN(activity);

            transferCardView.setOnClickListener(v -> {
                tranViewClose();
                requestPermissions(permission, 80);
            });
            allTranCardView.setOnClickListener(v -> {
                getIntentData = Constant.ABMN_ALL_TRAN;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Transactions");
                }
                tranViewClose();
                getData(Constant.ABMN_ALL_TRAN);
            });
            successTranCardView.setOnClickListener(v -> {
                getIntentData = Constant.ABMN_SUCCESS_TRAN;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Success Transactions");
                }
                tranViewClose();
                getData(Constant.ABMN_SUCCESS_TRAN);
            });
            pendingTranCardView.setOnClickListener(v -> {
                getIntentData = Constant.ABMN_PENDING_TRAN;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Pending Transactions");
                }
                tranViewClose();
                getData(Constant.ABMN_PENDING_TRAN);
            });
            newTranCardView.setOnClickListener(v -> {
                getIntentData = Constant.ABMN_NEW_TRAN;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All New Transactions");
                }
                tranViewClose();
                getData(Constant.ABMN_NEW_TRAN);
            });
            cancelTranCardView.setOnClickListener(v -> {
                getIntentData = Constant.ABMN_CANCELED_TRAN;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("All Canceled Transactions");
                }
                tranViewClose();
                getData(Constant.ABMN_CANCELED_TRAN);
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            transactionRecyclerView.setLayoutManager(layoutManager);
            allTransactionsModelArrayList = new ArrayList<>();
            allTransactionsAdapter = new AllTransactionsAdapter(activity, allTransactionsModelArrayList);
            transactionCardView.setOnClickListener(v ->  tranView());
        }
        getData(Constant.ABMN_ALL_TRAN);

        getOnlineVersion();

    }

    private void getOnlineVersion() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject data = jsonArray.getJSONObject(i);
                        String onlinePackageID = data.getString("packageID");
                        String onlineVersionName = data.getString("versionName");
                        if (activity.getPackageName().equals(onlinePackageID)){
                            String versionName = BuildConfig.VERSION_NAME;
                            if (!onlineVersionName.equals(versionName)){
                                ApiConfig.newUpdate(activity);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.d("app_update", e.getMessage());
                }
            }
        }, activity, Constant.APP_UPDATE_URL, params, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData(String getIntentData) {

        allTransactionsModelArrayList.clear();
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
                        allTransactionsAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(activity, getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(Constant.ALL_TRANSACTIONS, response);
        }, activity, Constant.ALL_TRANSACTIONS, params, true);

    }

    private void tranView(){
        if (!isTran){
            isTran = true;
            transactionLayout.setVisibility(View.VISIBLE);
        }else {
            isTran = false;
            transactionLayout.setVisibility(View.GONE);
        }
    }
    private void tranViewClose(){
        isTran = false;
        transactionLayout.setVisibility(View.GONE);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.navTransferByScanId:
                if (ApiConfig.isConnected(activity)) {
                    tranViewClose();
                    requestPermissions(permission, 80);
                }
                break;
            case R.id.navTransactionId:
                if (ApiConfig.isConnected(activity)) {
                    tranView();
                }
                break;
            case R.id.navProfileId:
                if (ApiConfig.isConnected(activity)) {
                    startActivity(new Intent(activity, DeaProfileActivity.class));
                }
                break;
            case R.id.navPrivacyPolicyId:
                if (ApiConfig.isConnected(activity)) {
                    startActivity(new Intent(activity, WebViewActivity.class).putExtra("type", "Privacy Policy"));
                }
                break;
            case R.id.navTermsAndConditionId:
                if (ApiConfig.isConnected(activity)) {
                    startActivity(new Intent(activity, WebViewActivity.class).putExtra("type", "Terms & Conditions"));
                }
                break;
            case R.id.navAboutUsId:
                if (ApiConfig.isConnected(activity)) {
                    startActivity(new Intent(activity, WebViewActivity.class).putExtra("type", "About Us"));
                }
                break;
            case R.id.navLogOutId:
                if (ApiConfig.isConnected(activity)) {
                    session.logoutUserConfirmation(activity);
                }
                break;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 80) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permission, 80);
            }
        } else {
            startActivity(new Intent(activity, ScannerActivity.class));
        }
    }

}
