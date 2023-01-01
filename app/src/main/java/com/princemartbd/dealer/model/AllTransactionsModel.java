package com.princemartbd.dealer.model;

public class AllTransactionsModel {

    String id;
    String seller_id;
    String dealer_id;
    String employee_id;
    String trx;
    String amount;
    String message;
    String status;
    String updated_at;
    String accepted_at;
    String created_id;
    String seller_name;
    String employee_name;

    public AllTransactionsModel(String id, String seller_id, String dealer_id, String employee_id, String trx, String amount, String message, String status, String updated_at, String accepted_at, String created_id, String seller_name, String employee_name) {
        this.id = id;
        this.seller_id = seller_id;
        this.dealer_id = dealer_id;
        this.employee_id = employee_id;
        this.trx = trx;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.updated_at = updated_at;
        this.accepted_at = accepted_at;
        this.created_id = created_id;
        this.seller_name = seller_name;
        this.employee_name = employee_name;
    }

    public String getId() {
        return id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getTrx() {
        return trx;
    }

    public String getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getAccepted_at() {
        return accepted_at;
    }

    public String getCreated_id() {
        return created_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getEmployee_name() {
        return employee_name;
    }
}
