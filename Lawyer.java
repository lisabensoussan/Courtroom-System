package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import enums.Gender;
import enums.Specialization;

public class Lawyer extends Person implements Serializable {
    private Specialization specialization;
    private int licenseNumber;
    private double salary;
    private Department department;
    private HashSet<Case> casesHandled;
    // Add photo field as byte array to store the image data
    private byte[] photo;

    public Lawyer(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
            String email, Gender gender, Specialization specialization, int licenseNumber, double salary) {
        super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.salary = salary;
        this.casesHandled = new HashSet<Case>();
    }
    
    // Constructor with photo
    public Lawyer(int id, String firstName, String lastName, Date birthDate, String address, String phoneNumber,
            String email, Gender gender, Specialization specialization, int licenseNumber, double salary, byte[] photo) {
        super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.salary = salary;
        this.casesHandled = new HashSet<Case>();
        this.photo = photo;
    }

    public boolean addCase(Case c) {
        if (c == null)
            return false;
        if (casesHandled.contains(c))
            return false;
        casesHandled.add(c);
        return true;
    }

    public boolean removeCase(Case c) {
        if (c == null)
            return false;
        if (!casesHandled.contains(c))
            return false;
        casesHandled.remove(c);
        return true;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(int licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public HashSet<Case> getCasesHandled() {
        return casesHandled;
    }

    public void setCasesHandled(HashSet<Case> casesHandled) {
        this.casesHandled = casesHandled;
    }
    
    // Getter and setter for photo
    public byte[] getPhoto() {
        return photo;
    }
    
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
    // Method to check if the lawyer has a photo
    public boolean hasPhoto() {
        return photo != null && photo.length > 0;
    }

    @Override
    public String toString() {
        return "Lawyer [id =" + getId() + ", FirstName=" + getFirstName() + ", LastName=" + getLastName()
                + ", BirthDate=" + getBirthDate() + ", Address=" + getAddress() + ", PhoneNumber=" + getPhoneNumber()
                + ", Email=" + getEmail() + ", Gender=" + getGender() + ", specialization=" + specialization
                + ", licenseNumber=" + licenseNumber + ", salary=" + salary + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(department, licenseNumber, salary, specialization);
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
        Lawyer other = (Lawyer) obj;
        return Objects.equals(casesHandled, other.casesHandled) && Objects.equals(department, other.department)
                && licenseNumber == other.licenseNumber
                && Double.doubleToLongBits(salary) == Double.doubleToLongBits(other.salary)
                && specialization == other.specialization;
    }
}