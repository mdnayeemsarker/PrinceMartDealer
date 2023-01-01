package com.princemartbd.dealer.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Base.LoginActivity;
import com.princemartbd.dealer.activity.Dealer.DealerActivity;
import com.princemartbd.dealer.activity.Employee.EmployeeActivity;

@SuppressWarnings("UnusedReturnValue")
public class Session {
    public static final String PREFER_NAME = "abmnPrinceMath";
    final int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _activity;

    public Session(Context activity) {
        try {
            this._activity = activity;
            pref = _activity.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Boolean IS_LOGGED_IN(Activity activity) {
        if (!pref.getBoolean(Constant.IS_USER_LOGIN, false)) {
            Intent i = new Intent(activity, LoginActivity.class);
            activity.startActivity(i);
            activity.finish();
        }
        return pref.getBoolean(Constant.IS_USER_LOGIN, false);
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setBoolean(String id, boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public boolean getBoolean(String id) {
        return pref.getBoolean(id, false);
    }

    public boolean getIsLog(){
        return pref.getBoolean(Constant.IS_USER_LOGIN, false);
    }

    public void createUserLoginSession(String name, String email, String mobile, String password) {
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.MOBILE, mobile);
        editor.putString(Constant.PASSWORD, password);
        editor.commit();
    }

    public void setUserData(String dealer_type, String user_id, String name, String email, String profile, String country_code, String mobile, String password, String balance, String fcm_id, String status, String created_at) {
        editor.putBoolean(Constant.IS_USER_LOGIN, true);
        editor.putString(Constant.DEALER_TYPE, dealer_type);
        editor.putString(Constant.USER_ID, user_id);
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.PROFILE, profile);
        editor.putString(Constant.COUNTRY_CODE, country_code);
        editor.putString(Constant.MOBILE, mobile);
        editor.putString(Constant.PASSWORD, password);
        editor.putString(Constant.BALANCE, balance);
        editor.putString(Constant.FCM_ID, fcm_id);
        editor.putString(Constant.STATUS, status);
        editor.putString(Constant.CREATED_AT, created_at);
        editor.commit();
    }

    public void logoutUser(Activity activity) {
        String dbMobile = getData(Constant.MOBILE);
        String dbPass = getData(Constant.PASSWORD);
        editor.clear();
        editor.commit();

        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();

        editor.putString(Constant.MOBILE, dbMobile);
        editor.putString(Constant.PASSWORD, dbPass);
        editor.commit();
    }

    public void logoutUserConfirmation(final Activity activity) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_activity);
        alertDialog.setTitle(R.string.logout);
        alertDialog.setMessage(R.string.logout_msg);
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        alertDialog.setPositiveButton(R.string.yes, (dialog, which) -> logoutUser(activity));

        alertDialog.setNegativeButton(R.string.no, (dialog, which) -> alertDialog1.dismiss());
        alertDialog.show();

    }

    public void saveDBData(String totalBalance, String today, String yesterday, String weekly, String monthly, String lastMonthly, String last3Monthly, String yearly, String lastYearly) {
        editor.putString(Constant.TOTAL_BALANCE, totalBalance);
        editor.putString(Constant.TODAY, today);
        editor.putString(Constant.YESTERDAY, yesterday);
        editor.putString(Constant.WEEKLY, weekly);
        editor.putString(Constant.MONTHLY, monthly);
        editor.putString(Constant.LAST_MONTH, lastMonthly);
        editor.putString(Constant.LAST_3_MONTH, last3Monthly);
        editor.putString(Constant.YEARLY, yearly);
        editor.putString(Constant.LAST_YEAR, lastYearly);
        editor.commit();
    }

    public void saveTranData(String seller_id, String dealer_id, String employee_id, String trx, String amount, String message, String status, String updated_at, String accepted_at, String created_id) {
        editor.putString(Constant.SELLER_ID, seller_id);
        editor.putString(Constant.DEALER_ID, dealer_id);
        editor.putString(Constant.EMPLOYEE_ID, employee_id);
        editor.putString(Constant.TRX, trx);
        editor.putString(Constant.AMOUNT, amount);
        editor.putString(Constant.MESSAGE, message);
        editor.putString(Constant.STATUS, status);
        editor.putString(Constant.UPDATED_AT, updated_at);
        editor.putString(Constant.ACCEPTED_AT, accepted_at);
        editor.putString(Constant.CREATED_AT, created_id);
        editor.commit();
    }

    public void goBack(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    public void goBackDealer(Activity activity) {
        Intent intent = new Intent(activity, DealerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    public void goBackWithEmp(Activity activity) {
        Intent intent = new Intent();
        if (getData(Constant.DEALER_TYPE).equals(Constant.DEALER)) {
            intent = new Intent(activity, DealerActivity.class);
        } else  if (getData(Constant.DEALER_TYPE).equals(Constant.EMPLOYEE)) {
            intent = new Intent(activity, EmployeeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}