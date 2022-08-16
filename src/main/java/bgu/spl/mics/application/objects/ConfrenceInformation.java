package bgu.spl.mics.application.objects;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation  {

    //fields
    private LinkedBlockingQueue<Model> results;
    private String name;
    private int date;
    private int internalClock;



    //constructor
    public ConfrenceInformation(String nam, int d)
    {
        name = nam;
        date =d;
        results = new LinkedBlockingQueue<>();
        internalClock=0;
    }

    public String getName()
    {
        return name;
    }

    public LinkedBlockingQueue<Model> getResults()
    {
        return results;
    }

    public void addResult(Model result)
    {
        results.add(result);
    }

    public void ClockTick()
    {
        internalClock++;
    }

    public int getDate()
    {
        return date;
    }

    public int getInternalClock()
    {
        return internalClock;
    }



}
