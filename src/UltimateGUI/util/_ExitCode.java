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
