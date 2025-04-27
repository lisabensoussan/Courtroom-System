package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import enums.Specialization;
import enums.Status;
import utils.MyFileLogWriter;

public class FamilyCase extends Case implements Serializable, DocumentsInterface {
	private Person victim;
	private String relationType;
	public FamilyCase(Accused accused, Date openedDate, Status caseStatus, Specialization caseType, Lawyer lawyer,
			Verdict verdict, Person victim, String relationType) {
		super(accused, openedDate, caseStatus, caseType, lawyer, verdict);
		this.victim = victim;
		this.relationType = relationType;
	}
	public Person getVictim() {
		return victim;
	}
	public void setVictim(Person victim) {
		this.victim = victim;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(relationType, victim);
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
		FamilyCase other = (FamilyCase) obj;
		return Objects.equals(relationType, other.relationType) && Objects.equals(victim, other.victim);
	}
	@Override
	public String toString() {
		if(getVerdict()!=null)
			return "FamilyCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
				+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", victim="  
				+" relationType "+relationType+ " , Verdict=" + getVerdict() +  "]";
		return "FamilyCase [Code=" + getCode() + ", Accused=" + getAccused() + ", OpenedDate=" + getOpenedDate()
		+ ", CaseType=" + getCaseType() + ", Lawyer=" + getLawyer().getFirstName()+" "+getLawyer().getLastName() +", victim=" + victim 
		+" relationType "+relationType+  "]";

	}
	@Override
	public Document getRequiredDocument() {
		Object[] docs = getDocumentsList().toArray();
		Document toRet = (Document) docs[0];
		Date newestDate = toRet.getIssusedDate();
		for(Document d : getDocumentsList()) {
			if(d.getIssusedDate().after(newestDate)) {
				newestDate = d.getIssusedDate();
				toRet = d;
			}
		}
		return toRet;

	}
	@Override
	public void describeSpecialProperties() {
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");
		MyFileLogWriter.println("-------------Special properties for the family case  "+getCode()+" are -------------");
		MyFileLogWriter.println("1)The victim is : "+victim);
		MyFileLogWriter.println("2)The relation Type : "+relationType);
		MyFileLogWriter.println("--------------------------------------------------------------------------------------");
	}
}
