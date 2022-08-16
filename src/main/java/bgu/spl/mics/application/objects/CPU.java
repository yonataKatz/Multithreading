package bgu.spl.mics.application.objects;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    /**
     * @INV - Cores number will not change after creation
     * @INV - Cluster will not change after creation
     */
    //fields - consider adding an indicator of who's dataCollection we have(GPU)
    private int cores;
    private LinkedBlockingQueue<DataBatch> collection ;
    private Cluster cluster;
    private GPU currentGPU;
    private int internalClock;



    public CPU(int core)
    {
        cores=core;
        cluster=Cluster.getInstance();
        //consider changing to Queue (PAGE 12 DOWN)
        collection=new LinkedBlockingQueue<DataBatch>();
        currentGPU=null;
    }

    public void setCollection(LinkedBlockingQueue<DataBatch> col)
    {
        collection=col;
    }
    public void setCurrentGpu(GPU g)
    {
        currentGPU=g;
    }

    /**
     * @PRE -
     * @post - @post = @pre
     */
    public GPU getCurrentGPU()
    {
        return currentGPU;
    }
    /**
     * @PRE - -
     * @post - @preCPU = @postCPU
     */
    public int getCoreNum()
    {
        return cores;
    }

    /**
     * @PRE - -
     * @post - @preCPU = @postCPU
     */
    public Cluster getCluster()
    {
        return cluster;
    }


    /**
     * @PRE - -
     * @post - @preCPU = @postCPU
     */
    public int sizeOfData()
    {
        if (!collection.isEmpty())
            return collection.peek().sizeByType();
        return 0;
    }

    /**
     * @PRE - internal clock >=0
     * @post - @pre InternalClock-1 = @post InternalClock
     */
    public void ClockTick()
    {
        internalClock++;
        getCluster().getStatistics().increaseCPUnitsTime();
    }

    /**
     * @PRE - internal clock >=0
     * @post - InternalClock = 0
     */
    public void resetClock()
    {
        internalClock=0;
    }

    /**
     * @PRE - -
     * @post - @preCPU = @postCPU
     */
    public Collection<DataBatch> getCollection()
    {
        return collection;
    }


    /**
     * @PRE - Collection is not null
     * @post - @preCPUCollection.size() <= @postCPUCollection.size()
     */
    public void getToCpu()
    {
        if (collection.isEmpty()) {
            synchronized (cluster.getUnprocessedQueue(this)) {
                if (cluster.getUnprocessedQueue(this).size() > 0)
                    collection.add(cluster.getUnprocessedQueue(this).remove());
                cluster.getUnprocessedQueue(this).notifyAll();
            }
        }
    }


    /**
     * @PRE - -
     * @post - @PreCPU = @postCPU
     */
    public int process()
    {
        int sizeOfBatch = this.sizeOfData();
        if (sizeOfBatch!=0)
        {
            if (internalClock/ ((32/getCoreNum())*sizeOfBatch)>=1)
            {
                int mod = internalClock% ((32/getCoreNum())*sizeOfBatch);
                int clock = internalClock;
                internalClock=mod;
                if (collection.size()>0)
                    return clock/ ((32/getCoreNum())*sizeOfBatch);
                else
                    return 0;
            }
        }
        return 0;
    }


    /**
     * @PRE - haveProcessed>=0
     * @post - @preCPUCollection.size() >= @postCPUCollection.size()
     */
    public void sendBack(int haveProcessed)
    {
        for (int i=0; i<haveProcessed; i++) {
            DataBatch d = collection.remove();
            synchronized (d.getData()) {
                d.getData().increaseProcessed();
            }
            cluster.putProcessed(d, this);
        }
    }
}
