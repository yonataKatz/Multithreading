package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    //field
    private LinkedBlockingQueue<String> trainedModels;
    private AtomicInteger processBatchCPU;
    private AtomicInteger CPUnitsTime;
    private AtomicInteger GPUnitsTime;


    //constructor
public Statistics()
{
    trainedModels = new LinkedBlockingQueue<>();
    processBatchCPU = new AtomicInteger(0) ;
    CPUnitsTime = new AtomicInteger(0);
    GPUnitsTime = new AtomicInteger(0);
}

    //methods
    public void addTrainedModels(Model m) {
        trainedModels.add(m.getName());
    }

    public void increaseprocessBatchCPU(int havePro){
        processBatchCPU.addAndGet(havePro);
    }

    public void increaseCPUnitsTime (){CPUnitsTime.incrementAndGet();}

    public void increaseGPUnitsTime (){GPUnitsTime.incrementAndGet();}

    public AtomicInteger getCPUnitsTime(){ return CPUnitsTime;}

    public AtomicInteger getGPUnitsTime() { return GPUnitsTime;}

    public AtomicInteger getProcessBatchCPU() {return processBatchCPU;}
    }

