import java.util.*;

public class Main {
    static int clk = 1;
    static Memory memory;
    static FileOfRegisters fileOfRegisters;
    static ArrayList<Instruction> setOfInstructions = new ArrayList<Instruction>();
    static int pointerCache = 0;
    static Queue<Instruction> queueInstructions;
    static ReservationArea[] MultiplyStation;
    static ReservationArea[] AddStation;
    static LoadBuffer[] LoadStation;
    static StoreBuffer[] StoreStation;
    private static int latencySub;
    private static int latencyAddi;
    private static int latencyStore;
    private static int latencyLoad;
    private static int latencyAdd;
    private static int latencyMul;
    private static int latencyDiv;
    private static int latencyBranch;

    public Main() {
        memory = new Memory();
        fileOfRegisters = new FileOfRegisters();
        queueInstructions = new LinkedList<Instruction>();
    }

    public static void fetch() {
        // for (int i = 0; i < memory.counter; i++) {
        // String part = memory.memory[i];
        // String[] parts = part.split(" ");
        // Instruction instruction = new Instruction(parts[0], parts[1], parts[2],
        // parts[3]);
        // cache.add(instruction);
        // instruction.issue = i + 1;
        // }
        if (pointerCache < memory.counter) {
            String part = memory.memory[pointerCache];
            String[] parts = part.split(" ");
            Instruction instruction = new Instruction(parts[0], parts[1], parts[2], parts[3]);
            setOfInstructions.add(instruction);
            queueInstructions.add(instruction);
            pointerCache++;
        }
    }

