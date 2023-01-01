package com.princemartbd.dealer.activity.Dealer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Base.SetAddressActivity;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeaProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private ImageView editNameImageView;
    private ImageView closeNameImageView;

    private TextView nameTextView;
    private TextView emailITextView;
    private TextView mobileTextView;
    private TextView balanceTextView;
    private TextView statusTextView;
    private TextView createdDateTextView;

    private EditText edtNameEditText;
    private LinearLayout nameLayout;

    private Uri imageUri;
    private String currentPhotoPath;
    private String filePath = null;;

    private Session session;
    private Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dea_profile);

        activity = this;
        session = new Session(activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        if (getSupportActionBar() != null) {
            if (session.getData(Constant.DEALER_TYPE).equals(Constant.DEALER)) {
                getSupportActionBar().setTitle("Dealer Profile");
            } else  if (session.getData(Constant.DEALER_TYPE).equals(Constant.EMPLOYEE)) {
                getSupportActionBar().setTitle("Employee Profile");
            }
        }

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);


        toolbar.setNavigationOnClickListener(v -> session.goBackWithEmp(activity));

        TextView type = findViewById(R.id.typeId);

        profileImage = findViewById(R.id.profileImageId);
        editNameImageView = findViewById(R.id.editNameImageViewId);
        ImageView editAddressImageView = findViewById(R.id.editAddressImageViewId);
        closeNameImageView = findViewById(R.id.closeNameImageViewId);

        nameTextView = findViewById(R.id.nameTextViewId);
        emailITextView = findViewById(R.id.emailITextViewId);
        mobileTextView = findViewById(R.id.mobileTextViewId);
        balanceTextView = findViewById(R.id.balanceTextViewId);
        statusTextView = findViewById(R.id.statusTextViewId);
        createdDateTextView = findViewById(R.id.createdDateTextViewId);

        edtNameEditText = findViewById(R.id.edtNameEditTextId);
        Button nameSaveBtn = findViewById(R.id.nameSaveBtnId);
        nameLayout = findViewById(R.id.nameLayoutId);

        if (session.getData(Constant.DEALER_TYPE).equals(Constant.DEALER)){
            type.setText("DEALER");
        }else if(session.getData(Constant.DEALER_TYPE).equals(Constant.EMPLOYEE)){
            type.setText("EMPLOYEE");
        }

        if (ApiConfig.isConnected(activity)){

        editNameImageView.setOnClickListener(v -> {
            nameTextView.setVisibility(View.GONE);
            nameLayout.setVisibility(View.VISIBLE);
            editNameImageView.setVisibility(View.GONE);
            closeNameImageView.setVisibility(View.VISIBLE);
        });
        closeNameImageView.setOnClickListener(this::closeNameVisibility);
        editAddressImageView.setOnClickListener(v -> startActivity(new Intent(activity, SetAddressActivity.class)));
        nameSaveBtn.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            String editName = edtNameEditText.getText().toString();
            if (editName.equals("")){
                edtNameEditText.requestFocus();
                edtNameEditText.setError(getString(R.string.name_error));
            }else {
                saveEditName(editName, v);
            }
        });

        getSaveData();
        }
    }

    @Override
    public void onBackPressed() {
        session.goBackWithEmp(activity);
    }

    private void closeNameVisibility(View v) {
        ApiConfig.hideKeyboard(activity, v);
        nameLayout.setVisibility(View.GONE);
        nameTextView.setVisibility(View.VISIBLE);
        closeNameImageView.setVisibility(View.GONE);
        editNameImageView.setVisibility(View.VISIBLE);
    }

    private void saveEditName(String editName, View v) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.UPDATE_NAME);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.NAME, editName);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean(Constant.ERROR)) {
                        Toast.makeText(activity, object.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        session.setData(Constant.NAME, editName);
                        getSaveData();
                        closeNameVisibility(v);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Please try again latter", Toast.LENGTH_SHORT).show();
                }
                Log.d(Constant.UPDATE_NAME, response);
            }
        }, activity, Constant.AUTH_LOGIN_URL, params, true);
    }

    @SuppressLint("SetTextI18n")
    private void getSaveData() {
        String dealer_type = session.getData(Constant.DEALER_TYPE);
        String user_id = session.getData(Constant.USER_ID);
        String name = session.getData(Constant.NAME);
        String email = session.getData(Constant.EMAIL);
        String profile = session.getData(Constant.PROFILE);
        String country_code = session.getData(Constant.COUNTRY_CODE);
        String mobile = session.getData(Constant.MOBILE);
        String password = session.getData(Constant.PASSWORD);
        String balance = session.getData(Constant.BALANCE);
        String status = session.getData(Constant.STATUS);
        String created_at = session.getData(Constant.CREATED_AT);

        if (!name.equals("")){
            nameTextView.setText(name);
        }
        if (!email.equals("")){
            emailITextView.setText(email);
        }
        if (!profile.equals("")){
            Glide
                    .with(activity)
                    .load(profile)
                    .into(profileImage);
        }
        if (!mobile.equals("") && !country_code.equals("")){
            mobileTextView.setText("+88 "+ mobile);
        }
        if (!balance.equals("")){
            balanceTextView.setText(balance);
        }
        switch (status) {
            case "0":
                statusTextView.setText(Constant.NOT_APPROVED);
                statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                break;
            case "1":
                statusTextView.setText(Constant.APPROVED);
                statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_green));
                break;
            case "2":
                statusTextView.setText(Constant.CANCEL);
                statusTextView.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                break;
        }
        if (!created_at.equals("")){
            createdDateTextView.setText("Since Connect: " + created_at);
        }
    }
}