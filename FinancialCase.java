package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import enums.Specialization;
import enums.Status;
import utils.MyFileLogWriter;

public class FinancialCase extends Case implements Serializable{
	private double lossesAmount;
	private String damagedItem;
	public FinancialCase(Accused accused, Date openedDate, Status caseStatus, Specialization caseType, Lawyer lawyer,
			Verdict verdict, double lossesAmount,String damagedItem) {
		super(accused, openedDate, caseStatus, caseType, lawyer, verdict);
		this.lossesAmount = lossesAmount;
		this.setDamagedItem(damagedItem);
	}
	public double getLossesAmount() {
		return lossesAmount;
	}
	public void setLossesAmount(double lossesAmount) {
		this.lossesAmount = lossesAmount;
	}
	
	@Override
	public String toString() {
		if(getVerdict()!=null)
			return "FinancialCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
				+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", lossesAmount=" + lossesAmount + ", Verdict=" + getVerdict() +  "]";
		
		return "FinancialCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
		+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", lossesAmount=" + lossesAmount + "]";

	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(damagedItem, lossesAmount);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FinancialCase other = (FinancialCase) obj;
		return Objects.equals(damagedItem, other.damagedItem)
				&& Double.doubleToLongBits(lossesAmount) == Double.doubleToLongBits(other.lossesAmount);
	}
	@Override
	public void describeSpecialProperties() {
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");
		MyFileLogWriter.println("-------------Special properties for the financial case  "+getCode()+" are -------------");
		MyFileLogWriter.println("1)Damaged item : "+damagedItem);
		MyFileLogWriter.println("2)Losses amount : "+lossesAmount);
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");

	}
	public String getDamagedItem() {
		return damagedItem;
	}
	public void setDamagedItem(String damagedItem) {
		this.damagedItem = damagedItem;
	}
}