    public static void issue() {
        // Instruction instruction = cache.get(pointerCache - 1);
        Instruction instruction = queueInstructions.peek();
        instruction.issue = pointerCache;
        // for (int i = 0; i < cache.size(); i++) {
        // if (instruction.inExecution == false) {
        boolean isIssued = false;
        if (instruction.opcode.equals("L.D")) {
            for (int i = 0; i < LoadStation.length; i++) {
                if (LoadStation[i] == null) {
                    LoadStation[i] = new LoadBuffer(instruction.instructionId, "L" + i, 1, instruction.src1);
                    isIssued = true;
                    break;
                } else if (LoadStation[i].getBusy() == 0) {
                    LoadStation[i].setBusy(1);
                    LoadStation[i].setAddress(instruction.src1);
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.dest.equals(fileOfRegisters.get(j).getName())) {
                            fileOfRegisters.get(j).setQueue("L" + i);
                        }
                    }
                    isIssued = true;
                    break;
                }
            }
        } else if (instruction.opcode.equals("S.D")) {
            // es2l fel MIPS code bt3ha S.D R1 Address?
            for (int i = 0; i < StoreStation.length; i++) {
                if (StoreStation[i] == null) {
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.dest.equals(fileOfRegisters.get(j).getName())) {
                            if (fileOfRegisters.get(j).getQueue().equals("0")) { // check if register available?
                                StoreStation[i] = new StoreBuffer(instruction.instructionId, "S" + i, 1,
                                        instruction.src1,
                                        fileOfRegisters.get(j).getValue(), "0");
                                fileOfRegisters.get(j).setQueue("S" + i);
                                isIssued = true;
                            } else {// check if register not available?
                                StoreStation[i] = new StoreBuffer(instruction.instructionId, "S" + i, 1,
                                        instruction.src1, -1,
                                        fileOfRegisters.get(j).getQueue());
                                isIssued = true;
                            }
                            break;
                        }
                    }
                } else if (StoreStation[i].getBusy() == 0) {
                    StoreStation[i].setBusy(1);
                    StoreStation[i].setAddress(instruction.src1);
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.dest.equals(fileOfRegisters.get(j).getName())) {
                            if (fileOfRegisters.get(j).getQueue().equals("0")) { // check if register available?
                                StoreStation[i].setValue(fileOfRegisters.get(j).getValue());
                                StoreStation[i].setQueue("0");
                                fileOfRegisters.get(j).setQueue("S" + i);
                                isIssued = true;
                            } else {// check if register not available?
                                StoreStation[i].setQueue(fileOfRegisters.get(j).getQueue());
                                isIssued = true;
                            }
                        }
                    }
                    break;
                }
            }
        } else if (instruction.opcode.equals("ADD.D") || instruction.opcode.equals("SUB.D")) {
            for (int i = 0; i < AddStation.length; i++) {
                if (AddStation[i] == null) {
                    float srcVal1 = 0;
                    float srcVal2 = 0;
                    String srcQue1 = "0";
                    String srcQue2 = "0";
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.src1.equals(fileOfRegisters.get(j).getName())) {
                            if (!fileOfRegisters.get(j).getQueue().equals("0")) {
                                srcQue1 = fileOfRegisters.get(j).getQueue();
                            } else {
                                srcVal1 = fileOfRegisters.get(j).getValue();
                            }
                        }
                        if (instruction.src2.equals(fileOfRegisters.get(j).getName())) {
                            if (!fileOfRegisters.get(j).getQueue().equals("0")) {
                                srcQue2 = fileOfRegisters.get(j).getQueue();
                            } else {
                                srcVal2 = fileOfRegisters.get(j).getValue();
                            }
                        }
                    }
                    AddStation[i] = new ReservationArea(instruction.instructionId, "A" + i, 1, instruction.opcode,
                            srcVal1, srcVal2, srcQue1, srcQue2);
                    fileOfRegisters.get(i).setQueue("A" + i);
                    isIssued = true;
                    break;
                } else if (AddStation[i].busy == 0) {
                    AddStation[i].busy = 1;

                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                            if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                AddStation[i].value_j = Float.parseFloat(instruction.src1);
                                AddStation[i].queue_j = "0";
                            } else {
                                AddStation[i].queue_j = fileOfRegisters.get(k).getQueue();
                            }
                        }
                        if (instruction.src2.equals(fileOfRegisters.get(k).getName())) {
                            if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                AddStation[i].value_k = Float.parseFloat(instruction.src2);
                                AddStation[i].queue_k = "0";
                            } else {
                                AddStation[i].queue_k = fileOfRegisters.get(k).getQueue();
                            }
                        }
                    }
                    fileOfRegisters.get(i).setQueue("A" + i);
                    isIssued = true;
                    break;
                }
            }
        } else if (instruction.opcode.equals("MUL.D") || instruction.opcode.equals("DIV.D")) {
            for (int i = 0; i < MultiplyStation.length; i++) {
                if (MultiplyStation[i] == null) {
                    float srcVal1 = 0;
                    float srcVal2 = 0;
                    String srcQue1 = "0";
                    String srcQue2 = "0";
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.src1.equals(fileOfRegisters.get(j).getName())) {
                            if (!fileOfRegisters.get(j).getQueue().equals("0")) {
                                srcQue1 = fileOfRegisters.get(j).getQueue();
                            } else {
                                srcVal1 = fileOfRegisters.get(j).getValue();
                            }
                        }
                        if (instruction.src2.equals(fileOfRegisters.get(j).getName())) {
                            if (!fileOfRegisters.get(j).getQueue().equals("0")) {
                                srcQue2 = fileOfRegisters.get(j).getQueue();
                            } else {
                                srcVal2 = fileOfRegisters.get(j).getValue();
                            }
                        }
                    }
                    MultiplyStation[i] = new ReservationArea(instruction.instructionId, "M" + i, 1, instruction.opcode,
                            srcVal1, srcVal2, srcQue1, srcQue2);
                    fileOfRegisters.get(i).setQueue("M" + i);
                    isIssued = true;
                    break;
                } else if (MultiplyStation[i].busy == 0) {
                    MultiplyStation[i].busy = 1;
                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                            if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                MultiplyStation[i].value_j = Float.parseFloat(instruction.src1);
                                MultiplyStation[i].queue_j = "0";
                            } else {
                                MultiplyStation[i].queue_j = fileOfRegisters.get(k).getQueue();
                            }
                        }
                        if (instruction.src2.equals(fileOfRegisters.get(k).getName())) {
                            if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                MultiplyStation[i].value_k = Float.parseFloat(instruction.src2);
                                MultiplyStation[i].queue_k = "0";
                            } else {
                                MultiplyStation[i].queue_k = fileOfRegisters.get(k).getQueue();
                            }
                        }
                    }
                    fileOfRegisters.get(i).setQueue("M" + i);
                    isIssued = true;
                    break;
                }
            }
        }
        if (isIssued) {
            queueInstructions.remove();
        }
        // }
        // }
    }

    public static void execute() {
        for (int i = 0; i < LoadStation.length; i++) {
            if (LoadStation[i].getBusy() == 1) {
                for (int j = 0; j < setOfInstructions.size(); j++) {
                    if (setOfInstructions.get(j).instructionId == LoadStation[i].getInstructionId()) {
                        setOfInstructions.get(j).startExec = Main.clk;
                        setOfInstructions.get(j).endExec = Main.clk + latencyLoad - 1;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < StoreStation.length; i++) {
            if (StoreStation[i].getBusy() == 1) {
                for (int j = 0; j < setOfInstructions.size(); j++) {
                    if (setOfInstructions.get(j).instructionId == StoreStation[i].getInstructionId()) {
                        if (StoreStation[i].getQueue().equals("0")) {
                            setOfInstructions.get(j).startExec = Main.clk;
                            setOfInstructions.get(j).endExec = Main.clk + latencyStore - 1;
                        }
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < AddStation.length; i++) {
            if (AddStation[i].busy == 1) {
                if (AddStation[i].getQueue_j().equals("0") &&
                        AddStation[i].getQueue_k().equals("0")) {
                    if (AddStation[i].opcode.equals("ADD.D")) {
                        // float result = 0;
                        // result = AddStation[i].value_j + AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyAdd - 1;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("SUB.D")) {
                        // float result = 0;
                        // result = AddStation[i].value_j - AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencySub - 1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < MultiplyStation.length; i++) {
            if (MultiplyStation[i].busy == 1) {
                if (MultiplyStation[i].getQueue_j().equals("0") &&
                        MultiplyStation[i].getQueue_k().equals("0")) {
                    if (MultiplyStation[i].opcode.equals("MUL.D")) {
                        // float result = 0;
                        // result = MultiplyStation[i].value_j * MultiplyStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == MultiplyStation[i].getInstructionId()) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyMul - 1;
                                break;
                            }
                        }
                    } else if (MultiplyStation[i].opcode.equals("DIV.D")) {
                        // float result = 0;
                        // result = MultiplyStation[i].value_j / MultiplyStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == MultiplyStation[i].getInstructionId()) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyDiv - 1;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // public static void write() {

    //     for (int i = 0; i < setOfInstructions.size(); i++) {
    //         if (cache.get(i).endExec < Main.clk && cache.get(i).writeResultClock == -1) {
    //             cache.get(i).writeResultClock = Main.clk;
    //             for (int j = 0; j < AddStation.length; j++) {
    //                 if (AddStation[j].getInstructionId() == cache.get(i).instructionId) {
    //                     AddStation[j].busy = 0;
    //                     break;
    //                 }
    //             }
    //             for (int j = 0; j < MultiplyStation.length; j++) {
    //                 if (MultiplyStation[j].getInstructionId() == cache.get(i).instructionId) {
    //                     MultiplyStation[j].busy = 0;
    //                     break;
    //                 }
    //             }
    //             for (int j = 0; j < fileOfRegisters.size(); j++) {
    //                 if (fileOfRegisters.get(j).getName().equals(cache.get(i).dest)) {
    //                     fileOfRegisters.get(j).setQueue("0");
    //                     break;
    //                 }
    //             }
    //             cache.remove(i);
    //             break;
    //         }
    //         // if (cache.get(j).instructionId == AddStation[i].getInstructionId()) {
    //         // cache.get(j).writeResultClock = Main.clk;
    //         // break;
    //         // }
    //     }
    //     // for (int i = 0; i < LoadStation.length; i++) {
    //     // if (LoadStation[i].busy == 1) {
    //     // for (int j = 0; j < cache.size(); j++) {
    //     // if (cache.get(j).instructionId == LoadStation[i].getInstructionId()) {
    //     // cache.get(j).write = Main.clk;
    //     // break;
    //     // }
    //     // }
    //     // LoadStation[i].busy = 0;
    //     // }
    //     // }
    //     // for (int i = 0; i < StoreStation.length; i++) {
    //     // if (StoreStation[i].busy == 1) {
    //     // for (int j = 0; j < cache.size(); j++) {
    //     // if (cache.get(j).instructionId == StoreStation[i].getInstructionId()) {
    //     // cache.get(j).write = Main.cl
    //     // }}
    // }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter the Multiply Station number");
            int multiplyNumber = sc.nextInt();
            System.out.println("Enter the Add Station number");
            int addNumber = sc.nextInt();
            System.out.println("Enter the Load Station number");
            int loadNumber = sc.nextInt();
            System.out.println("Enter the Store Station number");
            int storeNumber = sc.nextInt();
            System.out.println("Enter the latency of Mul");
            latencyMul = sc.nextInt();
            System.out.println("Enter the latency of Add");
            latencyAdd = sc.nextInt();
            System.out.println("Enter the latency of Load");
            latencyLoad = sc.nextInt();
            System.out.println("Enter the latency of Store");
            latencyStore = sc.nextInt();

            latencyBranch = 1;

            System.out.println("Enter the latency of Div");
            latencyDiv = sc.nextInt();
            System.out.println("Enter the latency of Sub");
            latencySub = sc.nextInt();
            System.out.println("Enter the latency of Addi");
            latencyAddi = 1;

            Main main = new Main();

            MultiplyStation = new ReservationArea[multiplyNumber];
            AddStation = new ReservationArea[addNumber];
            LoadStation = new LoadBuffer[loadNumber];
            StoreStation = new StoreBuffer[storeNumber];
        }
    }
}
