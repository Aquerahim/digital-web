package com.phoenixacces.apps.utiles;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {


	public static long generateRandom(int length) {
	    Random random = new Random();
	    char[] digits = new char[length];
	    digits[0] = (char) (random.nextInt(9) + '1');
	    for (int i = 1; i < length; i++) {
	        digits[i] = (char) (random.nextInt(10) + '0');
	    }
	    return Long.parseLong(new String(digits));
	}


	public static String numeroColis() {
		String numPreContrat = null;

		Date date = new Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
		cal.setTime(date);
		numPreContrat = (Utils.generateRandom(2)+"/"+(cal.get(Calendar.MONTH) + 1)+ "-"+ Utils.generateRandom(4)).toUpperCase();

	    return numPreContrat;
	}

	public static String generateCodeValidateur(int length) {
		String numPreContrat = null;
		Date date = new Date();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
		cal.setTime(date);
		numPreContrat = "DW-"+(cal.get(Calendar.MONTH) + 1)+""+ Utils.generateRandom(length);

		return numPreContrat;
	}

	public static String instant2String() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	}


	// Retourne une date +10j
	public static Date getDateExpiration(int jour) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, jour);
		return calendar.getTime();
	}


	// Retourne une date +10j
	public static Date getDateExpirationOnMonth(int jour) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, jour);
		return calendar.getTime();
	}
}
