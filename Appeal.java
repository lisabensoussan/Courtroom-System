package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Appeal implements Serializable{
	private static int count;
	private int appealID;
	private String appealSummary;
	private Date appealDate;
	private Verdict currentVerdict;
	private Verdict newVerdict;
	public Appeal(String appealSummary, Date appealDate, Verdict currentVerdict,Verdict newVerdict) {
		super();
		this.appealID = count++;
		this.appealSummary = appealSummary;
		this.appealDate = appealDate;
		this.currentVerdict = currentVerdict;
		this.newVerdict = newVerdict;
	}
	
	
	
	
	public int getAppealID() {
		return appealID;
	}
	
	public String getAppealSummary() {
		return appealSummary;
	}
	public void setAppealSummary(String appealSummary) {
		this.appealSummary = appealSummary;
	}
	public Date getAppealDate() {
		return appealDate;
	}
	public void setAppealDate(Date appealDate) {
		this.appealDate = appealDate;
	}
	public Verdict getCurrentVerdict() {
		return currentVerdict;
	}
	public void setCurrentVerdict(Verdict currentVerdict) {
		this.currentVerdict = currentVerdict;
	}
	
	
	public Verdict getNewVerdict() {
		return newVerdict;
	}
	public void setNewVerdict(Verdict newVerdict) {
		this.newVerdict = newVerdict;
	}
	@Override
	public String toString() {
		return "Appeal [appealID=" + appealID + ", appealSummary=" + appealSummary + ", appealDate=" + appealDate
				+ ", currentVerdict=" + currentVerdict.getVerdictID() +", newVerdict=" + newVerdict.getVerdictID() +   "]";
	}




	@Override
	public int hashCode() {
		return Objects.hash(appealDate, appealID, appealSummary, currentVerdict, newVerdict);
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Appeal other = (Appeal) obj;
		return Objects.equals(appealDate, other.appealDate) && appealID == other.appealID
				&& Objects.equals(appealSummary, other.appealSummary)
				&& Objects.equals(currentVerdict, other.currentVerdict) && Objects.equals(newVerdict, other.newVerdict);
	}
	

}
