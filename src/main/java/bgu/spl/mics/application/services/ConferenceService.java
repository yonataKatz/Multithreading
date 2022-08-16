package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    //fields
    private ConfrenceInformation con;


    public ConferenceService(String name, ConfrenceInformation c) {
        super(c.getName()+" service");
        con=c;
    }

    @Override
    protected void initialize() {

        subscribeEvent(PublishResultEvent.class, (PublishResultEvent p)->
        {
            if (p.getModel().getTestResult()== Model.tests.Good)
                con.addResult(p.getModel());
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
            con.ClockTick();
            if (con.getInternalClock()>=con.getDate())
            {
                sendBroadcast(new PublishConfrenceBroadcast(con));
                terminate();
            }
        });

        this.subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t)->{
            terminate();
        });
    }
}
