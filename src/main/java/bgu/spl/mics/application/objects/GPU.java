package bgu.spl.mics.application.objects;
//import bgu.spl.mics.application.messages.TrainModelEvent;
//import java.util.LinkedList;
//import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */

    /**
     * @INV - GPU type doesnt change after creation
     * @INV - cluster doesnt change after creation
     */

    public enum Type {RTX3090, RTX2080, GTX1080}

    //fields
    private Type type;
    private Model model;
    private Cluster cluster;
    private boolean isMatched;
    private int vRamCapacity;
    private LinkedBlockingQueue<DataBatch> vRam;
    private LinkedBlockingQueue<DataBatch> unprocessedBatches;
    private int internalClock;
    private int toBeProcessed;
    private int sentBatches;



    //constructor
    public GPU(Type t)
    {
        vRamCapacity=0;
        type=t;
        model=null;
        cluster=Cluster.getInstance();
        if (type==Type.RTX3090)
            vRamCapacity= 32;
        if (type==Type.RTX2080)
            vRamCapacity= 16;
        if (type==Type.GTX1080)
            vRamCapacity= 8;
        vRam=new LinkedBlockingQueue<>();
        unprocessedBatches=new LinkedBlockingQueue<>();
        isMatched=false;
        internalClock=0;
        toBeProcessed=0;
        sentBatches=0;
    }


    public int getToBeProcessed()
    {
        return toBeProcessed;
    }

    /**
     * @pre - toBeProcessed>=0
     * @post - @preToBeProcessed -1 = @Post ToBeProcessed
     */
    public void addToProcessed()
    {
        toBeProcessed--;
    }

    /**
     * @pre - toBeProcessed>=0
     * @post -  toBeProcessed = unprocessedBatches.size();
     */
    public void setToBeProcessed()
    {
        toBeProcessed = unprocessedBatches.size();
    }

    /**
     * @pre - internalClock>=0
     * @post - @Pre internalClock +1 = @post internalClock
     */
    public void ClockTick()
    {
        internalClock++;
        getCluster().getStatistics().increaseGPUnitsTime();
    }


    public void updateInternalClock(int num)
    {
        internalClock=internalClock-num;
    }


    public int getInternalClock()
    {
        return internalClock;
    }


    public boolean isMatched()
    {
        return isMatched;
    }


    public void setMatch(){ isMatched=true;}


    public void setUnMatch(){ isMatched=false;}


    public Type getType()
    {
        return type;
    }


    public Model getModel()
    {
        return model;
    }


    public Cluster getCluster()
    {
        return cluster;
    }


    public LinkedBlockingQueue<DataBatch> getVRam ()
    {
        return vRam;
    }


    public LinkedBlockingQueue<DataBatch> getUnprocessedBatches ()
    {
        return unprocessedBatches;
    }


    public void setModel (Model m)
    {
        model=m;
    }


    public boolean vRamIsFull() { return false;}



    public void DataToBatches(){
        Data data = model.getData();
        for (int i = 0 ; i<(int)data.size()/1000;i++)
            unprocessedBatches.add(new DataBatch(data,i*1000));

    }


    public int getTicksForABatch(){
        if (vRamCapacity==32)
            return 1;
        else if(vRamCapacity==16)
            return 2;
        else
            return 4;
    }


    /**
     * @pre - -
     * @post - @Pre UnprocessedBatches.size >= @Post UnprocessedBatches.size
     */
    public void sendUnprocessed() {
          if (cluster.getUnprocessedQueue(this).isEmpty()) {
              int count = 0;
              while (!unprocessedBatches.isEmpty() & count <= 100) {
                  count++;
                  cluster.putUnprocessed(unprocessedBatches.remove(), this);
                  sentBatches++;
                  addToProcessed();
              }
          }
      }


    /**
     * @pre - -
     * @post - cluster.getProcessed(this).size >=0
     * @post - vRam.size>=0
     */
    public void getProcessed(){
        while (!cluster.getProcessed(this).isEmpty() & !vRamIsFull())
            vRam.add(cluster.getProcessed(this).remove());
    }
}

