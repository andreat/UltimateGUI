package UltimateGUI.util;

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