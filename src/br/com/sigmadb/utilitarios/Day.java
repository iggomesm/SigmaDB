package br.com.sigmadb.utilitarios;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.sigmadb.exceptions.SigmaDBException;


/**
Stores dates and perform date arithmetic.

This is another date class, but more convenient that
<tt>java.util.Date</tt> or <tt>java.util.Calendar</tt>

@version 1.20 5 Oct 1998
@author Cay Horstmann 
*/
public class Day implements Cloneable, Serializable {
    private static final long serialVersionUID = 5884622348312431542L;
    public static int SUNDAY = 1;
    public static int MONDAY = 2;
    public static int TUESDAY = 3;
    public static int WEDNESDAY = 4;
    public static int THURSDAY = 5;
    public static int FRIDAY = 6;
    public static int SATURDAY = 7;
    
        
    public final static DecimalFormat prec4 = new DecimalFormat("0000");
    public final static DecimalFormat prec2 = new DecimalFormat("00");

    /** @serial */
    private int day;

    /** @serial */
    private int month;

    /** @serial */
    private int year;

    /**
      Constructs today's date
    */
    public Day() {
        GregorianCalendar todaysDate = new GregorianCalendar();
        year = todaysDate.get(Calendar.YEAR);
        month = todaysDate.get(Calendar.MONTH) + 1;
        day = todaysDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param data String contendo uma data formatada no padrão dd/MM/yyyy
     * @throws Exception
     */
    public Day(String data) throws Exception{
        this(System.currentTimeMillis());

        if (!SigmaDBUtil.isNullOrEmpty(data)) {
            Timestamp timestamp;

            timestamp = SigmaDBUtil.converteStringTimestamp(data);

            GregorianCalendar todaysDate = new GregorianCalendar();
            todaysDate.setTimeInMillis(timestamp.getTime());
            year = todaysDate.get(Calendar.YEAR);
            month = todaysDate.get(Calendar.MONTH) + 1;
            day = todaysDate.get(Calendar.DAY_OF_MONTH);
        } else {
        	throw new SigmaDBException("Não é permitido informar uma String nula para o construtor da classe Day.");
        }
    }

    public Day(Timestamp timestamp) throws SigmaDBException {
        if (timestamp == null) {
        	throw new SigmaDBException("Não é permitido informar um objeto null para o construtor da classe Day.");
        }

        GregorianCalendar todaysDate = new GregorianCalendar();
        todaysDate.setTimeInMillis(timestamp.getTime());
        year = todaysDate.get(Calendar.YEAR);
        month = todaysDate.get(Calendar.MONTH) + 1;
        day = todaysDate.get(Calendar.DAY_OF_MONTH);
    }
    
    public Day(Date date) throws SigmaDBException {
    	this(date == null ? null : new Timestamp(date.getTime()));    	
    }

    public Day(long time) {
        GregorianCalendar todaysDate = new GregorianCalendar();
        todaysDate.setTimeInMillis(time);
        year = todaysDate.get(Calendar.YEAR);
        month = todaysDate.get(Calendar.MONTH) + 1;
        day = todaysDate.get(Calendar.DAY_OF_MONTH);
    }

    public int getCompetencia() {
        return SigmaDBUtil.parseStringToInt(prec4.format(getYear())+ "" +
            prec2.format(getMonth())+""+prec2.format(getDay()));
    }

    public Timestamp toTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return new Timestamp(calendar.getTimeInMillis());
    }
    
    public Timestamp toTimestampZeroHour() {
    	
    	return (this.zeraHoraData(toTimestamp().getTime()));
    }
    
    public java.sql.Date toDate() {
    	return new java.sql.Date(toTimestampZeroHour().getTime());
    }
    
    public java.sql.Time toTime() {
    	return new java.sql.Time(toTimestamp().getTime());
    }
    
