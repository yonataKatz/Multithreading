package test;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    //Object being tested
    MessageBusImpl mb;

    Integer element ;
    Future<Integer> future;

    @Before
    protected void setUp()
    {
        mb = MessageBusImpl.getInstance();
    }

    @After
    protected void tearDown()
    {
        mb=null;
    }


    protected class increaseInteger implements Event<Integer>
    {

        //fields
        Integer result;

        //constructor
        protected increaseInteger( )
        {
            result=0;
        }


    }
    protected class IntegerBroadcast implements Broadcast
    {}



    @Test
    public void subscribeEvent()
    {
         assertTrue(mb!=null);
         MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
         increaseInteger event= new increaseInteger();
         mb.register(m);
         mb.subscribeEvent(event.getClass(),m);
         assertTrue(mb.getSubscribes(event.getClass()).contains(m));
    }

    @Test
    public void subscribeBroadcast()
    {
        assertTrue(mb!=null);
        MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        mb.register(m);
        IntegerBroadcast b = new IntegerBroadcast();
        mb.subscribeBroadcast(b.getClass(),m);
        assertTrue(mb.getSubscribesB(b.getClass()).contains(m));
    }

    @Test
    public void complete()
    {
        assertFalse(mb==null);
        increaseInteger event = new increaseInteger();
        mb.complete(event,1);
        assertTrue(mb.getFutureFromMap(event).isDone());
    }

    @Test
    public void sendBroadcast()
    {
        assertFalse(mb==null);
        MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        mb.register(m);
        IntegerBroadcast b1 = new IntegerBroadcast();
        mb.subscribeBroadcast(b1.getClass(),m);
        mb.sendBroadcast(b1);
        try {
            Message m1 = mb.awaitMessage(m);
            assertTrue(m1.getClass()==IntegerBroadcast.class);
        }catch (Exception e){}
    }

    @Test
    public void sendEvent()
    {
        assertFalse(mb==null);
        MicroService m1 = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        mb.register(m1);
        increaseInteger event = new increaseInteger();
        mb.subscribeEvent(event.getClass(),m1);
        mb.sendEvent(event);
        try{
            Message m = mb.awaitMessage(m1);
            assertTrue(m.getClass()==increaseInteger.class);
        }catch (Exception e){}
    }


    @Test
    public void register()
    {
        MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        assertFalse(mb==null);
        mb.register(m);
        assertTrue(mb.getMicroServiceQueues(m)!=null);
    }

    @Test
    public void unregister()
    {
        MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        assertFalse(mb==null);
        mb.register(m);
        mb.unregister(m);
        assertFalse(mb.getMicroServiceQueues(m)!=null);
    }


    @Test
    public void awaitMessage() throws InterruptedException {
        assertFalse(mb==null);
        MicroService m = new StudentService("SS",new Student("Omer", "EE", "MsC"));
        TickBroadcast b = new TickBroadcast();
        try{
            mb.awaitMessage(m);
        }catch (InterruptedException e){   assertTrue(true);}
        mb.register(m);
        mb.subscribeBroadcast(b.getClass(),m);
        mb.sendBroadcast(b);
        assertTrue(mb.getMicroServiceQueues(m)== null);
    }
}