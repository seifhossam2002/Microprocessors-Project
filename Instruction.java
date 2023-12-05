public class Instruction {
    private static int id = 1;
    int instructionId;
    String reservationName;
    String opcode;
    String dest;
    String src1;
    String src2;
    int issue;
    int startExec;
    int endExec;
    int writeResultClock;
    float result;
    boolean inExecution;

    public Instruction(String opcode, String dest, String src1, String src2) {
        this.instructionId = id++;
        this.opcode = opcode;
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
        this.issue = -1;
        this.startExec = -1;
        this.endExec = -1;
        this.writeResultClock = -1;
        this.result = 0f;
        this.inExecution = false;
        this.reservationName = "";
    }

    public String toString() {
        return "Instruction: " + this.instructionId + " Opcode: " + this.opcode + " Dest: " + this.dest + " Src1: "
                + this.src1 + " Src2: "
                + this.src2 + " Issue: "
                + issue + " StartExec: " + startExec + " End Exec: "
                + endExec + " Write Result Clock: " + writeResultClock;
    }
}
