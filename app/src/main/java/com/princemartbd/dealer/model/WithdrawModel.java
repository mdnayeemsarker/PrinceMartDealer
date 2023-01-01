package com.princemartbd.dealer.model;

public class WithdrawModel {

    private final String id;
    private final String type;
    private final String type_id;
    private final String amount;
    private final String message;
    private final String status;
    private final String last_updated;
    private final String date_created;


    public WithdrawModel(String id, String type, String type_id, String amount, String message, String status, String last_updated, String date_created) {
        this.id = id;
        this.type = type;
        this.type_id = type_id;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.last_updated = last_updated;
        this.date_created = date_created;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getType_id() {
        return type_id;
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

    public String getLast_updated() {
        return last_updated;
    }

    public String getDate_created() {
        return date_created;
    }
}
