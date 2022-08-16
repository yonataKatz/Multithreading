package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

public class PublishConfrenceBroadcast implements Broadcast {

    //fields
    ConfrenceInformation con;

    //constructor
    public PublishConfrenceBroadcast(ConfrenceInformation confrence)
    {
        con=confrence;
    }

    public ConfrenceInformation getConfrence()
    {
        return con;
    }

}