    public boolean maiorQue(Day dia) {
        int value = daysBetween(dia);

        if (value <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean menorQue(Day dia) {
        int value = daysBetween(dia);

        if (value >= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean maiorIgualA(Day dia) {
        int value = daysBetween(dia);

        if (value < 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean menorIgualA(Day dia) {
        int value = daysBetween(dia);

        if (value > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
       Advances this day by n days. For example.
       d.advance(30) adds thirdy days to d

       @param n the number of days by which to change this
          day (can be < 0)
     */
    public void advance(int n) {
        fromJulian(toJulian() + n);
    }

    /**
       Gets the day of the month

       @return the day of the month (1...31)
     */
    public int getDay() {
        return day;
    }

    /**
       Gets the month

       @return the month (1...12)
     */
    public int getMonth() {
        return month;
    }

    /**
       Gets the year

       @return the year (counting from 0, <i>not</i> from 1900)
     */
    public int getYear() {
        return year;
    }

    /**
       Gets the weekday
     */
    public int weekday() {
        return ((toJulian() + 1) % 7) + 1;
    }

    /**
       The number of days between this and day parameter

       @param b any date
       @return the number of days between this and day parameter
          and b (> 0 if this day comes after b)
     */
    public int daysBetween(Day b) {
        return toJulian() - b.toJulian();
    }

    /**
       A string representation of the day

       @return a string representation of the day
     */
    public String toString() {
        DecimalFormat df = new DecimalFormat("00");

        return df.format(day) + "/" + df.format(month) + "/" + year;
    }

    /**
     * Usar somente para pesquisa. Testado s� no postgres.
     * @return
     */
    public String getDataBanco() {
        return "'" + year + "-" + prec2.format(month) + "-" +
        prec2.format(day) + "%'";
    }

    public String getDataBanco23h() {
        return "'" + year + "-" + prec2.format(month) + "-" +
        prec2.format(day) + " 23:59:59.999'";
    }

    public String getDataBancoAnoMes() {
        return "'" + year + "-" + prec2.format(month) + "%'";
    }

    public String getDataBancoHoraZerada() {
        return "'" + year + "-" + prec2.format(month) + "-" +
        prec2.format(day) + " 00:00:00.000'";
    }

    public int toInt() {
        return Integer.parseInt("" + year + prec2.format(month) +
            prec2.format(day));
    }

    /**
       Makes a bitwise copy of a Day object

       @return a bitwise copy of a Day object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) { // this shouldn't happen, since we are Cloneable

            return null;
        }
    }

    public String getMesDescricao(int mes) {
        String retorno = "";

        if ((mes <= 0) || (mes > 12)) {
            return null;
        }

        String[] meses = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
            };
        retorno = meses[mes - 1];

        return retorno;
    }

    /**
       Compares this Day against another object

       @param obj another object
       @return true if the other object is identical to this Day object
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        Day b = (Day) obj;

        return (day == b.day) && (month == b.month) && (year == b.year);
    }

    /**
       Computes the number of days between two dates

       @return true iff this is a valid date
     */
    private boolean isValid() {
        Day t = new Day();
        t.fromJulian(this.toJulian());

        return (t.day == day) && (t.month == month) && (t.year == year);
    }

    /**
       @return The Julian day number that begins at noon of
       this day
       Positive year signifies A.D., negative year B.C.
       Remember that the year after 1 B.C. was 1 A.D.

       A convenient reference point is that May 23, 1968 noon
       is Julian day 2440000.

       Julian day 0 is a Monday.

       This algorithm is from Press et al., Numerical Recipes
       in C, 2nd ed., Cambridge University Press 1992
     */
    private int toJulian() {
        int jy = year;

        if (year < 0) {
            jy++;
        }

        int jm = month;

        if (month > 2) {
            jm++;
        } else {
            jy--;
            jm += 13;
        }

        int jul = (int) (java.lang.Math.floor(365.25 * jy) +
            java.lang.Math.floor(30.6001 * jm) + day + 1720995.0);

        int IGREG = 15 + (31 * (10 + (12 * 1582)));

        // Gregorian Calendar adopted Oct. 15, 1582
        if ((day + (31 * (month + (12 * year)))) >= IGREG) // change over to Gregorian calendar
         {
            int ja = (int) (0.01 * jy);
            jul += (2 - ja + (int) (0.25 * ja));
        }

        return jul;
    }

    /**
       Converts a Julian day to a calendar date

       This algorithm is from Press et al., Numerical Recipes
       in C, 2nd ed., Cambridge University Press 1992

       @param j  the Julian date
     */
    private void fromJulian(int j) {
        int ja = j;

        int JGREG = 2299161;

        /* the Julian date of the adoption of the Gregorian
           calendar
        */
        if (j >= JGREG) /* cross-over to Gregorian Calendar produces this
        correction
        */ {
            int jalpha = (int) (((float) (j - 1867216) - 0.25) / 36524.25);
            ja += ((1 + jalpha) - (int) (0.25 * jalpha));
        }

        int jb = ja + 1524;
        int jc = (int) (6680.0 + (((float) (jb - 2439870) - 122.1) / 365.25));
        int jd = (int) ((365 * jc) + (0.25 * jc));
        int je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;

        if (month > 12) {
            month -= 12;
        }

        year = jc - 4715;

        if (month > 2) {
            --year;
        }

        if (year <= 0) {
            --year;
        }
    }

    /**
     * Method that comes back the current hour of the system.
     */
    public String getHour() {
        Date date = new Date();

        return date.getHours() + ":" + date.getMinutes() + ":" +
        date.getSeconds();
    }

    public String getDataCompletaExtenso() {
        return getDay() + " de " + getMesDescricao(getMonth()) + " de " +
        getYear() + ".";
    }

    public String getDayFormat() {
        DecimalFormat df = new DecimalFormat("00");

        return df.format(day);
    }

    public String getMonthFormat() {
        DecimalFormat df = new DecimalFormat("00");

        return df.format(month);
    }
    
    @SuppressWarnings("deprecation")
	public String getLastHour() {
    	Date date = new Date();
        date.setHours(23);
        date.setMinutes(59) ;
        date.setSeconds(59);
      
        return  date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
    
    public static Timestamp zeraHoraData(long time){
		
		Calendar calendar = Calendar.getInstance(); 
	    
	    calendar.setTimeInMillis(time);  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);
		
        Timestamp timestampHoraZerada = new Timestamp(calendar.getTimeInMillis());
        
        return timestampHoraZerada;
	}
}
