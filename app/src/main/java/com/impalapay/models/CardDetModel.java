package com.impalapay.models;



public class CardDetModel  
{
	String cardname,cardno,verify;
	private String extDate;
	private String owner_name;
	private String address;
	private String pincode;
	
	public String getCardname()
	{
		return cardname;
	}
 	
	public void setCardname(String cardname )
	{
		this.cardname=cardname;
	}
	
	
	public String getCardno()
	{
		return cardno;
	}
	public void setCardno(String cardno)
	{
		this.cardno=cardno;
	}
	
	
	public String getVerify()
	{
		return verify;
	}
	 public void setVerify(String verify)
	 {
		 this.verify=verify;
	 }

	public String getExtDate() {
		return extDate;
	}

	public void setExtDate(String extDate) {
		this.extDate = extDate;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
