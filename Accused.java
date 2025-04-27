package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import control.Court;
import enums.Gender;

public class Accused extends Person implements Serializable,CasesInterface{
	private String job ;
	private HashSet<Case> cases;
	
	public Accused(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
			String email, Gender gender, String job) {
		super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
		this.job = job;
		this.cases = new HashSet<Case>();
	}
	
	public boolean addCase(Case newCase) {
		if( newCase == null) return false;
		if( cases.contains(newCase)) return false;
		cases.add(newCase);
		return true;
	}
	
	public boolean removeCase(Case caseToRemove) {
		if( caseToRemove == null) return false;
		if( !cases.contains(caseToRemove)) return false;
		cases.remove(caseToRemove);
		return true;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public HashSet<Case> getCases() {
		return cases;
	}

	public void setCases(HashSet<Case> cases) {
		this.cases = cases;
	}

	@Override
	public String toString() {
		return "Accused [id =" + getId()
		+ ", FirstName=" + getFirstName() + ", LastName=" + getLastName() + ", BirthDate="
		+ getBirthDate() + ", Address=" + getAddress() + ", PhoneNumber=" + getPhoneNumber()
		+ ", Email=" + getEmail() + ", Gender=" + getGender() +" ,job=" + job +  "]"; 
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(job);
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
		Accused other = (Accused) obj;
		return Objects.equals(job, other.job);
	}

	@Override
	public Case getRequiredCase() {
		Case oldestCase = null;
		Date oldestDate = Court.TODAY;
		for(Case c : cases)
		{
			if(c.getOpenedDate().before(oldestDate)) {
				oldestCase = c;
				oldestDate = c.getOpenedDate();
			}
		}
			
		return oldestCase;
	}

}
