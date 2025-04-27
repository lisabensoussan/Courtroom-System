package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import control.Court;
import enums.Gender;
import enums.Position;

public class Employee extends Person implements Serializable{
	private Date workStartDate;
	private HashSet<Department> departments;
	private double salary;
	private Position position;
    private byte[] photo; // Add photo field

    public Employee(int id, String firstName, String lastName, java.util.Date birthDate, String address,
            String phoneNumber, String email, Gender gender, Date workStartDate,double salary, Position position) {
        super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
        setWorkStartDate(workStartDate);
        this.departments = new HashSet<Department>();
        this.salary = salary;
        this.position = position;
    }
    
    public Employee(int id, String firstName, String lastName, java.util.Date birthDate, String address,
            String phoneNumber, String email, Gender gender, Date workStartDate, double salary, 
            Position position, byte[] photo) {
        super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
        setWorkStartDate(workStartDate);
        this.departments = new HashSet<Department>();
        this.salary = salary;
        this.position = position;
        this.photo = photo;
    }
	
	
	public boolean addDepartment(Department department) {
		if(department == null) return false;
		if(departments.contains(department)) return false;
		departments.add(department);
		return true;
	}
	
	public boolean removeDepartment(Department department) {
		if(department == null) return false;
		if(!departments.contains(department)) return false;
		departments.remove(department);
		return true;
	}
	
	
	public void setWorkStartDate(Date workStartDate) {
		//if the birthday is after "today", it set to "today"
		if(workStartDate.after(Court.TODAY)) {
			workStartDate= Court.TODAY;
		}
		this.workStartDate = workStartDate;
	}


	public HashSet<Department> getDepartments() {
		return departments;
	}


	public void setDepartments(HashSet<Department> departments) {
		this.departments = departments;
	}


	public double getSalary() {
		return salary;
	}


	public void setSalary(double salary) {
		this.salary = salary;
	}


	public Position getPosition() {
		return position;
	}


	public void setPosition(Position position) {
		this.position = position;
	}


	public Date getWorkStartDate() {
		return workStartDate;
	}
	// Getter and setter for photo
    public byte[] getPhoto() {
        return photo;
    }
    
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
    // Method to check if employee has photo
    public boolean hasPhoto() {
        return photo != null && photo.length > 0;
    }


	@Override
	public String toString() {
		return "Employee [id =" + getId()
		+ ", FirstName=" + getFirstName() + ", LastName=" + getLastName() + ", BirthDate="
		+ getBirthDate().toString() + ", Address=" + getAddress() + ", PhoneNumber=" + getPhoneNumber()
		+ ", Email=" + getEmail() + ", Gender=" + getGender() + "workStartDate= " + workStartDate.toString() + ", salary=" + salary
		+ ", position=" + position.toString()  + "]";
	
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(position, salary, workStartDate);
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
		Employee other = (Employee) obj;
		return Objects.equals(departments, other.departments) && position == other.position
				&& Double.doubleToLongBits(salary) == Double.doubleToLongBits(other.salary)
				&& Objects.equals(workStartDate, other.workStartDate);
	}


	
	
	

}
