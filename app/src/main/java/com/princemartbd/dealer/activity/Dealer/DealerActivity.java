package com.princemartbd.dealer.activity.Dealer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.princemartbd.dealer.BuildConfig;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Base.ScannerActivity;
import com.princemartbd.dealer.activity.Base.WebViewActivity;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DealerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Session session;
    private Activity activity;

    private ImageView profileImage;

    private TextView totalBalanceTextView;
    private TextView todayBalanceTextView;
    private TextView yesterdayBalanceTextView;
    private TextView weeklyBalanceTextView;
    private TextView monthlyBalanceTextView;
    private TextView lastMonthBalanceTextView;
    private TextView last3MonthBalanceTextView;
    private TextView yearlyBalanceTextView;
    private TextView lastYearBalanceTextView;

    private LinearLayout employeeLayout;
    private LinearLayout earningLayout;
    private LinearLayout withdrawLayout;
    private LinearLayout transactionLayout;

    private Boolean isBalance = false;
    private Boolean isEmployee = false;
    private Boolean isEarning = false;
    private Boolean isWithdraw = false;
    private Boolean isTran = false;

    private Intent intent;

    private final String[] permission = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.CAMERA"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("PrinceMart Dealer");
        }

        activity = this;
        session = new Session(activity);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        intent = new Intent();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CardView totalBalanceCardView = findViewById(R.id.totalBalanceCardViewId);
        CardView employeeCardView = findViewById(R.id.employeeCardViewId);
        CardView earningCardView = findViewById(R.id.earningCardViewId);
        CardView withdrawCardView = findViewById(R.id.withdrawCardViewId);
        CardView transactionCardView = findViewById(R.id.transactionCardViewId);

        CardView allTranCardView = findViewById(R.id.allTranCardViewId);
        CardView successTranCardView = findViewById(R.id.successTranCardViewId);
        CardView pendingTranCardView = findViewById(R.id.pendingTranCardViewId);
        CardView newTranCardView = findViewById(R.id.newTranCardViewId);
        CardView cancelTranCardView = findViewById(R.id.cancelTranCardViewId);

        CardView addEmployeeCardView = findViewById(R.id.addEmployeeCardViewId);
        CardView allEmployeeCardView = findViewById(R.id.allEmployeeCardViewId);

        CardView withdrawRequestCard = findViewById(R.id.withdrawRequestCardId);
        CardView withdrawHistoryCard = findViewById(R.id.withdrawHistoryCardId);

        CardView transferCardView = findViewById(R.id.transferCardViewId);

        profileImage = findViewById(R.id.profileImageId);

        employeeLayout = findViewById(R.id.employeeLayoutId);
        earningLayout = findViewById(R.id.earningLayoutId);
        withdrawLayout = findViewById(R.id.withdrawLayoutId);
        transactionLayout = findViewById(R.id.transactionLayoutId);

        totalBalanceTextView = findViewById(R.id.totalBalanceTextViewId);
        todayBalanceTextView = findViewById(R.id.todayBalanceTextViewId);
        yesterdayBalanceTextView = findViewById(R.id.yesterdayBalanceTextViewId);
        weeklyBalanceTextView = findViewById(R.id.weeklyBalanceTextViewId);
        monthlyBalanceTextView = findViewById(R.id.monthlyBalanceTextViewId);
        lastMonthBalanceTextView = findViewById(R.id.lastMonthBalanceTextViewId);
        last3MonthBalanceTextView = findViewById(R.id.last3MonthBalanceTextViewId);
        yearlyBalanceTextView = findViewById(R.id.yearlyBalanceTextViewId);
        lastYearBalanceTextView = findViewById(R.id.lastYearBalanceTextViewId);

        if (ApiConfig.isConnected(activity)){
            totalBalanceCardView.setOnClickListener(v -> taskClick(Constant.TOTAL_BALANCE));
            employeeCardView.setOnClickListener(v -> taskClick(Constant.EMPLOYEE));
            earningCardView.setOnClickListener(v ->  taskClick(Constant.EARNING));
            withdrawCardView.setOnClickListener(v -> taskClick(Constant.WITHDRAW));
            transactionCardView.setOnClickListener(v ->  taskClick(Constant.TRAN_DETAILS_ITEM));
            addEmployeeCardView.setOnClickListener(v -> taskClickIntent(Constant.EMP, Constant.ABMN_EMP_ADD));
            allEmployeeCardView.setOnClickListener(v -> taskClickIntent(Constant.EMP, Constant.ABMN_EMP_ALL));
            allTranCardView.setOnClickListener(v -> taskClickIntent(Constant.TRAN, Constant.ABMN_ALL_TRAN));
            successTranCardView.setOnClickListener(v -> taskClickIntent(Constant.TRAN, Constant.ABMN_SUCCESS_TRAN));
            pendingTranCardView.setOnClickListener(v -> taskClickIntent(Constant.TRAN, Constant.ABMN_PENDING_TRAN));
            newTranCardView.setOnClickListener(v -> taskClickIntent(Constant.TRAN, Constant.ABMN_NEW_TRAN));
            cancelTranCardView.setOnClickListener(v -> taskClickIntent(Constant.TRAN, Constant.ABMN_CANCELED_TRAN));
            withdrawRequestCard.setOnClickListener(v -> taskClickIntent(Constant.WITH, Constant.ABMN_WITH_REQUEST));
            withdrawHistoryCard.setOnClickListener(v -> taskClickIntent(Constant.WITH, Constant.ABMN_WITH_HISTORY));
            transferCardView.setOnClickListener(v -> requestPermissions(permission, 80));

            getData();
        }

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.navProfileId:
                if (ApiConfig.isConnected(activity)) {
                    startActivity(new Intent(activity, DeaProfileActivity.class));
                }
                break;
            case R.id.navEmployeeId:
                if (ApiConfig.isConnected(activity)) {
                    taskClick(Constant.EMPLOYEE);
                }
                break;
            case R.id.navWithdrawId:
                if (ApiConfig.isConnected(activity)) {
                    taskClick(Constant.WITHDRAW);
                }
                break;
            case R.id.navTransactionId:
                if (ApiConfig.isConnected(activity)) {
                    taskClick(Constant.TRAN_DETAILS_ITEM);
                }
                break;
            case R.id.navEarningId:
                if (ApiConfig.isConnected(activity)) {
                    taskClick(Constant.EARNING);
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

    private void taskClickIntent(String type, String data) {
        switch (type) {
            case "tran":
                intent = new Intent(activity, TransactionsActivity.class);
                break;
            case "emp":
                intent = new Intent(activity, AllEmployeeActivity.class);
                break;
            case "with":
                intent = new Intent(activity, WithdrawActivity.class);
                break;
        }
        intent.putExtra(Constant.ABMN, data);
        startActivity(intent);
        balanceViewClose();
        employeeViewClose();
        earningViewClose();
        withdrawViewClose();
        tranViewClose();
    }

    private void taskClick(String data) {
        switch (data) {
            case Constant.EMPLOYEE:
                employeeView();
                balanceViewClose();
                earningViewClose();
                withdrawViewClose();
                tranViewClose();
                break;
            case Constant.TOTAL_BALANCE:
                balanceTimer();
                balanceView();
                employeeViewClose();
                earningViewClose();
                withdrawViewClose();
                tranViewClose();
                break;
            case Constant.EARNING:
                earningView();
                balanceViewClose();
                employeeViewClose();
                withdrawViewClose();
                tranViewClose();
                break;
            case Constant.WITHDRAW:
                withdrawView();
                balanceViewClose();
                employeeViewClose();
                earningViewClose();
                tranViewClose();
                break;
            case Constant.TRAN_DETAILS_ITEM:
                tranView();
                balanceViewClose();
                employeeViewClose();
                earningViewClose();
                withdrawViewClose();
                break;
        }
    }

    private void balanceTimer() {
        new CountDownTimer(10000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                balanceViewClose();
                employeeViewClose();
                earningViewClose();
            }
        }.start();
    }

    private void balanceView(){
        if (!isBalance){
            isBalance = true;
            totalBalanceTextView.setVisibility(View.VISIBLE);
        }else {
            isBalance = false;
            totalBalanceTextView.setVisibility(View.GONE);
        }
    }
    private void balanceViewClose(){
        isBalance = false;
        totalBalanceTextView.setVisibility(View.GONE);
    }

    private void employeeView(){
        if (!isEmployee){
            isEmployee = true;
            employeeLayout.setVisibility(View.VISIBLE);
        }else {
            isEmployee = false;
            employeeLayout.setVisibility(View.GONE);
        }
    }
    private void employeeViewClose(){
        isEmployee = false;
        employeeLayout.setVisibility(View.GONE);
    }

    private void earningView(){
        if (!isEarning){
            isEarning = true;
            earningLayout.setVisibility(View.VISIBLE);
        }else {
            isEarning = false;
            earningLayout.setVisibility(View.GONE);
        }
    }
    private void earningViewClose(){
        isEarning = false;
        earningLayout.setVisibility(View.GONE);
    }

    private void withdrawView(){
        if (!isWithdraw){
            isWithdraw = true;
            withdrawLayout.setVisibility(View.VISIBLE);
        }else {
            isWithdraw = false;
            withdrawLayout.setVisibility(View.GONE);
        }
    }
    private void withdrawViewClose(){
        isWithdraw = false;
        withdrawLayout.setVisibility(View.GONE);
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

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    private void getOFFData() {

        if (!session.getData(Constant.PROFILE).equals("")) {
            Glide.with(activity).load(session.getData(Constant.PROFILE)).placeholder(getDrawable(R.drawable.dealer_logo)).error(getDrawable(R.drawable.dealer_logo)).into(profileImage);
        }
        if (!session.getData(Constant.TOTAL_BALANCE).equals("null")) {
            totalBalanceTextView.setText(session.getData(Constant.TOTAL_BALANCE));
        }
        if (!session.getData(Constant.TODAY).equals("null")) {
            todayBalanceTextView.setText(session.getData(Constant.TODAY));
        }
        if (!session.getData(Constant.YESTERDAY).equals("null")) {
            yesterdayBalanceTextView.setText(session.getData(Constant.YESTERDAY));
        }
        if (!session.getData(Constant.WEEKLY).equals("null")) {
            weeklyBalanceTextView.setText(session.getData(Constant.WEEKLY));
        }
        if (!session.getData(Constant.MONTHLY).equals("null")) {
            monthlyBalanceTextView.setText(session.getData(Constant.MONTHLY));
        }
        if (!session.getData(Constant.LAST_MONTH).equals("null")) {
            lastMonthBalanceTextView.setText(session.getData(Constant.LAST_MONTH));
        }
        if (!session.getData(Constant.LAST_3_MONTH).equals("null")) {
            last3MonthBalanceTextView.setText(session.getData(Constant.LAST_3_MONTH));
        }
        if (!session.getData(Constant.YEARLY).equals("null")) {
            yearlyBalanceTextView.setText(session.getData(Constant.YEARLY));
        }
        if (!session.getData(Constant.LAST_YEAR).equals("null")) {
            lastYearBalanceTextView.setText(session.getData(Constant.LAST_YEAR));
        }
    }

    private void getData() {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.DATA);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        JSONObject data = jsonObject.getJSONObject(Constant.DATA);

                        String totalBalance = data.getString(Constant.TOTAL_BALANCE);
                        String todayEarning = data.getString(Constant.TODAY);
                        String yesterdayEarning = data.getString(Constant.YESTERDAY);
                        String weeklyEarning = data.getString(Constant.WEEKLY);
                        String monthlyEarning = data.getString(Constant.MONTHLY);
                        String lastMonthlyEarning = data.getString(Constant.LAST_MONTH);
                        String last3MonthlyEarning = data.getString(Constant.LAST_3_MONTH);
                        String yearlyEarning = data.getString(Constant.YEARLY);
                        String lastYearlyEarning = data.getString(Constant.LAST_YEAR);

                        session.saveDBData(totalBalance, todayEarning, yesterdayEarning, weeklyEarning, monthlyEarning, lastMonthlyEarning, last3MonthlyEarning, yearlyEarning, lastYearlyEarning);
                        getOFFData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DASHBOARD_URL", e.getMessage());
                }
            }
            Log.d("DASHBOARD_URL", response);
        }, activity, Constant.DASHBOARD_URL, params, true);
    }
}