package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Document implements Serializable{
	private static int count=0;
	private int code;
	private String title;
	private String content;
	private Date issusedDate;
	private Case casee;
	public Document(String title, String content, Date issusedDate, Case casee) {
		super();
		this.code = count++;
		this.title = title;
		this.content = content;
		this.issusedDate = issusedDate;
		this.casee = casee;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getIssusedDate() {
		return issusedDate;
	}
	public void setIssusedDate(Date issusedDate) {
		this.issusedDate = issusedDate;
	}
	public Case getCasee() {
		return casee;
	}
	public void setCasee(Case casee) {
		this.casee = casee;
	}
	@Override
	public String toString() {
		return "Document [code=" + code +", caseID=" + casee.getCode() + ", title=" + title + ", content=" + content + ", issusedDate=" + issusedDate+ "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(casee, code, content, issusedDate, title);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		return Objects.equals(casee, other.casee) && code == other.code && Objects.equals(content, other.content)
				&& Objects.equals(issusedDate, other.issusedDate) && Objects.equals(title, other.title);
	}
	
	
	

}
