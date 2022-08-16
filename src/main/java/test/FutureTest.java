package test;
import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    //Object being tested;
    private static Future<Integer> f;

    @Before
    protected void setUp() throws Exception {
        f =new Future<Integer>();
    }

    @After
    protected void tearDown() throws Exception {
        f =null;
    }

    @Test
    public void get()
    {
        f =new Future<Integer>();
        assertTrue( !f.isDone());
        Thread t1 = new Thread(()->{
            f.resolve(1);
        });
        t1.start();
        Integer element= f.get();
        assertTrue(element!=null && element.equals(1));
        assertTrue(f.isDone());
    }

    @Test
    public void resolve()
    {
        f =new Future<Integer>();
        Integer result=1;
        assertFalse(f.isDone());
        f.resolve(result);
        assertTrue(f.isDone());
        Integer element=f.get();
        assertTrue(element.equals(result));
    }


    @Test
    public void isDone()
    {
        f =new Future<Integer>();
        assertFalse(f.isDone());
        f.resolve(1);
        assertTrue(f.isDone());
    }


    @Test
    public void get2()
    {
        long timeout = 50;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        f =new Future<Integer>();
        Thread t1 =new Thread(()->{
                try {
                    Thread.currentThread().sleep(2*timeout);
                }catch (Exception e){}
                f.resolve(1);
        });
        t1.start();
        Integer element=f.get(timeout,unit);
        assertTrue(element==null);
        try{
            t1.join();
        }catch (Exception e){}
        element=f.get(timeout,unit);
        assertTrue(element.equals(1));
    }
}