import java.util.*;

public class Main {
    static int clk = 1;
    static Memory memory;
    static FileOfRegisters fileOfRegisters;
    static ArrayList<Instruction> cache = new ArrayList<Instruction>();
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
            cache.add(instruction);
            queueInstructions.add(instruction);
            pointerCache++;
        }
    }

    public static void issue() {
        Instruction instruction = cache.get(pointerCache - 1);
        queueInstructions.remove();
        instruction.issue = pointerCache;
        // for (int i = 0; i < cache.size(); i++) {
        // if (instruction.inExecution == false) {
        if (instruction.opcode.equals("L.D")) {
            for (int j = 0; j < LoadStation.length; j++) {
                if (LoadStation[j].busy == 0) {
                    LoadStation[j].busy = 1;
                    LoadStation[j].Address = Integer.parseInt(instruction.src1);
                    // instruction.startExec = Main.clk;
                    break;
                }
            }
        } else if (instruction.opcode.equals("S.D")) {
            for (int j = 0; j < StoreStation.length; j++) {
                 if (StoreStation[j] == null) {
                    StoreStation[j] = new StoreBuffer(instruction.instructionId, "S" + j, instruction.opcode);
                }
                if (StoreStation[j].busy == 0) {
                    StoreStation[j].busy = 1;
                    StoreStation[j].Address = Integer.parseInt(instruction.src1)
                            + Integer.parseInt(instruction.src2);
                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                            if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                StoreStation[j].value = Float.parseFloat(instruction.src1);
                            }

                            // instruction.startExec = Main.clk;
                            break;
                        }
                    }
                }
            }
        } else if (instruction.opcode.equals("ADD.D") || instruction.opcode.equals("SUB.D")) {
            for (int j = 0; j < AddStation.length; j++) {
                if (AddStation[j] == null) {
                    AddStation[j] = new ReservationArea(instruction.instructionId, "A" + j, instruction.opcode,
                            0, 0, "0", "0", 0);
                }
                if (AddStation[j].busy == 0) {
                    AddStation[j].busy = 1;

                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.src1.equals(instruction.src2)) {
                            if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    AddStation[j].value_j = Float.parseFloat(instruction.src1);
                                    AddStation[j].value_k = Float.parseFloat(instruction.src2);
                                } else {
                                    AddStation[j].queue_j = fileOfRegisters.get(k).getQueue();
                                    AddStation[j].queue_k = fileOfRegisters.get(k).getQueue();
                                }
                            }
                        } else {
                            if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    AddStation[j].value_j = Float.parseFloat(instruction.src1);
                                } else {
                                    AddStation[j].queue_j = fileOfRegisters.get(k).getQueue();
                                }
                            }
                            if (instruction.src2.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    AddStation[j].value_k = Float.parseFloat(instruction.src2);
                                } else {
                                    AddStation[j].queue_k = fileOfRegisters.get(k).getQueue();
                                }
                            }
                        }
                    }
                    if (AddStation[j].getQueue_j().equals("0") &&
                            AddStation[j].getQueue_k().equals("0"))
                        instruction.startExec = Main.clk;
                    break;
                }
            }
        } else if (instruction.opcode.equals("MUL.D") || instruction.opcode.equals("DIV.D")) {
            for (int j = 0; j < MultiplyStation.length; j++) {
                if (MultiplyStation[j] == null) {
                    MultiplyStation[j] = new ReservationArea(instruction.instructionId, "M" + j,
                            instruction.opcode,
                            0, 0, "0", "0", 0);
                }
                if (MultiplyStation[j].busy == 0) {
                    MultiplyStation[j].busy = 1;
                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.src1.equals(instruction.src2)) {
                            if (instruction.src1.equals(fileOfRegisters.get(k).getName())
                                    && fileOfRegisters.get(k).getQueue().equals("0")) {
                                MultiplyStation[j].value_j = Float.parseFloat(instruction.src1);
                                MultiplyStation[j].value_k = Float.parseFloat(instruction.src2);
                            }
                        } else {
                            if (instruction.src1.equals(fileOfRegisters.get(k).getName())
                                    && fileOfRegisters.get(k).getQueue().equals("0")) {
                                MultiplyStation[j].value_j = Float.parseFloat(instruction.src1);
                            }
                            if (instruction.src2.equals(fileOfRegisters.get(k).getName())
                                    && fileOfRegisters.get(k).getQueue().equals("0")) {
                                MultiplyStation[j].value_k = Float.parseFloat(instruction.src2);
                            }
                        }
                    }
                    if (MultiplyStation[j].getQueue_j().equals("0") &&
                            MultiplyStation[j].getQueue_k().equals("0"))
                        instruction.startExec = Main.clk;
                    break;
                }
            }
        }
        // }
        // }
    }

    public static void execute() {
        for (int i = 0; i < AddStation.length; i++) {
            if (AddStation[i].busy == 1) {
                if (AddStation[i].getQueue_j().equals("0") &&
                        AddStation[i].getQueue_k().equals("0")) {
                    if (AddStation[i].opcode.equals("ADD.D")) {
                        float result = 0;
                        result = AddStation[i].value_j + AddStation[i].value_k;
                        for (int j = 0; j < cache.size(); j++) {
                            if (cache.get(j).instructionId == AddStation[i].getInstructionId()) {
                                cache.get(j).result = result;
                                cache.get(j).startExec = Main.clk;
                                cache.get(j).endExec = Main.clk + latencyAdd - 1;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("SUB.D")) {
                        float result = 0;
                        result = AddStation[i].value_j - AddStation[i].value_k;
                        for (int j = 0; j < cache.size(); j++) {
                            if (cache.get(j).instructionId == AddStation[i].getInstructionId()) {
                                cache.get(j).result = result;
                                cache.get(j).endExec = Main.clk + latencySub - 1;
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
                        float result = 0;
                        result = MultiplyStation[i].value_j * MultiplyStation[i].value_k;
                        for (int j = 0; j < cache.size(); j++) {
                            if (cache.get(j).instructionId == MultiplyStation[i].getInstructionId()) {
                                cache.get(j).result = result;
                                cache.get(j).startExec = Main.clk;
                                cache.get(j).endExec = Main.clk + latencyMul - 1;
                                break;
                            }
                        }
                    } else if (MultiplyStation[i].opcode.equals("DIV.D")) {
                        float result = 0;
                        result = MultiplyStation[i].value_j / MultiplyStation[i].value_k;
                        for (int j = 0; j < cache.size(); j++) {
                            if (cache.get(j).instructionId == MultiplyStation[i].getInstructionId()) {
                                cache.get(j).result = result;
                                cache.get(j).startExec = Main.clk;
                                cache.get(j).endExec = Main.clk + latencyDiv - 1;
                                break;
                            }
                        }
                    }
                }
            }
        }
        // for (int i = 0; i < LoadStation.length; i++) {
        // if (LoadStation[i].busy == 1) {
        // LoadStation[i].result = memory.memory[LoadStation[i].Address];
        // LoadStation[i].startExec = Main.clk;
        // }
        // }
        // for (int i = 0; i < StoreStation.length; i++) {
        // if (StoreStation[i].busy == 1) {
        // StoreStation[i].result = memory.memory[StoreStation[i].Address];
        // StoreStation[i].startExec = Main.clk;
        // }
        // }
    }

    public static void write() {

        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).endExec < Main.clk && cache.get(i).writeResultClock == -1) {
                cache.get(i).writeResultClock = Main.clk;
                for (int j = 0; j < AddStation.length; j++) {
                    if (AddStation[j].getInstructionId() == cache.get(i).instructionId) {
                        AddStation[j].busy = 0;
                        break;
                    }
                }
                for (int j = 0; j < MultiplyStation.length; j++) {
                    if (MultiplyStation[j].getInstructionId() == cache.get(i).instructionId) {
                        MultiplyStation[j].busy = 0;
                        break;
                    }
                }
                for (int j = 0; j < fileOfRegisters.size(); j++) {
                    if (fileOfRegisters.get(j).getName().equals(cache.get(i).dest)) {
                        fileOfRegisters.get(j).setQueue("0");
                        break;
                    }
                }
                cache.remove(i);
                break;
            }
            // if (cache.get(j).instructionId == AddStation[i].getInstructionId()) {
            // cache.get(j).writeResultClock = Main.clk;
            // break;
            // }
        }
        // for (int i = 0; i < LoadStation.length; i++) {
        // if (LoadStation[i].busy == 1) {
        // for (int j = 0; j < cache.size(); j++) {
        // if (cache.get(j).instructionId == LoadStation[i].getInstructionId()) {
        // cache.get(j).write = Main.clk;
        // break;
        // }
        // }
        // LoadStation[i].busy = 0;
        // }
        // }
        // for (int i = 0; i < StoreStation.length; i++) {
        // if (StoreStation[i].busy == 1) {
        // for (int j = 0; j < cache.size(); j++) {
        // if (cache.get(j).instructionId == StoreStation[i].getInstructionId()) {
        // cache.get(j).write = Main.cl
        // }}
    }

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
