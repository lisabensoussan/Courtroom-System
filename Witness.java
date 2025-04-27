package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import enums.Gender;

public class Witness extends Person implements Serializable{
	private HashSet<Testimony> testimoniesList;

	public Witness(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
			String email, Gender gender) {
		super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
		this.testimoniesList = new HashSet<Testimony>();
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

	public HashSet<Testimony> getTestimoniesList() {
		return testimoniesList;
	}

	public void setTestimoniesList(HashSet<Testimony> testimoniesList) {
		this.testimoniesList = testimoniesList;
	}

	@Override
	public String toString() {
		return "Witness [testimoniesList=" + testimoniesList + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result ;
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
		Witness other = (Witness) obj;
		return Objects.equals(testimoniesList, other.testimoniesList);
	}

	
}
