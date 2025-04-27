package model;

import java.io.Serializable;
import java.util.Objects;

public class Testimony implements Serializable{
	private static int count=0;
	private int testimonyID;
	private Case casee ;
	private String testimonyContent;
	private Witness witness;
	public Testimony( Case casee, String testimonyContent, Witness witness) {
		super();
		this.testimonyID = count++;
		this.casee = casee;
		this.testimonyContent = testimonyContent;
		this.witness = witness;
	}
	@Override
	public String toString() {
		return "Testimony [testimonyID=" + testimonyID + ", caseID=" + casee.getCode() + ", testimonyContent=" + testimonyContent
				+ ", witness=" + witness.getId()+" "+witness.getFirstName() + "]";
	}
	public int getTestimonyID() {
		return testimonyID;
	}
	
	public Case getCasee() {
		return casee;
	}
	public void setCasee(Case casee) {
		this.casee = casee;
	}
	public String getTestimonyContent() {
		return testimonyContent;
	}
	public void setTestimonyContent(String testimonyContent) {
		this.testimonyContent = testimonyContent;
	}
	public Witness getWitness() {
		return witness;
	}
	public void setWitness(Witness witness) {
		this.witness = witness;
	}
	@Override
	public int hashCode() {
		return Objects.hash(casee, testimonyContent, testimonyID, witness);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Testimony other = (Testimony) obj;
		return Objects.equals(casee, other.casee) && Objects.equals(testimonyContent, other.testimonyContent)
				&& testimonyID == other.testimonyID && Objects.equals(witness, other.witness);
	}
}
