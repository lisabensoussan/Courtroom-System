package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import enums.Gender;
import enums.Specialization;
import enums.Status;

public class Judge extends Lawyer implements Serializable{
	private int experienceYear ;
	private HashSet<Case> casesPresided;
	
	public boolean closeCase(Case caseToClose) {
		if(caseToClose == null) return false;
		if(!super.getCasesHandled().contains(caseToClose)) return false;
		super.getCasesHandled().remove(caseToClose);
		caseToClose.setCaseStatus(Status.finished);
		casesPresided.add(caseToClose);
		return true;
	}

	public Judge(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
			String email, Gender gender,  Specialization specialization, int licenseNumber,
			double salary,int experienceYear) {
		super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender, specialization,
				licenseNumber, salary);
		this.experienceYear = experienceYear;
		this.casesPresided = new HashSet<Case>();
	}

	public int getExperienceYear() {
		return experienceYear;
	}

	public void setExperienceYear(int experienceYear) {
		this.experienceYear = experienceYear;
	}

	public HashSet<Case> getCasesPresided() {
		return casesPresided;
	}

	public void setCasesPresided(HashSet<Case> casesPresided) {
		this.casesPresided = casesPresided;
	}

	@Override
	public String toString() {
		return "Judge [id =" + getId()
		+ ", FirstName=" + getFirstName() + ", LastName=" + getLastName() + ", BirthDate="
		+ getBirthDate() + ", Address=" + getAddress() + ", PhoneNumber=" + getPhoneNumber()
		+ ", Email=" + getEmail() + ", Gender=" + getGender() +"licenseNumber= " + getLicenseNumber() + ", salary=" + getSalary() +
		"ExperienceYear="+experienceYear+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(experienceYear);
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
		Judge other = (Judge) obj;
		return Objects.equals(casesPresided, other.casesPresided) && experienceYear == other.experienceYear;
	}
}
