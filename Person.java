package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import control.Court;
import enums.Gender;
import utils.UtilsMethods;

public class Person implements Serializable{
	
	//Fields
	private int id; //PK
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String address;
	private String phoneNumber;
	private String email;
	private Gender gender;
	
	
	
	//Constructor
	public Person(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
			String email, Gender gender) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		setBirthDate(birthDate);
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.gender = gender;
	}
	
	//Getters
	public int getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public String getAddress() {
		return address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getEmail() {
		return email;
	}

	public Gender getGender() {
		return gender;
	}
	
	public double getAge() {
		//returns the age of the person, in days
		return UtilsMethods.dateDiffInDays(Court.TODAY, birthDate);
	}
	
	//Setters
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setBirthDate(Date birthDate) {
		//if the birthday is after "today", it set to "today"
		if(birthDate.after(Court.TODAY)) {
			birthDate=Court.TODAY;
		}
		this.birthDate = birthDate;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	
	//hashCode and equals according to the PK
	


	//toString
	@Override
	public String toString() {
		return "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate
				+ ", address=" + address + ", phoneNumber=" + phoneNumber + ", email=" + email + ", gender=" + gender
				;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return id == other.id;
	}

	
	
}
