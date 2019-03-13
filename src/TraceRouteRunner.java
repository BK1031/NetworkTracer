
public class TraceRouteRunner extends CommandRunner {

	public static String getLastLine(String s) {
		if (s == null) return null;
		String[] parts = s.split("\n");
		return parts[parts.length-1];
	}
	public TraceRouteRunner(String target, int timeLimit) {
		StringBuffer cmd = new StringBuffer();
		cmd.append("traceroute -a -e -P ICMP ");
		cmd.append(target);
		setCommand(cmd.toString());
		setTimeLimit(timeLimit);
	}
	
	public boolean traceRouteSuccessful() {
		String lastLine = TraceRouteRunner.getLastLine(this.getOutput());
		return (lastLine.contains("[AS") && (getNumberOfNodes() > 1));
	}
	
	public int getNumberOfNodes() {
		String lastLine = TraceRouteRunner.getLastLine(this.getOutput());
		String[] parts = lastLine.split("  ");
		
		int nodes = 0;
		try {
			nodes = Integer.parseInt(parts[0].trim());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nodes;
	}
}
