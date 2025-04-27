package model;

import java.io.Serializable;
import java.util.Objects;

public class Courtroom implements Serializable{
	private int courtroomNumber;
	private Department department;
	
	public Courtroom(int courtroomNumber, Department department) {
		super();
		this.courtroomNumber = courtroomNumber;
		this.department = department;
		
	}
	public int getCourtroomNumber() {
		return courtroomNumber;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	@Override
	public String toString() {
		return "Courtroom [courtroomNumber=" + courtroomNumber + ", department=" + department.getName() + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(courtroomNumber, department);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Courtroom other = (Courtroom) obj;
		return courtroomNumber == other.courtroomNumber && Objects.equals(department, other.department);
	}

	
	
	
	
	

}
