package vajracode.calocal.shared.model;

import java.io.Serializable;
import java.util.Date;

public class MealData implements Serializable {
	
	private long id;
	private String name;
	private int cal;
	private Date dateTime;
	
	public MealData() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCal() {
		return cal;
	}

	public void setCal(int cal) {
		this.cal = cal;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}