package UltimateGUI.util;

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