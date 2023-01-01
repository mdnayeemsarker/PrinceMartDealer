package com.princemartbd.dealer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.Constant;
import com.princemartbd.dealer.helper.Session;
import com.princemartbd.dealer.model.WithdrawModel;

import java.util.ArrayList;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {

    private final Activity activity;
    private final Session session;
    private final ArrayList<WithdrawModel> withdrawModelArrayList;

    public WithdrawAdapter(Activity activity, ArrayList<WithdrawModel> withdrawModelArrayList) {
        this.activity = activity;
        this.withdrawModelArrayList = withdrawModelArrayList;
        session = new Session(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_withdraw, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.id.setText("Id: " + withdrawModelArrayList.get(position).getId());
        holder.type.setText("Type: " + withdrawModelArrayList.get(position).getType());
        holder.message.setText("Message: " + withdrawModelArrayList.get(position).getMessage());
        holder.amount.setText("৳: " + withdrawModelArrayList.get(position).getAmount());
        holder.createdAt.setText("Date: " + withdrawModelArrayList.get(position).getDate_created());
        holder.updatedAt.setText("Update At:: " + withdrawModelArrayList.get(position).getLast_updated());
        switch (withdrawModelArrayList.get(position).getStatus()) {
            case "0":
                holder.status.setText(Constant.NOT_APPROVED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.active_yellow));
                break;
            case "1":
                holder.status.setText(Constant.APPROVED);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.button_green));
//                holder.status.setTextColor(ContextCompat.getColor(activity, R.color.white));
                break;
            case "2":
                holder.status.setText(Constant.CANCEL);
                holder.status.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                holder.status.setTextColor(ContextCompat.getColor(activity, R.color.black));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return withdrawModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView id;
        private final TextView type;
        private final TextView status;
        private final TextView message;
        private final TextView amount;
        private final TextView createdAt;
        private final TextView updatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.idId);
            type = itemView.findViewById(R.id.typeId);
            status = itemView.findViewById(R.id.statusId);
            message = itemView.findViewById(R.id.messageId);
            amount = itemView.findViewById(R.id.amountId);
            createdAt = itemView.findViewById(R.id.createdAtId);
            updatedAt = itemView.findViewById(R.id.updatedAtId);
        }
    }
}
