import java.io.*;

public class ForkFlipFlop {
	static String command = "java FlipFlop";

	public ForkFlipFlop(String argOne, String argTwo) {
		command += " " + argOne;
		command += " " + argTwo; 
	}


	public void runExternalProcess(String command) {
		ProcessBuilder newProcess = new ProcessBuilder(command);
	}

	public static void main(String args[]) {
		ForkFlipFlop forkflipflop = new ForkFlipFlop(args[0], args[1]);
		command = command.trim();
		// String[] tokens = command.split("[ \t\n]");

		// ProcessBuilder externalProcess = new ProcessBuilder(tokens);
		Process p = null;

		try {
			p = Runtime.getRuntime().exec(command);
			// p = externalProcess.start();
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
	}
}