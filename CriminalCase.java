package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import enums.Specialization;
import enums.Status;
import utils.MyFileLogWriter;

public class CriminalCase extends Case implements Serializable, DocumentsInterface{
	private Person victim;
	private String crimeScene;
	private String crimeTool;
	public CriminalCase(Accused accused, Date openedDate, Status caseStatus, Specialization caseType, Lawyer lawyer,
			Verdict verdict, Person victim, String crimeScene, String crimeTool) {
		super(accused, openedDate, caseStatus, caseType, lawyer, verdict);
		this.victim = victim;
		this.crimeScene = crimeScene;
		this.crimeTool = crimeTool;
	}
	public Person getVictim() {
		return victim;
	}
	public void setVictim(Person victim) {
		this.victim = victim;
	}
	public String getCrimeScene() {
		return crimeScene;
	}
	public void setCrimeScene(String crimeScene) {
		this.crimeScene = crimeScene;
	}
	public String getCrimeTool() {
		return crimeTool;
	}
	public void setCrimeTool(String crimeTool) {
		this.crimeTool = crimeTool;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(crimeScene, crimeTool, victim);
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
		CriminalCase other = (CriminalCase) obj;
		return Objects.equals(crimeScene, other.crimeScene) && Objects.equals(crimeTool, other.crimeTool)
				&& Objects.equals(victim, other.victim);
	}
	@Override
	public String toString() {
		if(getVerdict()!=null)
			return "CriminalCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
				+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", victim=" + victim + ", crimeScene=" + crimeScene 
				+" crimeTool "+crimeTool+ " , Verdict=" + getVerdict() +  "]";
		return "CriminalCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
		+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", victim=" + victim + ", crimeScene=" + crimeScene 
		+" crimeTool "+crimeTool+  "]";

	}
	
	@Override
	public void describeSpecialProperties() {
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");
		MyFileLogWriter.println("-------------Special properties for the criminal case "+getCode()+" are -------------");
		MyFileLogWriter.println("1)The victim is : "+victim);
		MyFileLogWriter.println("2)The crime scene : "+crimeScene);
		MyFileLogWriter.println("3)The crime tool : "+crimeTool);
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");


	}
	@Override
	public Document getRequiredDocument() {
		Document toRet = null;
		String content = "";
		for(Document d : getDocumentsList()) {
			if(d.getContent().length()>content.length()) {
				toRet = d;
				content = d.getContent();
			}
		}
		return toRet;
	}
	

}
