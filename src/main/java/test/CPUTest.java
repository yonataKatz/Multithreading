package test;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.DataBatch;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import java.util.concurrent.LinkedBlockingQueue;
import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    //Object being tested
    private CPU c ;
    private int cores=3;


    @Before
    protected void setUp()
    {
        c = new CPU(cores);
    }

    @After
    protected void tearDown()
    {
        c=null;
    }

    @Test
    public void getCoreNum()
    {
        assertEquals(cores,c.getCoreNum());
    }

    @Test
    public void getCluster()
    {
        Cluster c1 = Cluster.getInstance();
        assertEquals(c.getCluster(),c1);

    }

    @Test
    public void getCollection()
    {
        assertTrue(c.getCollection().isEmpty());
        LinkedBlockingQueue<DataBatch> c1 = new LinkedBlockingQueue<>();
        c.setCollection(c1);
        assertTrue(c.getCollection()==c1);

    }

    @Test
    public void setCollection()
    {
        LinkedBlockingQueue<DataBatch> c1 = new LinkedBlockingQueue<>();
        c.setCollection(c1);
        assertTrue(c.getCollection()==c1);
    }

    @Test
    public void getToCpu()
    {
        assertTrue(c.getCollection()!=null);
        int x = c.getCollection().size();
        c.getToCpu();
        assertTrue(c.getCollection().size()>=x);

    }


    @Test
    public void process()
    {
        int x = c.process();
        assertTrue(x>=0);
    }

    @Test
    public void sendBack(int x)
    {
        x=1;
        int size = c.getCollection().size();
        if (c.getCollection().size()>=1)
            c.sendBack(x);
        assertTrue(c.getCollection().size()+1 == size);

    }


}