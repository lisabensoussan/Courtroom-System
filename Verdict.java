package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Verdict implements Serializable{
	private static int count=0;
	private int verdictID;
	private String verdictSummary;
	private Date issusedDate;
	private Judge judge;
	private Case casee;
	private Appeal appeal;
	public Verdict(String verdictSummary, Date issusedDate, Judge judge, Case casee, Appeal appeal) {
		super();
		this.verdictID = count++;
		this.verdictSummary = verdictSummary;
		this.issusedDate = issusedDate;
		this.judge = judge;
		this.casee = casee;
		this.appeal = appeal;
	}
	
	public Verdict(int verdictID, String verdictSummary, Date issusedDate, Judge judge, Case casee) {
		super();
		this.verdictID = count++;
		this.verdictSummary = verdictSummary;
		this.issusedDate = issusedDate;
		this.judge = judge;
		this.casee = casee;
		this.appeal = null;
	}
	public int getVerdictID() {
		return verdictID;
	}
	
	public String getVerdictSummary() {
		return verdictSummary;
	}
	public void setVerdictSummary(String verdictSummary) {
		this.verdictSummary = verdictSummary;
	}
	public Date getIssusedDate() {
		return issusedDate;
	}
	public void setIssusedDate(Date issusedDate) {
		this.issusedDate = issusedDate;
	}
	public Judge getJudge() {
		return judge;
	}
	public void setJudge(Judge judge) {
		this.judge = judge;
	}
	public Case getCasee() {
		return casee;
	}
	public void setCasee(Case casee) {
		this.casee = casee;
	}
	public Appeal getAppeal() {
		return appeal;
	}
	public void setAppeal(Appeal appeal) {
		this.appeal = appeal;
	}
	@Override
	public String toString() {
		return "Verdict [verdictID=" + verdictID +", caseID=" + casee.getCode() + ", verdictSummary=" + verdictSummary + ", issusedDate=" + issusedDate
				+ ", judge=" + judge.getFirstName()+" "+judge.getLastName()  + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(appeal, casee, issusedDate, judge, verdictID, verdictSummary);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Verdict other = (Verdict) obj;
		return Objects.equals(appeal, other.appeal) && Objects.equals(casee, other.casee)
				&& Objects.equals(issusedDate, other.issusedDate) && Objects.equals(judge, other.judge)
				&& verdictID == other.verdictID && Objects.equals(verdictSummary, other.verdictSummary);
	}

}
