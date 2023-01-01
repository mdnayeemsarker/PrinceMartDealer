package com.princemartbd.dealer.activity.Base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Dealer.TransferActivity;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    private Activity activity;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Transfer By RQ Code Scan");
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        activity = this;
        session = new Session(activity);

        toolbar.setNavigationOnClickListener(v -> goBackActivity());
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        mScannerView.resumeCameraPreview(this);
        setContentView(mScannerView);
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("TAG", rawResult.getText()); // Prints scan results
        Log.v("TAG", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if (!rawResult.getText().equals("")){
            mScannerView.stopCamera();
            Intent intent = new Intent(activity, TransferActivity.class);
            intent.putExtra(Constant.ABMN_QR_RESULT, rawResult.getText());
            startActivity(intent);
        }
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

        // Setting OK Button
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            session.goBackWithEmp(activity);
        });

        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

}