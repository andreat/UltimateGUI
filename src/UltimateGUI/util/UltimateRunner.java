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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class UltimateRunner {
	
	public enum ARCHITECTURE {
		_32BIT ("32bit"),
		_64BIT ("64bit");

		private final String string;
		private ARCHITECTURE(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
	
	public enum ANALYSIS {
		REACHABILITY("Reach"),
		TERMINATION("Termination"),
		LTL("LTL");
		
		private final String string;
		private ANALYSIS(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
	
	public enum PRECISION {
		BITPRECISE("Bitvector"),
		DEFAULT("Default");

		private final String string;
		private PRECISION(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
	
	private final boolean enable_assertions = false;
	
	private final ANALYSIS analysis;
	private final File toolchain_file;
	private final File settings_file;
	private final List<String> command;
	
	private String result;
	private String result_msg;
	private boolean overapprox;
	private String ultimate_output;
	private String error_path;
	
	public UltimateRunner(File program, ARCHITECTURE architecture, ANALYSIS analysis, PRECISION precision) throws UltimateException {
		this.analysis = analysis;
		this.toolchain_file = search_config_dir(analysis.toString() + ".xml");
		this.settings_file = search_config_dir(".epf", analysis.toString(), architecture.toString(), "_" + precision.toString());
		command = new LinkedList<String>();
		command.add("java");
		command.add("-Dosgi.configuration.area=" + Constants.datadir + File.separator + "config");
		command.add("-Xmx12G");
		command.add("-Xms1G");
		if (enable_assertions) {
			command.add("-ea");
		}
		command.add("-jar");
		command.add(Constants.ultimatedir + File.separator + "plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar");
		command.add("-data");
		command.add(Constants.datadir);
		command.add("-tc");
		command.add(toolchain_file.getAbsolutePath());
		command.add("-i");
		command.add(program.getAbsolutePath());
		command.add("-s");
		command.add(settings_file.getAbsolutePath());
		command.add("--cacsl2boogietranslator.entry.function");
		command.add("main");
		command.add("--witnessprinter.witness.directory");
		command.add(Constants.witnessdir);
		command.add("--witnessprinter.witness.filename");
		command.add(Constants.witnessname);
		command.add("--witnessprinter.write.witness.besides.input.file");
		command.add("false");
		command.add("--witnessprinter.graph.data.specification");
		switch (analysis) {
		case REACHABILITY:
			command.add("CHECK( init(main()), LTL(G ! call(__VERIFIER_error())) )");
			break;
		case TERMINATION:
			command.add("CHECK( init(main()), LTL(F end) )");
			break;
		default:
			throw new IllegalArgumentException("Unknown requested analysis: " + analysis.toString()); 
		}
		command.add("--witnessprinter.graph.data.producer");
		command.add(Constants.toolname);
		command.add("--witnessprinter.graph.data.architecture");
		command.add(architecture.toString());
		command.add("--witnessprinter.graph.data.programhash");
		try {
			command.add(SHA1(program));
		} catch (Exception e) {
			throw new UltimateException("General error in computing SHA1SUM for the program", e);
		}		
	}
	
	public void execute() throws UltimateException {
		ProcessBuilder pb = new ProcessBuilder(command);
		Map<String, String> environment = pb.environment();
		String path = environment.get("PATH");
		environment.put("PATH", path + Constants.PATH_SEPARATOR + Constants.ultimatedir);
		pb.redirectErrorStream(true);
		Process ultimate_process;
		try {
			ultimate_process = pb.start();
		} catch (IOException e) {
			throw new UltimateException("General error in running Ultimate Automizer", e);
		}
		StreamGobbler ultimate_process_output = new StreamGobbler(ultimate_process.getInputStream());
		ultimate_process_output.start();
		
		while (true) {
			try {
				ultimate_process.waitFor();
				ultimate_process_output.join();
			} catch (InterruptedException ie) {
				continue;
			}
			break;
		}
		
	    result = "UNKNOWN";
	    result_msg = "NONE";
	    boolean reading_error_path = false;
	    overapprox = false;

	    // poll the output
	    StringBuilder ultimate_output_sb = new StringBuilder();
	    StringBuilder error_path_sb = new StringBuilder();
	    
	    for (String line : ultimate_process_output.getLines()) {	
	        if (reading_error_path)
	            error_path_sb.append(line).append(Constants.LINE_SEPARATOR);
	        ultimate_output_sb.append(line).append(Constants.LINE_SEPARATOR);
	        if (line.contains(Constants.unsupported_syntax_errorstring))
	            result = "ERROR: UNSUPPORTED SYNTAX";
	        else if (line.contains(Constants.incorrect_syntax_errorstring))
	            result = "ERROR: INCORRECT SYNTAX";
	        else if (line.contains(Constants.type_errorstring))
	            result = "ERROR: TYPE ERROR";
	        else if (line.contains(Constants.witness_errorstring))
	            result = "ERROR: INVALID WITNESS FILE";
            else if (line.contains(Constants.exception_errorstring)) {
	            result = "ERROR: " + line.substring(line.indexOf(Constants.exception_errorstring));
	            // hack to avoid errors with floats 
	            overapprox = true;
            }
	        if (!overapprox && contains_overapproximation_result(line)) {
	            result = "UNKNOWN: Overapproximated counterexample";
	            overapprox = true;
	        }
	        if (analysis == ANALYSIS.TERMINATION) {
	            result_msg = "TERM";
	            if (line.contains(Constants.termination_true_string))
	                result = Constants.TRUE;
	            if (line.contains(Constants.termination_false_string)) {
	                result = Constants.FALSE;
	                reading_error_path = true;
	            }
	            if (line.contains(Constants.termination_path_end))
	                reading_error_path = false;
		        } else if (analysis == ANALYSIS.LTL) {
		            result_msg = "valid-ltl";
		            if (line.contains(Constants.ltl_false_string)) {
		                result = Constants.FALSE;
		                reading_error_path = true;
		            }
		            if (line.contains(Constants.ltl_true_string))
		                result = Constants.TRUE;
		            if (line.contains(Constants.termination_path_end))
		                reading_error_path = false;
	        } else {
	            if (line.contains(Constants.safety_string) || line.contains(Constants.all_spec_string))
	                result = Constants.TRUE;
	            if (line.contains(Constants.unsafety_string))
	                result = Constants.FALSE;
	            if (line.contains(Constants.mem_deref_false_string))
	                result_msg = "valid-deref";
	            if (line.contains(Constants.mem_deref_false_string_2))
	                result_msg = "valid-deref";
	            if (line.contains(Constants.mem_free_false_string))
	                result_msg = "valid-free";
	            if (line.contains(Constants.mem_memtrack_false_string))
	                result_msg = "valid-memtrack";
	            if (line.contains(Constants.overflow_false_string)) {
	                result = Constants.FALSE;
	                result_msg = "OVERFLOW";
	            }
	            if (line.contains(Constants.error_path_begin_string))
	                reading_error_path = true;
	            if (reading_error_path && line.trim().isEmpty())
	                reading_error_path = false;
	        }
	    }

	    ultimate_output = ultimate_output_sb.toString();
	    error_path = error_path_sb.toString();
	}
	
	private boolean contains_overapproximation_result(String line) {
	    String[] triggers = {
	                "Reason: overapproximation of",
	                "Reason: overapproximation of bitwiseAnd",
	                "Reason: overapproximation of bitwiseOr",
	                "Reason: overapproximation of bitwiseXor",
	                "Reason: overapproximation of shiftLeft",
	                "Reason: overapproximation of shiftRight",
	                "Reason: overapproximation of bitwiseComplement"
	    };

        for (String trigger : triggers)
            if (line.contains(trigger))
                return true;
        return false;
	}
	
	public String getResult() {
		return result;
	}
	
	public boolean isResultFalse() {
		return result.equals(Constants.FALSE);
	}
	
	public boolean isResultTrue() {
		return result.equals(Constants.TRUE);
	}
	
	public String getResultMessage() {
		return result_msg;
	}
	
	public boolean isOverapproximation() {
		return overapprox;
	}
	
	public String getUltimateOutput() {
		return ultimate_output;
	}
	
	public String getErrorPath() {
		return error_path;
	}
	
	private String SHA1(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			byte[] buffer = new byte[8192];
	        int len = input.read(buffer);

	        while (len != -1) {
	            sha1.update(buffer, 0, len);
	            len = input.read(buffer);
	        }
	        return new HexBinaryAdapter().marshal(sha1.digest()).toLowerCase(Locale.ROOT);
		}
	}

	private File search_config_dir(String ending, String ... contained) {
		File[] listFiles = (new File(Constants.configdir)).listFiles(new FilenameFilterPattern(ending, contained));

		if (listFiles.length > 0) {
			return listFiles[0];
		}
		StringBuilder sb = new StringBuilder("No suitable file found in config dir ")
				.append(Constants.configdir)
				.append(" using search string ");
		for (String in : contained) {
			sb.append("*").append(in);
		}
		sb.append("*").append(ending);
		throw new IllegalArgumentException(sb.toString());
	}	
}
