package com.princemartbd.dealer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Dealer.DealerActivity;
import com.princemartbd.dealer.activity.Employee.EmployeeActivity;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.AllTransactionsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllTransactionsAdapter extends RecyclerView.Adapter<AllTransactionsAdapter.ViewHolder> {

    private final Activity activity;
    private final Session session;
    private final ArrayList<AllTransactionsModel> allTransactionsModelArrayList;

    public AllTransactionsAdapter(Activity activity, ArrayList<AllTransactionsModel> allTransactionsModelArrayList) {
        this.activity = activity;
        this.allTransactionsModelArrayList = allTransactionsModelArrayList;
        session = new Session(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_all_transactions, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.id.setText("Id: " + allTransactionsModelArrayList.get(position).getId());
        if (allTransactionsModelArrayList.get(position).getEmployee_name().equals("null")) {
            holder.name.setText("Self");
        } else {
            holder.name.setText("Name: " + allTransactionsModelArrayList.get(position).getEmployee_name());
        }
        holder.message.setText("Message: " + allTransactionsModelArrayList.get(position).getMessage());
        holder.createdAt.setText("Date: " + allTransactionsModelArrayList.get(position).getCreated_id());
        holder.updatedAt.setText("Update: " + allTransactionsModelArrayList.get(position).getUpdated_at());
        holder.trx.setText("Trx.Id: " + allTransactionsModelArrayList.get(position).getTrx());
        holder.amount.setText("৳" + allTransactionsModelArrayList.get(position).getAmount());
        switch (allTransactionsModelArrayList.get(position).getStatus()) {
            case "0":
                holder.status.setText(Constant.ERROR_TRAN);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                break;
            case "1":
                holder.status.setText(Constant.PENDING);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                holder.rootCardView.setOnClickListener(v -> openDialog(position));
                break;
            case "2":
                holder.status.setText(Constant.SUCCESS_TRAN);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_green));
                break;
            case "3":
                holder.status.setText(Constant.CANCEL);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                break;
            case "5":
                holder.status.setText(Constant.REQUESTED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                holder.rootCardView.setOnClickListener(v -> openDialog(position));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void openDialog(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        @SuppressLint("InflateParams") final View customLayout = activity.getLayoutInflater().inflate(R.layout.lyt_transaction_details, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        TextView id = customLayout.findViewById(R.id.idId);
        TextView name = customLayout.findViewById(R.id.nameId);
        TextView messageTV = customLayout.findViewById(R.id.messageId);
        TextView statusTV = customLayout.findViewById(R.id.statusId);
        TextView createdAt = customLayout.findViewById(R.id.createdAtId);
        TextView updatedAt = customLayout.findViewById(R.id.updatedAtId);
        TextView trx = customLayout.findViewById(R.id.trxId);
        TextView amount = customLayout.findViewById(R.id.amountId);

        LinearLayout otpVerificationLayout = customLayout.findViewById(R.id.otpVerificationLayoutId);
        Button btnApproveNow = customLayout.findViewById(R.id.btnApproveNowId);

        RadioButton approveRB = customLayout.findViewById(R.id.approveRB);
        RadioButton cancelRB = customLayout.findViewById(R.id.cancelRB);
        EditText editTextOtp = customLayout.findViewById(R.id.editTextOtp);
        EditText editTextMessage = customLayout.findViewById(R.id.editTextMessage);

        ProgressBar progress_bar = customLayout.findViewById(R.id.progress_bar);

        ImageView ic_close = customLayout.findViewById(R.id.ic_closeId);

        id.setText("Id: " + allTransactionsModelArrayList.get(position).getId());
        if (allTransactionsModelArrayList.get(position).getEmployee_name().equals("null")) {
            name.setText("Self");
        } else {
            name.setText("Name: " + allTransactionsModelArrayList.get(position).getEmployee_name());
        }
        messageTV.setText("Message: " + allTransactionsModelArrayList.get(position).getMessage());
        createdAt.setText("Date: " + allTransactionsModelArrayList.get(position).getCreated_id());
        if (allTransactionsModelArrayList.get(position).getUpdated_at().equals("null")) {
            updatedAt.setText("Update: N/A");
        } else {
            updatedAt.setText("Update: " + allTransactionsModelArrayList.get(position).getUpdated_at());
        }
        trx.setText("Trx.Id: " + allTransactionsModelArrayList.get(position).getTrx());
        amount.setText("৳" + allTransactionsModelArrayList.get(position).getAmount());
        switch (allTransactionsModelArrayList.get(position).getStatus()) {
            case "0":
                statusTV.setText(Constant.ERROR_TRAN);
                statusTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                break;
            case "1":
                statusTV.setText(Constant.PENDING);
                statusTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                btnApproveNow.setVisibility(View.VISIBLE);
                approveRB.setOnClickListener(v1 -> {
                    otpVerificationLayout.setVisibility(View.VISIBLE);
                    btnApproveNow.setText(activity.getString(R.string.approve));
                    btnApproveNow.setVisibility(View.VISIBLE);
                });
                cancelRB.setOnClickListener(v1 -> {
                    otpVerificationLayout.setVisibility(View.GONE);
                    btnApproveNow.setText(activity.getString(R.string.cancel));
                    btnApproveNow.setVisibility(View.VISIBLE);
                });
                btnApproveNow.setOnClickListener(v1 -> {
                    String otp = editTextOtp.getText().toString();
                    String message = editTextMessage.getText().toString();

                    if (approveRB.isChecked()) {
                        dialog.dismiss();
                        if (otp.isEmpty()) {
                            editTextOtp.requestFocus();
                            editTextOtp.setError(activity.getString(R.string.otp_error));
                        }
                        ApproveNow(activity, otp, "2", message, allTransactionsModelArrayList.get(position).getId(), progress_bar);
                    }
                    if (cancelRB.isChecked()) {
                        dialog.dismiss();
                        ApproveNow(activity, otp, "3", message, allTransactionsModelArrayList.get(position).getId(), progress_bar);
                    }
                });
                break;
            case "2":
                statusTV.setText(Constant.SUCCESS_TRAN);
                statusTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_green));
                break;
            case "3":
                statusTV.setText(Constant.CANCEL);
                statusTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                break;
            case "5":
                statusTV.setText(Constant.REQUESTED);
                statusTV.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                approveRB.setOnClickListener(v1 -> {
                    otpVerificationLayout.setVisibility(View.VISIBLE);
                    btnApproveNow.setText(activity.getString(R.string.approve));
                    btnApproveNow.setVisibility(View.VISIBLE);
                });
                cancelRB.setOnClickListener(v1 -> {
                    otpVerificationLayout.setVisibility(View.GONE);
                    btnApproveNow.setText(activity.getString(R.string.cancel));
                    btnApproveNow.setVisibility(View.VISIBLE);
                });
                btnApproveNow.setOnClickListener(v1 -> {
                    String otp = editTextOtp.getText().toString();
                    String message = editTextMessage.getText().toString();
                    if (approveRB.isChecked()) {
                        dialog.dismiss();
                        if (otp.isEmpty()) {
                            editTextOtp.requestFocus();
                            editTextOtp.setError(activity.getString(R.string.otp_error));
                        }
                        ApproveNow(activity, otp, "2", message, allTransactionsModelArrayList.get(position).getId(), progress_bar);
                    }
                    if (cancelRB.isChecked()) {
                        dialog.dismiss();
                        ApproveNow(activity, otp, "3", message, allTransactionsModelArrayList.get(position).getId(), progress_bar);
                    }

                });
                break;
        }
        ic_close.setOnClickListener(v -> dialog.dismiss());
    }

    private void ApproveNow(Activity activity, String otp, String status, String message, String tran_id, ProgressBar progressBar) {

        progressBar.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.UPDATE_TRANSACTION);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.TRANSACTION_ID, tran_id);
        params.put(Constant.STATUS, status);
        params.put(Constant.OTP, otp);
        params.put(Constant.MESSAGE, message);

//        Log.d(Constant.ALL_TRANSACTIONS, String.valueOf(params));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    if (session.getData(Constant.DEALER_TYPE).equals(Constant.DEALER)) {
                        activity.startActivity(new Intent(activity, DealerActivity.class));
                    } else {
                        activity.startActivity(new Intent(activity, EmployeeActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, activity.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
                Log.d(Constant.ALL_TRANSACTIONS, response);
                progressBar.setVisibility(View.GONE);
            }
        }, activity, Constant.ALL_TRANSACTIONS, params, true);
    }

    @Override
    public int getItemCount() {
        return allTransactionsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView id;
        private final TextView name;
        private final TextView message;
        private final TextView status;
        private final TextView createdAt;
        private final TextView updatedAt;
        private final TextView trx;
        private final TextView amount;
        private final CardView rootCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.idId);
            name = itemView.findViewById(R.id.nameId);
            message = itemView.findViewById(R.id.messageId);
            status = itemView.findViewById(R.id.statusId);
            createdAt = itemView.findViewById(R.id.createdAtId);
            updatedAt = itemView.findViewById(R.id.updatedAtId);
            trx = itemView.findViewById(R.id.trxId);
            amount = itemView.findViewById(R.id.amountId);
            rootCardView = itemView.findViewById(R.id.rootCardViewId);
        }
    }
}
