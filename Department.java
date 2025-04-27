package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

import enums.Specialization;

public class Department implements Serializable{
	private int number;
	private String name;
	private Employee manager;
	private String building;
	private Specialization specialization;
	private HashSet<Employee> employees;
	private HashSet<Lawyer> lawyers;
	private HashSet<Courtroom> courtrooms;
	public Department(int number, String name, Employee manager, String building, Specialization specialization) {
		super();
		this.number = number;
		this.name = name;
		this.manager = manager;
		this.building = building;
		this.specialization = specialization;
		this.employees = new HashSet<Employee>();
		this.lawyers = new HashSet<Lawyer>();
		this.courtrooms = new HashSet<Courtroom>();
	}
	
	

	public boolean addEmployee(Employee employee) {
		if(employee == null) return false;
		if(employees.contains(employee)) return false;
		employee.addDepartment(this);
		employees.add(employee);
		return true;	
	}
	
	public boolean removeEmployee(Employee employee) {
		if(employee == null) return false;
		if(!employees.contains(employee)) return false;
		employee.removeDepartment(this);
		employees.remove(employee);
		return true;	
	}
	
	public boolean addJudge(Judge judge) {
		if(judge == null) return false;
		if(lawyers.contains(judge)) return false;
		judge.setDepartment(this);
		lawyers.add(judge);
		return true;	
	}
	
	public boolean removeJudge(Judge judge) {
		if(judge == null) return false;
		if(!lawyers.contains(judge)) return false;
		judge.setDepartment(null);
		lawyers.remove(judge);
		return true;	
	}

	public boolean addLawyer(Lawyer lawyer) {
		if(lawyer == null) return false;
		if(lawyers.contains(lawyer)) return false;
		lawyer.setDepartment(this);
		lawyers.add(lawyer);
		return true;	
	}
	
	public boolean removeLawyer(Lawyer lawyer) {
		if(lawyer == null) return false;
		if(!lawyers.contains(lawyer)) return false;
		lawyer.setDepartment(null);
		for(Lawyer l : lawyers)
			if(l.getId()==lawyer.getId()) lawyers.remove(l);
		lawyers.remove(lawyer);
		return true;	
	}
	
	public boolean addCourtroom(Courtroom courtroom) {
		if(courtroom == null) return false;
		if(courtrooms.contains(courtroom)) return false;
		courtroom.setDepartment(this);
		courtrooms.add(courtroom);
		return true;	
	}
	
	public boolean removeCourtroom(Courtroom courtroom) {
		if(courtroom == null) return false;
		if(!courtrooms.contains(courtroom)) return false;
		courtroom.setDepartment(null);
		courtrooms.remove(courtroom);
		return true;	
	}

	public int getNumber() {
		return number;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}

	public HashSet<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(HashSet<Employee> employees) {
		this.employees = employees;
	}

	public HashSet<Lawyer> getLawyers() {
		return lawyers;
	}

	public void setLawyers(HashSet<Lawyer> lawyers) {
		this.lawyers = lawyers;
	}

	
	public HashSet<Courtroom> getCourtrooms() {
		return courtrooms;
	}

	public void setCourtrooms(HashSet<Courtroom> courtrooms) {
		this.courtrooms = courtrooms;
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(building,manager, name, number, specialization);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Department other = (Department) obj;
		return Objects.equals(building, other.building) && Objects.equals(courtrooms, other.courtrooms)
				&& Objects.equals(employees, other.employees) && Objects.equals(lawyers, other.lawyers)
				&& Objects.equals(manager, other.manager) && Objects.equals(name, other.name) && number == other.number
				&& specialization == other.specialization;
	}

	@Override
	public String toString() {
		return "Department [number=" + number + ", name=" + name + ", manager=" + manager + ", building=" + building
				+ ", specialization=" + specialization + "]";
	}

	

	
	
	
	

}
