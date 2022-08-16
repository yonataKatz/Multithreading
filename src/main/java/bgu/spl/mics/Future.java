package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */

/**
 * @inv - (?)
 */
public class Future<T> {

	private boolean isResolved;
	private T result;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		//TODO: implement this
		isResolved=false;
		result=null;
	}

	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */

	/**
	 * @pre - isResolved == True | isResolved == False
	 * @post - Future is resolved
	 */
	public synchronized T get() {
		while(!isDone())
			try{
				this.wait();
			}catch (Exception e){}
		return result;
	}


	/**
     * Resolves the result of this Future object.
     */

	/**
	 * @pre - isResolved == isDone == false
	 * @pre - result ==null
	 * @post - isResolved == isDone == true
	 * @post result !=null
	 */
	public synchronized void resolve (T res) {
		result=res;
		isResolved=true;
		notifyAll();
	}



	/**
     * @return true if this object has been resolved, false otherwise
     */

	/**
	 * @pre - (?)
	 * @post -isDone return == isResolved
	 */
	public boolean isDone() {
		return isResolved;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */

	/**
	 * @pre - isResolved == True | isResolved == False
	 * @post - return == result | return == null if timeout is done
	 */
	public T get(long timeout, TimeUnit unit) {
		if(isResolved)
			return result;
		try{
			wait(unit.toMillis(timeout));
		}catch (Exception e){}
		return result;
	}



}
