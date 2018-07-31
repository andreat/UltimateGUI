package UltimateGUI.util;

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