package UltimateGUI.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class StreamGobbler extends Thread {
	private final InputStream is;
	private final List<String> lines;
	
    
    public StreamGobbler(InputStream is)
    {
        this.is = is;
        lines = new LinkedList<String>();
    }
    
    public void run()
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line=null;
            while ((line = br.readLine()) != null)
                lines.add(line);    
        } catch (IOException ioe) {
        }
    }
    
    public List<String> getLines() {
    	return lines;
    }
}
