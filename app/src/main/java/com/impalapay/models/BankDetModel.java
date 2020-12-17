package com.impalapay.models;

public class BankDetModel  {
	int client_bank_id;
	String bankname, accountno;
	private String country;
	private String swift_code;
	private String aba_number;
	private String address;
	private String pincode;

	
	public BankDetModel(String bankname, String accountno) {
		this.bankname = bankname;
		this.accountno = accountno;
	}
	public BankDetModel(){
		
	}

	public int getclientbankid() {
		return client_bank_id;
	}

	public void setclientbankid(int id) {
		this.client_bank_id = id;
	}

	public String getbankname() {
		return bankname;
	}

	public void setbankname(String bankname) {
		this.bankname = bankname;
	}

	public String getaccountno() {
		return accountno;
	}

	public void setaccountno(String accountno) {
		this.accountno = accountno;
	}

	public String getabanumber() {
		return aba_number;
	}

	public void setabanumber(String abanumber) {
		this.aba_number = abanumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSwift_code() {
		return swift_code;
	}

	public void setSwift_code(String swift_code) {
		this.swift_code = swift_code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

}
