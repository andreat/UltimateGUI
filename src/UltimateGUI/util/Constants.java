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

import java.io.File;
import java.nio.file.Paths;

public class Constants {

	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String PATH = "PATH";
	public static final String PATH_SEPARATOR = File.pathSeparator;
	public static final String ULTIMATE_OS;
	static {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			ULTIMATE_OS = "UAutomizer-win32";
		} else {
			ULTIMATE_OS = "UAutomizer-linux";
		}
	}
	
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
	
	public static final String C_PROGRAM = "//C program";
	public static final String TAB = "    ";
	public static final String REACHABILITY_STATEMENT = "__VERIFIER_error();";
	
	public static final String version = "8e1d75aa";
	public static final String toolname = "Automizer";
	public static final String ultimatedir = Paths.get("").toAbsolutePath().toString() + File.separator + ULTIMATE_OS;
	public static final String configdir = ultimatedir + File.separator + "config";
	public static final String datadir = ultimatedir + File.separator + "data";
	public static final String witnessdir = ultimatedir;
	public static final String witnessname = "witness.graphml";
	
	// special strings in ultimate output
	public static final String unsupported_syntax_errorstring = "ShortDescription: Unsupported Syntax";
	public static final String incorrect_syntax_errorstring = "ShortDescription: Incorrect Syntax";
	public static final String type_errorstring = "Type Error";
	public static final String witness_errorstring = "InvalidWitnessErrorResult";
	public static final String exception_errorstring = "ExceptionOrErrorResult";
	public static final String safety_string = "Ultimate proved your program to be correct";
	public static final String all_spec_string = "AllSpecificationsHoldResult";
	public static final String unsafety_string = "Ultimate proved your program to be incorrect";
	public static final String mem_deref_false_string = "pointer dereference may fail";
	public static final String mem_deref_false_string_2 = "array index can be out of bounds";
	public static final String mem_free_false_string = "free of unallocated memory possible";
	public static final String mem_memtrack_false_string = "not all allocated memory was freed";
	public static final String termination_false_string = "Found a nonterminating execution for the following lasso shaped sequence of statements";
	public static final String termination_true_string = "TerminationAnalysisResult: Termination proven";
	public static final String ltl_false_string = "execution that violates the LTL property";
	public static final String ltl_true_string = "Buchi Automizer proved that the LTL property";
	public static final String error_path_begin_string = "We found a FailurePath:";
	public static final String termination_path_end = "End of lasso representation.";
	public static final String overflow_false_string = "overflow possible";
}
