package com.impalapay.models;

/**
 * Created by Sly on 2016-06-08.
 */
public class BeneficiariesModel {

    private String beneficiary_id;
    private String txnCode;
    private String orderid;
    private String username;

    public void setTxnCode(String txn_code) {
        this.txnCode = txn_code;
    }
    public String getOrderid() {
        return orderid;
    }
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }


}
