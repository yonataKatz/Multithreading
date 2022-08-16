package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultEvent implements Event<Model> {

    //fields
    Model m;

    //constructor
    public PublishResultEvent(Model mod )
    {
        m=mod;
    }

    public Model getModel()
    {
        return m;
    }

}
