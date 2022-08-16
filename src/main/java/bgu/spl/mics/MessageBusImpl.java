package bgu.spl.mics;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private ConcurrentHashMap<Event<?>, Future<?>> futureMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastsSubscribeMap;
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribeMap;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceQueues;
	private LinkedBlockingQueue<GPUService> gpuServicesQue;
	private LinkedBlockingQueue<ConferenceService> confrenceServiceQue;

	//constructor - singleton
	private static class messageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	public static MessageBusImpl getInstance() {
		return messageBusHolder.instance;
	}

	private MessageBusImpl() {
		futureMap = new ConcurrentHashMap<>();
		broadcastsSubscribeMap = new ConcurrentHashMap<>();
		eventSubscribeMap = new ConcurrentHashMap<>();
		microServiceQueues = new ConcurrentHashMap<>();
		gpuServicesQue = new LinkedBlockingQueue<>();
		confrenceServiceQue = new LinkedBlockingQueue<>();

		broadcastsSubscribeMap.put(PublishConfrenceBroadcast.class, new LinkedBlockingQueue<MicroService>());
		broadcastsSubscribeMap.put(TerminationBroadcast.class, new LinkedBlockingQueue<MicroService>());
		broadcastsSubscribeMap.put(TickBroadcast.class, new LinkedBlockingQueue<MicroService>());
		eventSubscribeMap.put(PublishResultEvent.class, new LinkedBlockingQueue<MicroService>());
		eventSubscribeMap.put(TestModelEvent.class, new LinkedBlockingQueue<MicroService>());
		eventSubscribeMap.put(TrainModelEvent.class, new LinkedBlockingQueue<MicroService>());
	}



	public Future<?> getFutureFromMap(Event<?> e) {
		return futureMap.get(e);
	}


	public void addFutureToMap(Event<?> event, Future<?> future) {
		this.futureMap.put(event, future);
	}


	/**
	 * @PRE - m is registered to MB
	 * @POST - type enters an event types collection of m on MB
	 */
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		LinkedBlockingQueue<MicroService> l = eventSubscribeMap.get(type);
		l.add(m);
	}

	public <T> LinkedBlockingQueue<MicroService> getSubscribes (Class<? extends  Event<T>> type)
	{
		return eventSubscribeMap.get(type);
	}


	/**
	 * @PRE - m is registered to MB
	 * @POST - type enters a broadcast types collection of m on MB
	 */
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		LinkedBlockingQueue<MicroService> l = broadcastsSubscribeMap.get(type);
		l.add(m);
	}

	public <T> LinkedBlockingQueue<MicroService> getSubscribesB (Class<? extends  Broadcast> type)
	{
		return broadcastsSubscribeMap.get(type);
	}


	/**
	 * @PRE - MB is not null
	 * @POST - @preMB = @postMB
	 */
	public <T> void complete(Event<T> e, T result) {
		Future<T> fu = (Future<T>) getFutureFromMap(e);
		fu.resolve(result);
	}


	public LinkedBlockingQueue<Message> getMicroServiceQueues(MicroService m)
	{
		return microServiceQueues.get(m);
	}


	/**
	 * @PRE - MB is not null
	 * @POST - Queues of sucscribers will increase by 1
	 */
	public void sendBroadcast(Broadcast b) {
		Iterator<MicroService> it = broadcastsSubscribeMap.get(b.getClass()).iterator();
		while (it.hasNext()) {
			MicroService m = it.next();
			microServiceQueues.get(m).add(b);
		}
	}


	/**
	 * @PRE - MB is not null
	 * @POST - Queues of sucscribers will increase by 1
	 * @Post - returns a Future object to be resolved by a handling micro-service
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<T>();
		futureMap.put(e, f);
		if (e.getClass() == PublishResultEvent.class) {
			ConferenceService c;
			if (!confrenceServiceQue.isEmpty()) {
				synchronized (confrenceServiceQue) {
					c = confrenceServiceQue.remove();
					microServiceQueues.get(c).add(e);
					confrenceServiceQue.add(c);
				}
			}
		}
		if (e.getClass() == TrainModelEvent.class) {
			synchronized (gpuServicesQue) {
				GPUService g;
				if (!gpuServicesQue.isEmpty()) {
					g = gpuServicesQue.remove();
					microServiceQueues.get(g).add(e);
					gpuServicesQue.add(g);
				}
			}
		}
		if (e.getClass() == TestModelEvent.class) {
			synchronized (gpuServicesQue) {
				GPUService g;
				if (!gpuServicesQue.isEmpty())
					microServiceQueues.get(gpuServicesQue.peek()).add(e);
			}
		}
		return f;
	}

	/**
	 * @PRE -MB is not null
	 * @POST - the number of queues MB holds is increased by 1
	 */
	public void register(MicroService m) {
		microServiceQueues.put(m,new LinkedBlockingQueue<Message>());
		if (m.getClass()== GPUService.class)
			gpuServicesQue.add((GPUService) m);
		if (m.getClass()== ConferenceService.class)
			confrenceServiceQue.add((ConferenceService) m);
	}

	/**
	 * @PRE - MB is not null
	 * @POST - the number of queues MB holds is decreased by 1
	 */
	public void unregister(MicroService m) {
		microServiceQueues.remove(m);
		if (m.getClass()== ConferenceService.class)
			synchronized (confrenceServiceQue) {
				confrenceServiceQue.remove((ConferenceService) m);
				broadcastsSubscribeMap.get(TickBroadcast.class).remove(m);
				broadcastsSubscribeMap.get(TerminationBroadcast.class).remove(m);
				eventSubscribeMap.get(PublishResultEvent.class).remove(m);

			}
		if (m.getClass()==GPUService.class)
			synchronized (gpuServicesQue) {
				gpuServicesQue.remove((GPUService) m);
			}
	}

	/**
	 * @PRE - MB is not null
	 * @POST - checks and decreases by 1 the queue of m (if not empty)
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException{
		Message mes=null;
		try {
			mes = microServiceQueues.get(m).take();
		}catch (InterruptedException e){}
		return mes;
	}
}
