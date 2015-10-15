import java.lang.*;

public class FlipFlop implements Runnable {
	int interations = 20;
	int sleepTime;
	String message;
	boolean toErr;

	public FlipFlop (int sleepTime, String message, boolean toErr) {
		this.sleepTime = sleepTime;
		this.message = message;
		this.toErr = toErr;
	}

	public void run() {
		try {
			for(int i = 0; i < interations; i++) {
				if (toErr) {
					System.err.println(message);
				} else {
					System.out.println(message);
				}
				Thread.sleep(sleepTime);
			}
		} catch(Exception e) {}
	}

	public static void main(String args[]) {
		int sleepTimeOne = Integer.parseInt(args[0]);
		int sleepTimeTwo = Integer.parseInt(args[1]);

		Thread t1 = new Thread(new FlipFlop(sleepTimeOne, "flip", false));
		Thread t2 = new Thread(new FlipFlop(sleepTimeTwo, "flop", true));

		t1.start();
		t2.start();
	}
}