package UltimateGUI.util;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameFilterPattern implements FilenameFilter {
	private final String ending;
	private final String[] contained;

	public FilenameFilterPattern(String ending, String ... contained) {
		this.ending = ending;
		this.contained = contained;
	}

	@Override
    public boolean accept(File dir, String name) {
    	boolean found = name.endsWith(ending);
    	if (! found) 
    		return found;
    	for (String in : contained) {
    		found &= name.contains(in);
    	}
    	return found;
    }
}
