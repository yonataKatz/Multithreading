package bgu.spl.mics.application.objects;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	//fields
	private LinkedBlockingQueue<GPU> gpus;
	private LinkedBlockingQueue<CPU> cpus;
	private ConcurrentHashMap<GPU,LinkedBlockingQueue<DataBatch>> gpuProcessMap;
	private ConcurrentHashMap<GPU,LinkedBlockingQueue<DataBatch>> gpuUnProcessMap;
	private Statistics statistics;

	/**
     * Retrieves the single instance of this class.
     */
	//singleton
	private static class clusterHolder{
		private static Cluster instance = new Cluster();
	}
	public static Cluster getInstance() {
		return clusterHolder.instance;
	}

	private Cluster(){
		gpuProcessMap = new ConcurrentHashMap<>();
		gpuUnProcessMap = new ConcurrentHashMap<>();
		statistics = new Statistics();
	}

	public void setCPUS (LinkedBlockingQueue<CPU> c) {
		cpus = c;
	}

	public void setGPUS (LinkedBlockingQueue<GPU> gpu) {
		gpus = gpu;
		Iterator<GPU> it = gpus.iterator();
		while(it.hasNext())
		{
			GPU c = it.next();
			gpuProcessMap.put(c,new LinkedBlockingQueue<DataBatch>());
			gpuUnProcessMap.put(c,new LinkedBlockingQueue<DataBatch>());
		}
	}

	public LinkedBlockingQueue<DataBatch> getUnprocessedQueue(GPU g)
	{
		return gpuUnProcessMap.get(g);
	}

	public GPU findGpu()
	{
		Iterator<GPU> it = gpus.iterator();
		while (it.hasNext()) {
			GPU g = it.next();
			synchronized (g) {
				if (!g.isMatched() && g.getModel() != null) {
					g.setMatch();
					return g;
				}
			}
		}
		// all of the GPUS are being helped by CPUS
		Iterator<GPU> iter = gpus.iterator();
		GPU g2,g3=null;
		int max=0;
		while (iter.hasNext()) {
			g2=iter.next();
			if (g2.getModel()!=null)
			{
				if (getUnprocessedQueue(g2).size()>=max) {
					g3 = g2;
					max = getUnprocessedQueue(g2).size();
				}
//				int num=0;
//				if (g2.getModel().getData().getType()== Data.Type.Images)
//				  	num=4;
//				if (g2.getModel().getData().getType()== Data.Type.Text)
//						num=2;
//				if (g2.getModel().getData().getType()== Data.Type.Tabular)
//					num=1;
//				if (max<g2.getToBeProcessed()*num) {
//					g3 = g2;
//					max = (g2.getToBeProcessed()+ gpuUnProcessMap.get(g2).size()) *num;
//				}
			}
		}
		return g3;
	}


	public LinkedBlockingQueue<GPU> getGpus()
	{
		return gpus;
	}


	public LinkedBlockingQueue<DataBatch> getUnprocessedQueue(CPU cpu)
	{
		GPU currentGpu = cpu.getCurrentGPU();
		return gpuUnProcessMap.get(currentGpu);
	}

	public LinkedBlockingQueue<DataBatch> getProcessed(GPU gpu){
		return gpuProcessMap.get(gpu);
	}

	public void putProcessed(DataBatch d, CPU cpu)
	{
		gpuProcessMap.get(cpu.getCurrentGPU()).add(d);
	}

	public void putUnprocessed(DataBatch d, GPU gpu)
	{
		gpuUnProcessMap.get(gpu).add(d);
	}

	public Statistics getStatistics(){	return statistics;}


}