/*
 * Created on Oct 1, 2005
 *
 */
package ssmith.util;

/**
 * @author Stephen Smith
 *
 */
public class Interval {

	private long next_check_time, duration;

	public Interval(long duration) {
		super();
		this.duration = duration;
		this.next_check_time = System.currentTimeMillis(); // Fire straight away
	}
	
	public void restartTimer() {
		this.next_check_time = System.currentTimeMillis() + duration;
	}

	public void setInterval(long amt, boolean restart) {
		duration = amt;
		
		if (restart) {
			this.restartTimer();
		}
	}

	public boolean hitInterval() {
		if (System.currentTimeMillis() >= this.next_check_time) {
			//this.next_check_time = System.currentTimeMillis() + duration;
			this.restartTimer();
			return true;
		}
		return false;
	}
	
	public void fireInterval() {
		this.next_check_time = System.currentTimeMillis(); // Fire straight away
	}

}
