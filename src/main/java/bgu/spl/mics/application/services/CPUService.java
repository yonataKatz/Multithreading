package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;

/**
 * CPU service is responsible for handling the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private CPU cpu;

    public CPUService(String name, CPU c) {
        super(name);
        cpu=c;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
            cpu.ClockTick();
            if (cpu.getCurrentGPU()==null)
            {
                cpu.resetClock();
                GPU g = cpu.getCluster().findGpu();
                if (g!=null) {
                    cpu.setCurrentGpu(g);
                }
            }
            if (cpu.getCurrentGPU()!=null) {
                synchronized (cpu.getCollection()) {
                    cpu.getToCpu();
                    int haveProcessed = cpu.process();
                    cpu.sendBack(haveProcessed);
                    cpu.getCluster().getStatistics().increaseprocessBatchCPU(haveProcessed);
                }
                //checks if we finished with current GPU
                if (cpu.getCollection().isEmpty() & cpu.getCluster().getUnprocessedQueue(cpu).isEmpty())
                    cpu.setCurrentGpu(null);
            }
        });

        this.subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t)->{
            terminate();
        });
    }




}
