import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class SystemCommands {

	public static void main(String[] args) {
		try {
			String pingCmd = "ping -c 10 www.cnn.com";
			String tracerouteCmd = "traceroute -a -e -P icmp www.cnn.com";
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);
			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
		
			while ((line = b.readLine()) != null) {
			  System.out.println(line);
			}
		
			p = r.exec(tracerouteCmd);
			p.waitFor();
			b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = "";
		
			while ((line = b.readLine()) != null) {
			  System.out.println(line);
			}
			
			b.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
