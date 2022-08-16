package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
     public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedBlockingQueue<Model> models;


    public Student (String nam, String dep, String deg)
    {
        name=nam;
        department=dep;
        if (deg.charAt(0)=='M')
            status=Degree.MSc;
        else
            status=Degree.PhD;
        publications=0;
        papersRead=0;
        models = new LinkedBlockingQueue<Model>();
    }


    public void addModel(Model m)
    {
        models.add(m);
    }

    public LinkedBlockingQueue<Model> getModels()
    {
        return models;
    }

    public String getName()
    {
        return name;
    }

    public  String getDepartment()
    {
        return department;
    }

    public Degree getStatus()
    {
        return status;
    }

    public void increasePublicationsBy(int num)
    {
        publications= publications+num;
    }

    public int getPublications()
    {
        return publications;
    }

    public void increasePapersReadBy(int num)
    {
        papersRead=papersRead+num;
    }

    public int getPapersRead()
    {
        return papersRead;
    }

}
