package com.princemartbd.dealer.activity.Base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentHostCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    private Activity activity;
    private Session session;
    Button btnPrint;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        setHasOptionsMenu();
        activity = this;
        session = new Session(activity);
        type = getIntent().getStringExtra("type");

        prgLoading = findViewById(R.id.prgLoading);
        btnPrint = findViewById(R.id.btnPrint);
        mWebView = findViewById(R.id.webView1);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }
        });

        try {
            switch (type) {
                case "Privacy Policy":
                    GetContent(Constant.GET_PRIVACY, "privacy");
                    break;
                case "Terms & Conditions":
                    GetContent(Constant.GET_TERMS, "terms");
                    break;
                case "Contact Us":
                    GetContent(Constant.GET_CONTACT, "contact");
                    break;
                case "About Us":
                    GetContent(Constant.GET_ABOUT_US, "about");
                    break;
            }
            activity.invalidateOptionsMenu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    boolean mHasMenu;
    boolean mAdded;
    boolean mHidden;
    FragmentHostCallback<?> mHost;

    private void setHasOptionsMenu() {
        if (!mHasMenu) {
            mHasMenu = true;
            if (isAdded() && !isHidden()) {
                mHost.onSupportInvalidateOptionsMenu();
            }
        }
    }
    final public boolean isAdded() {
        return mHost != null && mAdded;
    }
    final public boolean isHidden() {
        return mHidden;
    }

    public void GetContent(final String type, final String key) {
        prgLoading.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(type, Constant.GetVal);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean(Constant.ERROR)) {
                        String privacyStr = obj.getString(key);
                        mWebView.setVerticalScrollBarEnabled(true);
                        mWebView.loadDataWithBaseURL("", privacyStr, "text/html", "UTF-8", "");

                        prgLoading.setVisibility(View.GONE);
                    } else {
                        prgLoading.setVisibility(View.GONE);
                        Toast.makeText(activity, obj.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();
                    }
                    prgLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    prgLoading.setVisibility(View.GONE);
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            session.goBackWithEmp(activity);
            super.onBackPressed();
        }

    }
}