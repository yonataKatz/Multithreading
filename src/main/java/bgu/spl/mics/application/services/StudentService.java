package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    //fields
    Student student;

    //constructor
    public StudentService(String name, Student s) {
        super(name);
        student=s;
    }


    @Override
    protected void initialize() {

        subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast cb)->{
            LinkedBlockingQueue<Model> q =cb.getConfrence().getResults();
            Iterator<Model> it = q.iterator();
            int count=0;
            while (it.hasNext())
                if (it.next().getStudent()==student)
                    count++;
            student.increasePapersReadBy(q.size()-count);
            student.increasePublicationsBy(count);
        });

        this.subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t)->{
            terminate();
        });

        Thread t = new Thread(()->
        {
            LinkedBlockingQueue<Model> myModels = student.getModels();
            Iterator<Model> it = myModels.iterator();
            while (it.hasNext())
            {
                Model currentM = it.next();
                Future<Model> f_trained = sendEvent(new TrainModelEvent(currentM));
                currentM = f_trained.get();
                Future <Model> f_tested = sendEvent(new TestModelEvent(currentM));
                currentM = f_tested.get();
                sendEvent(new PublishResultEvent(currentM));
            }
        });
        t.setDaemon(true);
        t.start();

    }
}
