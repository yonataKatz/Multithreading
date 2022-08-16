package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model> {
    //fields
    private Model model;



    //constructor
    public TestModelEvent ( Model mod)
    {
        model=mod;
    }

    public Model getModel()
    {
        return model;
    }

    public Student getStudent(){ return model.getStudent();}

}
