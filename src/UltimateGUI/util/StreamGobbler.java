/****************************************************************************

    UltimateGUI - A standalone GUI for Ultimate Automizer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 *****************************************************************************/
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
