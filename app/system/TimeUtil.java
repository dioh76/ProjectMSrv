package system;

import java.sql.Timestamp;
import java.util.Calendar;

public final class TimeUtil {
	public static long getCurrent()
	{
		return Calendar.getInstance().getTime().getTime();
	}
	
	public static long diffFromNow( Timestamp rhs )
	{
		Timestamp now = new Timestamp( TimeUtil.getCurrent() );
		return now.getTime() - rhs.getTime();
	}
	
	public static long toDay( long day )
	{
		return day * 24L * 60L * 60L * 1000L;
	}
	
	public static long second( long sec ) {
		return sec * 1000L;
	}

	public static long minute( long min ) {
		return min * 60L * 1000L;
	}
	
}