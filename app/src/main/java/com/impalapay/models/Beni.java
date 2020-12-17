package com.impalapay.models;

public class Beni {
	private String beniid;
	private String name;
	private String number;
	private String email;
	private String address;
	private String city;
	private String country;
	private String bank_name;
	private String branch_name;
	private String account_number;
	private String account_name;
	private int is_from_phonebook;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBranch_name() {
		return branch_name;
	}

	public void setBranch_name(String branch_name) {
		this.branch_name = branch_name;
	}

	public String getAccount_number() {
		return account_number;
	}

	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getBeniid() {
		return beniid;
	}

	public void setBeniid(String beniid) {
		this.beniid = beniid;
	}

	public int getIsFromPhonebook() {
		return is_from_phonebook;
	}

	public void setIsFromPhonebook(int fromPhonebook) {
		this.is_from_phonebook = fromPhonebook;
	}
}
