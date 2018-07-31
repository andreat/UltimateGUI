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

public class _ExitCode {
    private final static String[] _exit_codes = {"SUCCESS", "FAIL_OPEN_SUBPROCESS", "FAIL_NO_INPUT_FILE", "FAIL_NO_WITNESS_TO_VALIDATE",
                   "FAIL_MULTIPLE_FILES", "FAIL_NO_TOOLCHAIN_FOUND", "FAIL_NO_SETTINGS_FILE_FOUND",
                   "FAIL_ULTIMATE_ERROR"};

    public int __getattr__(String name) {
    	for (int i = 0, _exit_codes_length = _ExitCode._exit_codes.length; i < _exit_codes_length; i++) {
    		if (_ExitCode._exit_codes[i].equals(name)) {
    			return i;
    		}
    	}
    	throw new IllegalArgumentException("Exit code " + name + " not found");
    }
}
