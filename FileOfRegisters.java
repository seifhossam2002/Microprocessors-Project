import java.util.ArrayList;

public class FileOfRegisters extends ArrayList<Register> {
	public FileOfRegisters() {
		this.add(new Register("R0", 0, "0"));
		this.add(new Register("R1", 0, "0"));
		this.add(new Register("R2", 0, "0"));
		this.add(new Register("R3", 0, "0"));
		this.add(new Register("R4", 0, "0"));
		this.add(new Register("R5", 0, "0"));
		this.add(new Register("R6", 0, "0"));
		this.add(new Register("R7", 0, "0"));
		this.add(new Register("R8", 0, "0"));
		this.add(new Register("R9", 0, "0"));
		this.add(new Register("R10", 0, "0"));
		this.add(new Register("R11", 0, "0"));
		this.add(new Register("R12", 0, "0"));
		this.add(new Register("R13", 0, "0"));
		this.add(new Register("R14", 0, "0"));
		this.add(new Register("R15", 0, "0"));
		this.add(new Register("R16", 0, "0"));
		this.add(new Register("R17", 0, "0"));
		this.add(new Register("R18", 0, "0"));
		this.add(new Register("R19", 0, "0"));
		this.add(new Register("R20", 0, "0"));
		this.add(new Register("R21", 0, "0"));
		this.add(new Register("R22", 0, "0"));
		this.add(new Register("R23", 0, "0"));
		this.add(new Register("R24", 0, "0"));
		this.add(new Register("R25", 0, "0"));
		this.add(new Register("R26", 0, "0"));
		this.add(new Register("R27", 0, "0"));
		this.add(new Register("R28", 0, "0"));
		this.add(new Register("R29", 0, "0"));
		this.add(new Register("R30", 0, "0"));
		this.add(new Register("R31", 0, "0"));

		this.add(new Register("F0", 0, "0"));
		this.add(new Register("F1", 0, "0"));
		this.add(new Register("F2", 0, "0"));
		this.add(new Register("F3", 0, "0"));
		this.add(new Register("F4", 0, "0"));
		this.add(new Register("F5", 0, "0"));
		this.add(new Register("F6", 0, "0"));
		this.add(new Register("F7", 0, "0"));
		this.add(new Register("F8", 0, "0"));
		this.add(new Register("F9", 0, "0"));
		this.add(new Register("F10", 0, "0"));
		this.add(new Register("F11", 0, "0"));
		this.add(new Register("F12", 0, "0"));
		this.add(new Register("F13", 0, "0"));
		this.add(new Register("F14", 0, "0"));
		this.add(new Register("F15", 0, "0"));
		this.add(new Register("F16", 0, "0"));
		this.add(new Register("F17", 0, "0"));
		this.add(new Register("F18", 0, "0"));
		this.add(new Register("F19", 0, "0"));
		this.add(new Register("F20", 0, "0"));
		this.add(new Register("F21", 0, "0"));
		this.add(new Register("F22", 0, "0"));
		this.add(new Register("F23", 0, "0"));
		this.add(new Register("F24", 0, "0"));
		this.add(new Register("F25", 0, "0"));
		this.add(new Register("F26", 0, "0"));
		this.add(new Register("F27", 0, "0"));
		this.add(new Register("F28", 0, "0"));
		this.add(new Register("F29", 0, "0"));
		this.add(new Register("F30", 0, "0"));
		this.add(new Register("F31", 0, "0"));

		this.add(new Register("PC", 0, "0"));
	}

	public void setValue(int index, int value) {
		this.get(index).setValue(value);
	}

	public void setQueue(int index, String queue) {
		this.get(index).setQueue(queue);
	}
}