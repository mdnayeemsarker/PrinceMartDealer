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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.princemartbd.dealer.R;
import com.princemartbd.dealer.activity.Dealer.DealerActivity;
import com.princemartbd.dealer.helper.ApiConfig;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.helper.Utils;
import com.princemartbd.dealer.model.AllEmployeeModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllEmployeeAdapter extends RecyclerView.Adapter<AllEmployeeAdapter.ViewHolder> {

    private final Activity activity;
    private final Session session;
    private final ArrayList<AllEmployeeModel> allEmployeeModelArrayList;

    public AllEmployeeAdapter(Activity activity, ArrayList<AllEmployeeModel> allEmployeeModelArrayList) {
        this.activity = activity;
        this.allEmployeeModelArrayList = allEmployeeModelArrayList;
        session = new Session(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_all_employee, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(allEmployeeModelArrayList.get(position).getName());
        String email = allEmployeeModelArrayList.get(position).getEmail();

        holder.email.setText(email.replaceAll("(^[^@]{2}|(?!^)\\G)[^@]", "$1*"));
        String mobile = allEmployeeModelArrayList.get(position).getMobile();
        holder.phone.setText(mobile.replaceAll("\\b(\\d{3})(\\d{5})(\\d{3})", "$1xxxxx$3"));
        holder.dateAndTime.setText(allEmployeeModelArrayList.get(position).getDate_create());
//        holder.address.setText(allEmployeeModelArrayList.get(position).getAddress());
        holder.rootCardView.setOnClickListener(v ->  selectDialog(activity, position));

        String url = "https://beta.princemartbd.com/upload/dealer/" + allEmployeeModelArrayList.get(position).getProfile();

        Glide.with(activity).load(url).placeholder(R.drawable.dealer_logo).error(R.drawable.dealer_logo).into(holder.profileImage);

        if (allEmployeeModelArrayList.get(position).getCountry_code().equals("88")) {
            holder.countryCode.setText("BD");
        }
        holder.balance.setText("৳" + allEmployeeModelArrayList.get(position).getBalance());
        switch (allEmployeeModelArrayList.get(position).getStatus()) {
            case "0":
                holder.status.setText(Constant.NOT_APPROVED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                break;
            case "1":
                holder.status.setText(Constant.APPROVED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.primary_green));
                break;
            case "2":
                holder.status.setText(Constant.DEACTIVATE);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                holder.status.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
            case "3":
            case "4":
            case "5":
            case "6":
                holder.status.setText(Constant.CREATED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.activate_text_color));
                break;
            case "7":
                holder.status.setText(Constant.BLOCK);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                break;
        }
    }

    private void selectDialog(Activity activity, int position) {
        final CharSequence[] items = {activity.getString(R.string.manage_employee), activity.getString(R.string.change_password), activity.getString(R.string.delete_employee)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0:
                    openDialog(activity, position);
                    break;
                case 1:
                    changePasswordDialog(activity, position);
                    break;
                case 2:
                    deleteDialog(activity, position);
                    break;
            }
        });
        builder.show();
    }

    private void deleteDialog(Activity activity, int position) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.DELETE_EMPLOYEE);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.EMPLOYEE_ID, allEmployeeModelArrayList.get(position).getId());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        activity.startActivity(new Intent(activity, DealerActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, activity.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                }
                Log.d(Constant.ADD_EMPLOYEE_URL, response);
            }
        }, activity, Constant.ADD_EMPLOYEE_URL, params, true);
    }

    private void changePasswordDialog(Activity activity, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        @SuppressLint("InflateParams") final View customLayout = activity.getLayoutInflater().inflate(R.layout.lyt_change_password, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        EditText imgLoginPassword = customLayout.findViewById(R.id.imgLoginPassword);
        EditText imgLoginConfirmPassword = customLayout.findViewById(R.id.imgLoginConfirmPassword);

        imgLoginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);
        imgLoginConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pass, 0, R.drawable.ic_show, 0);

        Utils.setHideShowPassword(imgLoginPassword);
        Utils.setHideShowPassword(imgLoginConfirmPassword);

        Button btnChange = customLayout.findViewById(R.id.btnChange);
        Button btnCancel = customLayout.findViewById(R.id.btnCancel);

        btnChange.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            String password = imgLoginPassword.getText().toString();
            String conPassword = imgLoginConfirmPassword.getText().toString();

            if (password.isEmpty()){
                imgLoginPassword.requestFocus();
                imgLoginPassword.setError(activity.getString(R.string.password_error));
            }else if (conPassword.isEmpty()){
                imgLoginConfirmPassword.requestFocus();
                imgLoginConfirmPassword.setError(activity.getString(R.string.password_error));
            }else if (!password.equals(conPassword)){
                Toast.makeText(activity, "Password Does not match.!", Toast.LENGTH_SHORT).show();
            }else {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.PAGE, Constant.CHANGE_PASSWORD);
                params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
                params.put(Constant.EMPLOYEE_ID, allEmployeeModelArrayList.get(position).getId());
                params.put(Constant.PASSWORD, conPassword);
                ApiConfig.RequestToVolley((result, response) -> {
                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                activity.startActivity(new Intent(activity, DealerActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, activity.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        Log.d(Constant.ADD_EMPLOYEE_URL, response);
                    }
                }, activity, Constant.ADD_EMPLOYEE_URL, params, true);
            }

        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            ApiConfig.hideKeyboard(activity, v);
        });
    }

    private void openDialog(Activity activity, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        @SuppressLint("InflateParams") final View customLayout = activity.getLayoutInflater().inflate(R.layout.lyt_manage_employee, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        EditText edtName = customLayout.findViewById(R.id.edtName);
        EditText edtEmail = customLayout.findViewById(R.id.edtEmail);
        EditText edtLoginMobile = customLayout.findViewById(R.id.edtLoginMobile);

        edtName.setText(allEmployeeModelArrayList.get(position).getName());
        edtEmail.setText(allEmployeeModelArrayList.get(position).getEmail());
        edtLoginMobile.setText(allEmployeeModelArrayList.get(position).getMobile());

        RadioButton approveRB = customLayout.findViewById(R.id.approveRB);
        RadioButton deactivateRB = customLayout.findViewById(R.id.deactivateRB);
        RadioButton blockRB = customLayout.findViewById(R.id.blockRB);
        Button btnChange = customLayout.findViewById(R.id.btnChange);
        Button btnCancel = customLayout.findViewById(R.id.btnCancel);

        btnChange.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            String status = "";
            if (approveRB.isChecked()) status = "1";
            if (deactivateRB.isChecked()) status = "2";
            if (blockRB.isChecked()) status = "7";

            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            String mobile = edtLoginMobile.getText().toString();

            if (name.equals("")){
                name = allEmployeeModelArrayList.get(position).getName();
            }
            if (email.equals("")){
                email = allEmployeeModelArrayList.get(position).getEmail();
            }
            if (mobile.equals("")){
                mobile = allEmployeeModelArrayList.get(position).getMobile();
            }
            if (status.equals("")) {
                Toast.makeText(activity, "Please Select Any One Status, Thank you", Toast.LENGTH_SHORT).show();
            } else {
                updateEmployee(name, email, mobile, status, allEmployeeModelArrayList.get(position).getId(), dialog);
//                Toast.makeText(activity, name + " " + email + " " + mobile + " " + allEmployeeModelArrayList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> {
            ApiConfig.hideKeyboard(activity, v);
            dialog.dismiss();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateEmployee(String name, String email, String mobile, String status, String employee_id, AlertDialog dialog) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PAGE, Constant.UPDATE_EMPLOYEE);
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.NAME, name);
        params.put(Constant.EMAIL, email);
        params.put(Constant.MOBILE, mobile);
        params.put(Constant.STATUS, status);
        params.put(Constant.EMPLOYEE_ID, employee_id);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, DealerActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, activity.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                Log.d(Constant.ADD_EMPLOYEE_URL, response);
            }
        }, activity, Constant.ADD_EMPLOYEE_URL, params, true);
    }

    @Override
    public int getItemCount() {
        return allEmployeeModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView email;
        private final TextView phone;
        private final TextView countryCode;
        private final TextView dateAndTime;
//        private final TextView address;
        private final TextView balance;
        private final TextView status;
        private final ImageView profileImage;
        private final CardView rootCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameId);
            email = itemView.findViewById(R.id.emailId);
            phone = itemView.findViewById(R.id.phoneId);
//            address = itemView.findViewById(R.id.addressId);
            countryCode = itemView.findViewById(R.id.countryCodeId);
            dateAndTime = itemView.findViewById(R.id.dateAndTimeId);
            balance = itemView.findViewById(R.id.balanceId);
            status = itemView.findViewById(R.id.statusId);
            profileImage = itemView.findViewById(R.id.profileImageId);
            rootCardView = itemView.findViewById(R.id.rootCardViewId);
        }
    }
}
