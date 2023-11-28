public class Instruction {
    static int id = 1;
    int instructionId;
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
        this.issue = 0;
        this.startExec = 0;
        this.endExec = 0;
        this.writeResultClock = -1;
        this.result = 0f;
        this.inExecution = false;
    }

    public String toString() {
        return this.opcode + " " + this.dest + " " + this.src1 + " " + this.src2;
    }
}
