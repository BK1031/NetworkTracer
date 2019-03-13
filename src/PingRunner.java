
public class PingRunner extends CommandRunner {
	
	public PingRunner(String target, int timeLimit) {
		StringBuffer cmd = new StringBuffer();
		cmd.append("ping -c 10 ");
		cmd.append(target);
		setCommand(cmd.toString());
		setTimeLimit(timeLimit);
	}
	
	public boolean pingSuccessful() {
		return (this.getOutput().contains("round-trip min/avg/max/stddev"));
	}
	
	public float getAverageLatency() {
		int lastLineIndex = this.getOutput().indexOf("round-trip min/avg/max/stddev");
		String lastLine = this.getOutput().substring(lastLineIndex);
		String[] parts = lastLine.split(" = ");
		String[] times = parts[1].split("/");
		return Float.parseFloat(times[1]);
	}
}
