package model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

public class Meeting implements Serializable{
	private static int count =0;
	private int meetingID;
	private Date meetingDate;
	private Time hour;
	private Courtroom courtroom;
	private Case casee;
	public Meeting(Date meetingDate, Time hour, Courtroom courtroom, Case casee) {
		super();
		this.meetingID = count++;
		this.meetingDate = meetingDate;
		this.hour = hour;
		this.courtroom = courtroom;
		this.casee = casee;
	}
	
	public int getMeetingID() {
		return meetingID;
	}
	
	public Date getMeetingDate() {
		return meetingDate;
	}
	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}
	public Time getHour() {
		return hour;
	}
	public void setHour(Time hour) {
		this.hour = hour;
	}
	public Courtroom getCourtroom() {
		return courtroom;
	}
	public void setCourtroom(Courtroom courtroom) {
		this.courtroom = courtroom;
	}
	public Case getCasee() {
		return casee;
	}
	public void setCasee(Case casee) {
		this.casee = casee;
	}
	@Override
	public String toString() {
		return "Meeting [meetingID=" + meetingID + ", meetingDate=" + meetingDate + ", hour=" + hour + ", courtroom="
				+ courtroom + ", caseID=" + casee.getCode() + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(casee, courtroom, hour, meetingDate, meetingID);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meeting other = (Meeting) obj;
		return Objects.equals(casee, other.casee) && Objects.equals(courtroom, other.courtroom)
				&& Objects.equals(hour, other.hour) && Objects.equals(meetingDate, other.meetingDate)
				&& meetingID == other.meetingID;
	}
}
