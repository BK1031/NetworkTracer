import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LatencyApp {
	
	public static String SITES_FILE = "data/allsites.txt";
	
	
	public static String getDateAndTime() {
		String pattern = "MM/dd/yyyy HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		Date today = Calendar.getInstance().getTime();        
		return df.format(today);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		List<String> targets = new ArrayList<String>();
		File sites = new File(SITES_FILE);
		BufferedReader sitesReader = new BufferedReader(new FileReader(sites));
		String line = sitesReader.readLine();
		int count = 0;
		while (line != null) {
			if (count%10 ==0) {
				System.out.println("****** Reading next 10 targets from input data file");
			}
			targets.add(line);
			System.out.println(line);
			count++;
			if(count%10 ==0) {
				System.out.println("****** Running ping and traceroute on these sites");
				processSites(targets);
				targets.clear();
			}
			line = sitesReader.readLine();
		}
		sitesReader.close();
		System.out.println("****** Program Completed");
	}
	
	
	public static void processSites(List<String> targetSites) throws InterruptedException, IOException {
		String outputFile = "output.txt";
		String processOutFile = "processOut.txt";
		//String outputFile = "output" + getDateAndTime();
		File output = new File("data/" + outputFile);
		File processOut = new File("data/" + processOutFile);
		//output.createNewFile();
		System.out.println("Output File Path: " + output.getAbsolutePath());
		//BufferedWriter outWriter = new BufferedWriter(new FileWriter(outputFile, true));
		
		
		List<String> targets = targetSites;
		List<PingRunner> pingRunners = new ArrayList<PingRunner>();
		List<TraceRouteRunner> traceRouteRunners = new ArrayList<TraceRouteRunner>();
		ThreadGroup tg = new ThreadGroup("LatencyApp");
		ArrayList<Thread> pingExecThreads = new ArrayList<Thread>();
		ArrayList<Thread> traceRouteExecThreads = new ArrayList<Thread>();
		
		/*
		System.out.println("****** Reading targets from input data file");
		
		File sites = new File(SITES_FILE);
		BufferedReader sitesReader = new BufferedReader(new FileReader(sites));
		String line = sitesReader.readLine();
		while (line != null) {
			targets.add(line);
			System.out.println(line);
			line = sitesReader.readLine();
		}
		sitesReader.close();
		*/
		
		
		System.out.println("****** Total targets: " + targets.size());
		
		
		
		for (int i = 0; i< targets.size() ; i++) {
			System.out.println("****** Starting ping process for " + targets.get(i));
			PingRunner pr = new PingRunner(targets.get(i), 10000);
			pingRunners.add(pr);
			Thread pingThread = new Thread(tg, pr);
			pingExecThreads.add(pingThread);
			pingThread.start();
			
			System.out.println("****** Starting traceroute process for " + targets.get(i));
			TraceRouteRunner trr = new TraceRouteRunner(targets.get(i), 20000);
			traceRouteRunners.add(trr);
			Thread tracerouteThread = new Thread(tg, trr);
			traceRouteExecThreads.add(tracerouteThread);
			tracerouteThread.start();
			
			// Add 5 seconds delay between every batch of 10 ping and traceroute commands
			/*
			if (i>1 && i%10 ==0) {
				System.out.println("****** Waiting for 5 seconds");
				Thread.sleep(5000);
			}
			*/
		}
		
		
		System.out.println("****** Waiting for ping and traceroute processes to complete...");
		
		int elapsedSeconds = 0;
		while (tg.activeCount() != 0) {
			Thread.sleep(1000);
			elapsedSeconds++;
			
			/*
			if (elapsedSeconds > 120) {
				int activeCount = tg.activeCount();
				System.out.println("****** There are " + activeCount + " number threads still active");
				break;
			}
			*/
		}
		
		for(int i = 0; i<targets.size(); i++) {
			
			String target = targets.get(i);
			Thread pingThread = pingExecThreads.get(i);
			Thread traceRouteThread = traceRouteExecThreads.get(i);
			PingRunner pingRunner = pingRunners.get(i);
			TraceRouteRunner traceRouteRunner = traceRouteRunners.get(i);
			
			/*
			boolean pingCompleted = false;
			System.out.print("****** Results for target: " + target + " : ");
			if (pingThread.getState() == Thread.State.TERMINATED) {
				System.out.print("ping completed, ");
				pingCompleted = true;
			} else {
				System.out.print("ping did not complete, ");
			}
			
			boolean traceRouteCompleted = false;
			if (traceRouteThread.getState() == Thread.State.TERMINATED) {
				System.out.println("traceroute completed, ");
				traceRouteCompleted = true;
			} else {
				System.out.println("traceroute did not complete, ");
			}
			*/
			
			if (pingRunner.pingSuccessful() && traceRouteRunner.traceRouteSuccessful()) {
				//System.out.println(pingRunner.getOutput());
				//System.out.println(traceRouteRunner.getOutput());
				//System.out.println("****** Ping Latency: " + pingRunner.getAverageLatency());
				//System.out.println("****** Number of Nodes: " + traceRouteRunner.getNumberOfNodes());
				
				String entry = target + "," + pingRunner.getAverageLatency() + "," + traceRouteRunner.getNumberOfNodes() + "\n";
				Files.write(Paths.get(output.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
				entry = "****** " + target + " ******\n";
				Files.write(Paths.get(processOut.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
				entry = pingRunner.getOutput();
				Files.write(Paths.get(processOut.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
				entry = "\n---------------------------\n";
				Files.write(Paths.get(processOut.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
				entry = traceRouteRunner.getOutput();
				Files.write(Paths.get(processOut.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
				entry = "\n\n\n";
				Files.write(Paths.get(processOut.getAbsolutePath()), entry.getBytes(), StandardOpenOption.APPEND);
				
			}
			

		}
		
		//outWriter.close();
		
		
		/*
		//terminate any pending threads
		for(CommandRunner cr:runners) {
			cr.getCommandProcess().getInputStream().close();
			cr.getCommandProcess().destroy();
		}
		
		File output = new File("data/" + outputFile);
		if (!output.exists()) {
			output.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		
		for (int i=0; i< runners.size(); i++) {
			System.out.println(runners.get(i).getOutput());
			bw.write(runners.get(i).getOutput());
			
		}
		bw.close();
		*/
	}

}
