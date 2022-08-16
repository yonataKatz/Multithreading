package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	//fields
	private long speed;
	private long duration;
	private AtomicInteger clock;
	private Timer timer ;

	//speed = number of milliseconds each clocktick takes
	//duration = number of ticks before termination
	public TimeService(long s,long d ) {
		super("TimeService");
		speed=s;
		duration=d;
		clock=new AtomicInteger(0);
		timer=new Timer(true);
	}


	@Override
	protected void initialize()
	{
		this.subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast ter) ->{
			terminate();
		});
		timerBroadcastTask t = new timerBroadcastTask(clock,(int)duration);
		timer.schedule(t,speed, speed);
	}


	private class timerBroadcastTask extends TimerTask
	{
		AtomicInteger count;
		int duration;

		public timerBroadcastTask (AtomicInteger clockRef, int d)
		{
			count=clockRef;
			duration=d;
		}

		public void run()
		{
			sendBroadcast(new TickBroadcast());
			if (count.incrementAndGet()>=duration/speed) {
				sendBroadcast(new TerminationBroadcast());
				this.cancel();

			}
		}
	}
}
