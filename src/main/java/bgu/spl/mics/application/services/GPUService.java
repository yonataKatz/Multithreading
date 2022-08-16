package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class    GPUService extends MicroService {

    //fields
    private GPU gpu;
    private LinkedBlockingQueue<TrainModelEvent> manager;
    private int tickForBatch;
    private TrainModelEvent currentTrainModelEvent;


    public GPUService(String name ,GPU g) {
        super(name);
        gpu=g;
        manager = new LinkedBlockingQueue<>();
        tickForBatch = 0 ;
        currentTrainModelEvent = null;
    }

    @Override
    protected void initialize() {

        this.subscribeEvent(TrainModelEvent.class,(TrainModelEvent t)->{
            if (gpu.getModel()==null) {
                manager.add(t);
                currentTrainModelEvent = manager.remove();
                currentTrainModelEvent.getModel().setStatus(Model.status.Training);
                gpu.setModel(currentTrainModelEvent.getModel());
                gpu.DataToBatches();
                gpu.setToBeProcessed();
                gpu.sendUnprocessed();
                tickForBatch = gpu.getTicksForABatch();
            }
            else
                manager.add(t);
        });

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick)->{
            if (gpu.getModel()==null) {
                if (!manager.isEmpty()) {
                    currentTrainModelEvent = manager.remove();
                    Model mod =  currentTrainModelEvent.getModel();
                    mod.setStatus(Model.status.Training);
                    gpu.setModel(mod);
                    gpu.DataToBatches();
                    gpu.setToBeProcessed();
                    gpu.sendUnprocessed();
                    gpu.ClockTick();
                }
            } else
                {
                    gpu.ClockTick();
                    gpu.sendUnprocessed();
                    gpu.getProcessed();

                    if (gpu.getModel().getData().getProcessed()>= gpu.getModel().getData().size() & gpu.getCluster().getProcessed(gpu).isEmpty() & gpu.getVRam().isEmpty() )
                    {
                        gpu.getModel().setStatus(Model.status.Trained);
                        gpu.getCluster().getStatistics().addTrainedModels(gpu.getModel());
                        complete(currentTrainModelEvent, gpu.getModel());
                        gpu.setUnMatch();
                        currentTrainModelEvent=null;
                        gpu.setModel(null);
                    }
                    else
                    {
                        if (gpu.getInternalClock()>=tickForBatch & !gpu.getVRam().isEmpty()) {
                            gpu.getVRam().remove();
                            gpu.updateInternalClock(tickForBatch);
                        }
                    }
                }
            });


        this.subscribeEvent(TestModelEvent.class,(TestModelEvent t)->{
            int num = (int)(Math.random()*100);
            if (t.getStudent().getStatus()== Student.Degree.MSc)
            {
                if (num<=80)
                    t.getModel().setTests(Model.tests.Good);
                else
                    t.getModel().setTests(Model.tests.Bad);
            }
            else
            {
                if (num<=60)
                    t.getModel().setTests(Model.tests.Good);
                else
                    t.getModel().setTests(Model.tests.Bad);
            }
            t.getModel().setStatus(Model.status.Tested);
            complete(t,t.getModel());

        });

        this.subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast t)->{
            terminate();
        });
    }
}
