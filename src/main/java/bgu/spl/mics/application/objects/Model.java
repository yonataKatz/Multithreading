package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */


public class Model {
    public enum status {
        PreTrained, Training , Trained , Tested;
    }

    public enum tests {
       None,Good, Bad;
    }

    //fields
    private String name;
    private Data data;
    private Student student;
    private  status modelStatus;
    private tests testResult=tests.None;

    //constructor
    public Model (String nam, Data d, Student s)
    {
        name=nam;
        data=d;
        student=s;
        modelStatus= Model.status.PreTrained;
    }

    //methods
    public status getStatus(){
        return modelStatus;
    }

    public void setStatus(status s)
    {
        modelStatus =s;
    }

    public tests getTestResult()
    {
        return testResult;
    }

    public void setTests(tests t)
    {
        testResult =t;
    }

    public Data getData(){
        return data;
    }

    public Student getStudent(){
        return student;
    }

    public String getName() {return name;}
}
