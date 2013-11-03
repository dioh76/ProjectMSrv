package system;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class TaskManager {
	private HashSet<Timer>	mTimers = new HashSet<Timer>();
	
	public void release()
	{
		synchronized( mTimers ) {
			for( Timer timer : mTimers ) {
				timer.cancel();
			}
			
			mTimers.clear();
		}
	}
	
	public void addTask( TimerTask task, long delay, long period )
	{
		Timer timer = new Timer();
		timer.schedule( task, delay, period );

		synchronized( mTimers ) {
			mTimers.add( timer );
		}
	}

	private static class Holder {
		private static final TaskManager Instance = new TaskManager(); 
	}
	
	public static TaskManager getInstance() 
	{
		return TaskManager.Holder.Instance;
	}
}
