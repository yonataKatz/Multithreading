package test;

import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {
    //Object being tested
    private static GPU g ;
    private Cluster c;
    private Model m;

    @Before
    protected void setUp()
    {
        g = new GPU(GPU.Type.RTX3090);
    }

    @After
    protected void tearDown()
    {
        g=null;
        c=null;
    }

    @Test
    public void getType()
    {
        assertEquals(g.getType(), GPU.Type.RTX3090);
    }

    @Test
    public void getModel()
    {
        assertTrue(g.getModel()==null);
    }

    @Test
    public void getCluster()
    {
        assertEquals(g.getCluster(),c);
    }

    @Test
    public void ClockTick()
    {
        int x = g.getInternalClock();
        g.ClockTick();
        assertTrue(x+1<=g.getInternalClock());
    }

    @Test
    public void getTicksForABatch() {
        int x = g.getTicksForABatch();
        assertTrue(x>=1);

    }
        @Test
    public void  SendUnprocessedBatches()
    {
        assertFalse(g.getUnprocessedBatches().size()==0);
        int x=g.getUnprocessedBatches().size();
         g.sendUnprocessed();
        assertTrue(g.getUnprocessedBatches().size()<x);
    }


    @Test
    public void sendUnprocessed() {
        int x = g.getUnprocessedBatches().size();
        g.sendUnprocessed();
        assertTrue(g.getUnprocessedBatches().size()<=x);
    }


    @Test
    public void getProcessed()
    {
        g.getProcessed();
        assertTrue(g.getVRam().size()>=0);
        assertTrue(g.getCluster().getProcessed(g).size()>=0);
    }
}