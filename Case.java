package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import enums.Specialization;
import enums.Status;

public abstract class Case implements Serializable{
	private static int count=0;
	private String code;
	private Accused accused;
	private Date openedDate;
	private Status caseStatus;
	private Specialization caseType;
	private Lawyer lawyer;
	private HashSet<Meeting> meetingsList;
	private HashSet<Document> documentsList;
	private HashSet<Testimony> testimoniesList;
	private Verdict verdict;
		
	public Case(Accused accused, Date openedDate, Status caseStatus, Specialization caseType, Lawyer lawyer, Verdict verdict) {
		super();
		this.code = generateCode();
		count++;
		this.accused = accused;
		this.openedDate = openedDate;
		this.caseStatus = caseStatus;
		this.caseType = caseType;
		this.lawyer = lawyer;
		this.meetingsList = new HashSet<Meeting>();
		this.documentsList = new HashSet<Document>();
		this.testimoniesList = new HashSet<Testimony>();
		this.verdict=verdict;
	}
	
	public String generateCode() {
		if(this instanceof CriminalCase)
			return "c"+count;
		else if(this instanceof FamilyCase)
			return "m"+count;
		return "f"+count;
	}
	
	public Verdict getVerdict() {
		return verdict;
	}

	public void setVerdict(Verdict verdict) {
		this.verdict = verdict;
	}

	public boolean addMeeting(Meeting meeting) {
		if(meeting==null) return false;
		if(meetingsList.contains(meeting)) return false;
		meetingsList.add(meeting);
		return true;
	}
	
	public boolean removeMeeting(Meeting meeting) {
		if(meeting==null) return false;
		if(!meetingsList.contains(meeting)) return false;
		meetingsList.remove(meeting);
		return true;
	}
	
	public boolean addDocument(Document doc) {
		if(doc==null) return false;
		if(documentsList.contains(doc)) return false;
		documentsList.add(doc);
		return true;
	}
	
	public boolean removeDocument(Document doc) {
		if(doc==null) return false;
		if(!documentsList.contains(doc)) return false;
		documentsList.remove(doc);
		return true;
	}
	public boolean addTestimony(Testimony testimony) {
		if(testimony==null) return false;
		if(testimoniesList.contains(testimony)) return false;
		testimoniesList.add(testimony);
		return true;
	}
	
	public boolean removeTestimony(Testimony testimony) {
		if(testimony==null) return false;
		if(!testimoniesList.contains(testimony)) return false;
		testimoniesList.remove(testimony);
		return true;
	}

	public static int getCount() {
		return count;
	}
	public static void setCount(int count) {
		Case.count = count;
	}

	public String getCode() {
		return code;
	}

	public Accused getAccused() {
		return accused;
	}

	public void setAccused(Accused accused) {
		this.accused = accused;
	}

	public Date getOpenedDate() {
		return openedDate;
	}

	public void setOpenedDate(Date openedDate) {
		this.openedDate = openedDate;
	}
	
	public Status getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(Status caseStatus) {
		this.caseStatus = caseStatus;
	}

	public Specialization getCaseType() {
		return caseType;
	}

	public void setCaseType(Specialization caseType) {
		this.caseType = caseType;
	}

	public Lawyer getLawyer() {
		return lawyer;
	}
	public void setLawyer(Lawyer lawyer) {
		this.lawyer = lawyer;
	}
	public HashSet<Meeting> getMeetingsList() {
		return meetingsList;
	}
	public void setMeetingsList(HashSet<Meeting> meetingsList) {
		this.meetingsList = meetingsList;
	}
	public HashSet<Document> getDocumentsList() {
		return documentsList;
	}

	public void setDocumentsList(HashSet<Document> documentsList) {
		this.documentsList = documentsList;
	}

	public HashSet<Testimony> gettestimoniesList() {
		return testimoniesList;
	}

	public void settestimoniesList(HashSet<Testimony> testimoniesList) {
		this.testimoniesList = testimoniesList;
	}

	@Override
	public String toString() {
		return "Case [code=" + code + ", accused=" + accused + ", openedDate=" + openedDate + ", caseStatus="
				+ caseStatus + ", caseType=" + caseType + ", lawyer=" + lawyer.getFirstName() + "]";
	}

	@Override
	public int hashCode() {
	    // Use only immutable and unique fields
	    return Objects.hash(code, caseStatus, caseType);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Case other = (Case) obj;
		return Objects.equals(accused, other.accused) && caseStatus == other.caseStatus && caseType == other.caseType
				&& Objects.equals(code, other.code) && Objects.equals(documentsList, other.documentsList)
				&& Objects.equals(lawyer, other.lawyer) && Objects.equals(meetingsList, other.meetingsList)
				&& Objects.equals(openedDate, other.openedDate)
				&& Objects.equals(testimoniesList, other.testimoniesList) && Objects.equals(verdict, other.verdict);
	}	

	public abstract void describeSpecialProperties();

}
