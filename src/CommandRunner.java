import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CommandRunner implements Runnable {

	

	private String command = "";
	private String output = "";
	private int timeLimit = 0; // No time limit
	private Process commandProcess = null;
	
	public void CommandRunner(String command, int timeLimit) {
		this.command = command;
		this.timeLimit = timeLimit;
	}
	
	@Override
	public void run() {

		try {
		
			Runtime r = Runtime.getRuntime();
			commandProcess = r.exec(command);
			commandProcess.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getOutput() {
		if (output.equals("")) {
			StringBuffer outBuf = new StringBuffer();
			BufferedReader b = new BufferedReader(new InputStreamReader(commandProcess.getInputStream()));
			String line = null;
		
			try {
				while ((line = b.readLine()) != null) {
				  outBuf.append(line);
				  outBuf.append("\n");
				}
				output = outBuf.toString();
				b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Process getCommandProcess() {
		return commandProcess;
	}

	public void setCommandProcess(Process commandProcess) {
		this.commandProcess = commandProcess;
	}
	
	
}
