package com.alma.telekocsi.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalDate extends Date implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	public static final String[] namesUS = new String[] {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
	public static final String[] namesFR = new String[] {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");

	
	public LocalDate() {
		super();
	}

	
	public LocalDate(long milliseconds) {
		super(milliseconds);
	}
	
	
	public String toString() {
		
		String day = sdf1.format(this);
		
		for (int cpt = 0; cpt < 7; cpt++) {
			if (namesUS[cpt].equalsIgnoreCase(day)) {
				day = namesFR[cpt];
				cpt = 7;
			}
		}
		return day + " " + sdf2.format(this);
	}
	
	
	public String getDateFormatCalendar(){
		return sdf2.format(this);
	}
	
	
	public String getDateFomatHeure(){
		return sdf3.format(this);
	}

}
