import java.util.*;
import java.util.Queue;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class Main extends JFrame {
    private DefaultTableModel addSubTableModel;
    private DefaultTableModel mulDivTableModel;
    private DefaultTableModel loadBufferTableModel;
    private DefaultTableModel storeBufferTableModel;
    private DefaultTableModel registerRTableModel;
    private DefaultTableModel registerFTableModel;
    private DefaultTableModel instructionsTableModel;
    private DefaultTableModel inputsTableModel;
    private DefaultTableModel queuDefaultTableModel;
    private JTable addSubTable;
    private JTable mulDivTable;
    private JTable loadBufferTable;
    private JTable storeBufferTable;
    private JTable registerRTable;
    private JTable registerFTable;
    private JTable instructionsTable;
    private JTable inputsTable;
    private JTable queueTable;
    private JLabel cycleLabel;
    private JButton nextButton;
    private int currentCycle = 0;
    private static int inputValues = 0;

    static int clk = 1;
    static Memory memory;
    static FileOfRegisters fileOfRegisters;
    // deh ely boperate 3leeha
    static ArrayList<Instruction> setOfInstructions;
    // deh elmain table ely feeha kol 7aga 2deema w gdeeda
    static ArrayList<Instruction> tableOfInstructions;
    static int pointerCache = 0;
    static Queue<Instruction> queueInstructions;
    static ReservationArea[] MultiplyStation;
    static ReservationArea[] AddStation;
    static LoadBuffer[] LoadStation;
    static StoreBuffer[] StoreStation;
    static int latencyAddi;
    static int latencySubi;
    static int latencyStore;
    static int latencyLoad;
    static int latencyAddD;
    static int latencyDAdd;
    static int latencyMul;
    static int latencyDiv;
    static int latencyBranch;
    static boolean stall = false;

    public Main() {
        memory = new Memory();
        fileOfRegisters = new FileOfRegisters();
        queueInstructions = new LinkedList<Instruction>();
        setOfInstructions = new ArrayList<Instruction>();
        tableOfInstructions = new ArrayList<Instruction>();
        // initialize();

        initializeJavaFx();
    }

    // print the result but i need to call the fetch issue execute write while
    // keeping track of the Main.clk
    public static void printProcessorState() {
        do {
            System.out.println("CYCLE " + Main.clk);

            fetch();
            issue();
            execute();

            // 3lshan 2abl ma 2write lazem 2t2kd en el instruction msh f el execution
            updateExecution();

            write();

            System.out.println("Set of Instructions: ");
            System.out.println("[");
            for (int i = 0; i < tableOfInstructions.size(); i++) {
                System.out.println(tableOfInstructions.get(i));
            }
            System.out.println("]");
            System.out.println("Queue of Instructions: ");
            Queue<Instruction> temp = new LinkedList<Instruction>();
            while (!queueInstructions.isEmpty()) {
                System.out.println(queueInstructions.peek());
                System.out.println("-----------------------");
                temp.add(queueInstructions.remove());
            }
            while (!temp.isEmpty()) {
                queueInstructions.add(temp.remove());
            }
            // System.out.println("Load Reservation Stations: ");
            // for (int i = 0; i < LoadStation.length; i++) {
            // System.out.println(LoadStation[i]);
            // }
            // System.out.println("Store Reservation Stations: ");
            // for (int i = 0; i < StoreStation.length; i++) {
            // System.out.println(StoreStation[i]);
            // }
            // System.out.println("Add Reservation Stations: ");
            // for (int i = 0; i < AddStation.length; i++) {
            // System.out.println(AddStation[i]);
            // }
            // System.out.println("Multiply Reservation Stations: ");
            // for (int i = 0; i < MultiplyStation.length; i++) {
            // System.out.println(MultiplyStation[i]);
            // }
            // System.out.println("File of Registers: ");
            // for(int i=0;i<fileOfRegisters.size();i++){
            // System.out.println(fileOfRegisters.get(i));
            // }
            System.out.println("-------------------------------------------------");
            // for (int i = 0; i < MultiplyStation.length; i++) {
            // System.out.println(MultiplyStation[i]);
            // }
            // System.out.println("-------------------------------------------------");
            // for (int i = 0; i < AddStation.length; i++) {
            // System.out.println(AddStation[i]);
            // }
            System.out.println("-------------------------------------------------");

            Main.clk++;
        } while (!setOfInstructions.isEmpty() || pointerCache == 0);
    }

    public static void updateExecution() {
        for (int i = 0; i < setOfInstructions.size(); i++) {
            if (setOfInstructions.get(i).inExecution) {
                if (setOfInstructions.get(i).endExec < Main.clk) {
                    setOfInstructions.get(i).inExecution = false;
                }
            }
        }
    }

    public static void initialize() {
        for (int i = 0; i < LoadStation.length; i++) {
            LoadStation[i] = new LoadBuffer(-1, "L" + i, 0, null);
        }
        for (int i = 0; i < StoreStation.length; i++) {
            StoreStation[i] = new StoreBuffer(-1, "S" + i, 0, "0", 0, "0");
        }
        for (int i = 0; i < AddStation.length; i++) {
            AddStation[i] = new ReservationArea(-1, "A" + i, 0, "0", 0, 0, "0", "0");
        }
        for (int i = 0; i < MultiplyStation.length; i++) {
            MultiplyStation[i] = new ReservationArea(-1, "M" + i, 0, "0", 0, 0, "0", "0");
        }
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
        if (pointerCache < memory.counter && !stall) {
            String part = memory.memory[pointerCache];
            String[] parts = part.split(" ");
            Instruction instruction;

            int isLoop = 0;

            if (!(parts[0].equals("ADDI") || parts[0].equals("SUBI") || parts[0].equals("BNEZ")
                    || parts[0].equals("L.D")
                    || parts[0].equals("S.D") || parts[0].equals("MUL.D") || parts[0].equals("DIV.D")
                    || parts[0].equals("ADD.D") || parts[0].equals("SUB.D")
                    || parts[0].equals("DADD")))
                isLoop = 1;

            if (parts.length == 3 && isLoop == 0)
                instruction = new Instruction(parts[0], parts[1], parts[2], "");
            else if (parts.length == 4 && isLoop == 1)
                instruction = new Instruction(parts[isLoop + 0], parts[isLoop + 1], parts[isLoop + 2], "");
            else if (parts.length == 4 && isLoop == 0)
                instruction = new Instruction(parts[0], parts[1], parts[2], parts[3]);
            else
                instruction = new Instruction(parts[isLoop + 0], parts[isLoop + 1], parts[isLoop + 2],
                        parts[isLoop + 3]);

            tableOfInstructions.add(instruction);
            setOfInstructions.add(instruction);
            queueInstructions.add(instruction);
            pointerCache++;
        }
    }

    public static void issue() {
        // before I continue I need to check if the pointer is equal to the size of the
        // memory for null pointer exception
        // Instruction instruction = cache.get(pointerCache - 1);
        Instruction instruction = queueInstructions.peek();
        if (instruction == null) {
            return;
        }
        if (instruction.opcode.equals("BNEZ")) {
            stall = true;
        }
        boolean isIssued = false;
        int indexReservation = -1;
        if (instruction.opcode.equals("L.D")) {
            for (int i = 0; i < LoadStation.length; i++) {
                // if (LoadStation[i] == null) {
                // LoadStation[i] = new LoadBuffer(instruction.instructionId, "L" + i, 1,
                // instruction.src1);
                // isIssued = true;
                // break;
                // } else
                if (LoadStation[i].getBusy() == 0) {
                    LoadStation[i].setBusy(1);
                    LoadStation[i].setAddress(instruction.src1);
                    LoadStation[i].setInstructionId(instruction.instructionId);
                    indexReservation = i;
                    isIssued = true;
                    break;
                }
            }
        } else if (instruction.opcode.equals("S.D")) {
            // es2l fel MIPS code bt3ha S.D R1 Address?
            for (int i = 0; i < StoreStation.length; i++) {
                if (StoreStation[i].getBusy() == 0) {
                    StoreStation[i].setBusy(1);
                    StoreStation[i].setAddress(instruction.src1);
                    StoreStation[i].setInstructionId(instruction.instructionId);
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.dest.equals(fileOfRegisters.get(j).getName())) {
                            if (fileOfRegisters.get(j).getQueue().equals("0")) { // check if register available?
                                StoreStation[i].setValue(fileOfRegisters.get(j).getValue());
                                StoreStation[i].setQueue("0");
                                indexReservation = i;
                                isIssued = true;
                            } else {// check if register not available?
                                StoreStation[i].setQueue(fileOfRegisters.get(j).getQueue());
                                indexReservation = i;
                                isIssued = true;
                            }
                        }
                    }
                    break;
                }
            }
        } else if (instruction.opcode.equals("ADD.D") || instruction.opcode.equals("SUB.D")
                || instruction.opcode.equals("ADDI") || instruction.opcode.equals("SUBI")
                || instruction.opcode.equals("BNEZ") || instruction.opcode.equals("DADD")) {
            for (int i = 0; i < AddStation.length; i++) {
                if (AddStation[i].busy == 0) {
                    AddStation[i].busy = 1;
                    AddStation[i].instructionId = instruction.instructionId;
                    AddStation[i].opcode = instruction.opcode;

                    for (int k = 0; k < fileOfRegisters.size(); k++) {
                        if (instruction.opcode.equals("BNEZ")) {
                            if (instruction.dest.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    AddStation[i].value_j = fileOfRegisters.getValueRegister(instruction.dest);
                                    AddStation[i].queue_j = "0";
                                } else {
                                    AddStation[i].queue_j = fileOfRegisters.get(k).getQueue();
                                }
                            }

                        } else {
                            if (instruction.src1.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    AddStation[i].value_j = fileOfRegisters.getValueRegister(instruction.src1);
                                    AddStation[i].queue_j = "0";
                                } else {
                                    AddStation[i].queue_j = fileOfRegisters.get(k).getQueue();
                                }
                            }
                            if (instruction.src2.equals(fileOfRegisters.get(k).getName())) {
                                if (fileOfRegisters.get(k).getQueue().equals("0")) {
                                    if (instruction.opcode.equals("ADDI") || instruction.opcode.equals("SUBI"))
                                        AddStation[i].value_k = Float.parseFloat(instruction.src2);
                                    else
                                        AddStation[i].value_k = fileOfRegisters.getValueRegister(instruction.src2);
                                    AddStation[i].queue_k = "0";
                                } else {
                                    AddStation[i].queue_k = fileOfRegisters.get(k).getQueue();
                                }
                            }
                        }

                    }
                    // }
                    indexReservation = i;
                    isIssued = true;
                    break;
                }
            }
        } else if (instruction.opcode.equals("MUL.D") || instruction.opcode.equals("DIV.D")) {
            for (int i = 0; i < MultiplyStation.length; i++) {
                if (MultiplyStation[i].busy == 0) {
                    MultiplyStation[i].busy = 1;
                    MultiplyStation[i].instructionId = instruction.instructionId;
                    MultiplyStation[i].opcode = instruction.opcode;
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (instruction.src1.equals(fileOfRegisters.get(j).getName())) {
                            if (fileOfRegisters.get(j).getQueue().equals("0")) {
                                MultiplyStation[i].value_j = fileOfRegisters.get(j).getValue();
                                MultiplyStation[i].queue_j = "0";
                            } else {
                                MultiplyStation[i].queue_j = fileOfRegisters.get(j).getQueue();
                            }
                        }
                        if (instruction.src2.equals(fileOfRegisters.get(j).getName())) {
                            if (fileOfRegisters.get(j).getQueue().equals("0")) {
                                MultiplyStation[i].value_k = fileOfRegisters.get(j).getValue();
                                MultiplyStation[i].queue_k = "0";
                            } else {
                                MultiplyStation[i].queue_k = fileOfRegisters.get(j).getQueue();
                            }
                        }
                    }
                    indexReservation = i;
                    isIssued = true;
                    break;
                }
            }
        }

        if (isIssued) {
            instruction.issue = Main.clk;
            for (int i = 0; i < fileOfRegisters.size(); i++) {
                if (fileOfRegisters.get(i).getName().equals(instruction.dest)) {
                    String reservationString = "";
                    if (instruction.opcode.equals("S.D"))
                        reservationString = "S" + indexReservation;
                    else if (instruction.opcode.charAt(0) == 'S' || instruction.opcode.charAt(0) == 'A'
                            || instruction.opcode.charAt(0) == 'B')
                        reservationString = "A" + indexReservation;
                    else if (instruction.opcode.charAt(0) == 'L')
                        reservationString = "L" + indexReservation;
                    else if (instruction.opcode.charAt(0) == 'M' || instruction.opcode.charAt(0) == 'D')
                        reservationString = "M" + indexReservation;

                    instruction.reservationName = reservationString.charAt(0) + "";
                    if (instruction.opcode.equals("BNEZ"))
                        fileOfRegisters.get(i).setQueue("0");
                    else
                        fileOfRegisters.get(i).setQueue(reservationString);
                    break;
                }
            }
            queueInstructions.remove();
        }
        // }
        // }
    }

    public static void execute() {
        // ana 3aml fy kolo 7war eny 2shoof ana b3ml execution wala la2 3lshan lw msh
        // mwgood elcondition dah !setOfInstructions.get(j).inExecution byoverride
        // elstart exec w end exec
        for (int i = 0; i < LoadStation.length; i++) {
            if (LoadStation[i].getBusy() == 1) {
                for (int j = 0; j < setOfInstructions.size(); j++) {
                    if (setOfInstructions.get(j).instructionId == LoadStation[i].getInstructionId()
                            && !setOfInstructions.get(j).inExecution
                            && setOfInstructions.get(j).issue != Main.clk
                            && setOfInstructions.get(j).endExec == -1) {
                        setOfInstructions.get(j).startExec = Main.clk;
                        setOfInstructions.get(j).endExec = Main.clk + latencyLoad - 1;
                        setOfInstructions.get(j).inExecution = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < StoreStation.length; i++) {
            if (StoreStation[i].getBusy() == 1) {
                for (int j = 0; j < setOfInstructions.size(); j++) {
                    if (setOfInstructions.get(j).instructionId == StoreStation[i].getInstructionId()) {
                        if (StoreStation[i].getQueue().equals("0") && !setOfInstructions.get(j).inExecution
                                && setOfInstructions.get(j).issue != Main.clk
                                && setOfInstructions.get(j).endExec == -1) {
                            setOfInstructions.get(j).startExec = Main.clk;
                            setOfInstructions.get(j).endExec = Main.clk + latencyStore - 1;
                            setOfInstructions.get(j).inExecution = true;
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
                    if (AddStation[i].opcode.equals("ADD.D") || AddStation[i].opcode.equals("SUB.D")) {
                        // float result = 0;
                        // result = AddStation[i].value_j + AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyAddD - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("DADD")) {
                        // float result = 0;
                        // result = AddStation[i].value_j - AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyDAdd - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("ADDI")) {
                        // float result = 0;
                        // result = AddStation[i].value_j + AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyAddi - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("SUBI")) {
                        // float result = 0;
                        // result = AddStation[i].value_j - AddStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencySubi - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    } else if (AddStation[i].opcode.equals("BNEZ")) {
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == AddStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyBranch - 1;
                                setOfInstructions.get(j).inExecution = true;
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
                            if (setOfInstructions.get(j).instructionId == MultiplyStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyMul - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    } else if (MultiplyStation[i].opcode.equals("DIV.D")) {
                        // float result = 0;
                        // result = MultiplyStation[i].value_j / MultiplyStation[i].value_k;
                        for (int j = 0; j < setOfInstructions.size(); j++) {
                            if (setOfInstructions.get(j).instructionId == MultiplyStation[i].getInstructionId()
                                    && !setOfInstructions.get(j).inExecution
                                    && setOfInstructions.get(j).issue != Main.clk
                                    && setOfInstructions.get(j).endExec == -1) {
                                // setOfInstructions.get(j).result = result;
                                setOfInstructions.get(j).startExec = Main.clk;
                                setOfInstructions.get(j).endExec = Main.clk + latencyDiv - 1;
                                setOfInstructions.get(j).inExecution = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < setOfInstructions.size(); i++) {
            if (setOfInstructions.get(i).endExec == Main.clk && setOfInstructions.get(i).writeResultClock == -1
                    && setOfInstructions.get(i).startExec != -1) {

                for (int j = 0; j < fileOfRegisters.size(); j++) {
                    if (setOfInstructions.get(i).dest.equals(fileOfRegisters.get(j).getName())) {
                        if (setOfInstructions.get(i).opcode.equals("ADD.D")
                                || setOfInstructions.get(i).opcode.equals("DADD")) {
                            setOfInstructions.get(i).result = fileOfRegisters
                                    .getValueRegister(setOfInstructions.get(i).src1)
                                    + fileOfRegisters.getValueRegister(setOfInstructions.get(i).src2);
                        } else if (setOfInstructions.get(i).opcode.equals("BNEZ")) {
                            if (fileOfRegisters.getValueRegister(setOfInstructions.get(i).dest) != 0) {
                                pointerCache = 0;
                            }
                            stall = false;
                            for (int k = 0; k < AddStation.length; k++) {
                                if (AddStation[k].getInstructionId() == setOfInstructions.get(i).instructionId) {
                                    AddStation[k].setBusy(0);
                                    break;
                                }
                            }
                            setOfInstructions.remove(setOfInstructions.get(i));
                            if (i != 0)
                                i--;
                            if (setOfInstructions.isEmpty())
                                return;
                        }
                    } else if (setOfInstructions.get(i).opcode.equals("ADDI")) {
                        setOfInstructions.get(i).result = fileOfRegisters
                                .getValueRegister(setOfInstructions.get(i).src1)
                                + Float.parseFloat(setOfInstructions.get(i).src2);
                    } else if (setOfInstructions.get(i).opcode.equals("SUB.D")) {
                        setOfInstructions.get(i).result = fileOfRegisters
                                .getValueRegister(setOfInstructions.get(i).src1)
                                - fileOfRegisters.getValueRegister(setOfInstructions.get(i).src2);
                    } else if (setOfInstructions.get(i).opcode.equals("SUBI")) {
                        setOfInstructions.get(i).result = fileOfRegisters
                                .getValueRegister(setOfInstructions.get(i).src1)
                                - Float.parseFloat(setOfInstructions.get(i).src2);
                    } else if (setOfInstructions.get(i).opcode.equals("MUL.D")) {
                        setOfInstructions.get(i).result = fileOfRegisters
                                .getValueRegister(setOfInstructions.get(i).src1)
                                * fileOfRegisters.getValueRegister(setOfInstructions.get(i).src2);
                    } else if (setOfInstructions.get(i).opcode.equals("L.D")) {
                        for (int k = 0; k < LoadStation.length; k++) {
                            if (LoadStation[k].getInstructionId() == setOfInstructions.get(i).instructionId) {
                                setOfInstructions.get(i).result = Float.parseFloat(
                                        memory.memory[Integer.parseInt(LoadStation[k].getAddress()) + 1024]);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void write() {
        Instruction myInstruction;
        for (int i = 0; i < setOfInstructions.size(); i++) {
            if (setOfInstructions.get(i).endExec < Main.clk && setOfInstructions.get(i).writeResultClock == -1
                    && !setOfInstructions.get(i).inExecution && setOfInstructions.get(i).startExec != -1) {
                setOfInstructions.get(i).writeResultClock = Main.clk;
                myInstruction = setOfInstructions.get(i);
                // if (myInstruction.opcode.equals("BNEZ")) {
                // if (myInstruction.result == 1) {
                // pointerCache = 0;
                // }
                // setOfInstructions.remove(myInstruction);
                // return;
                // }
                for (int j = 0; j < fileOfRegisters.size(); j++) {
                    if (setOfInstructions.get(i).dest.equals(fileOfRegisters.get(j).getName())) {

                        if (!setOfInstructions.get(i).opcode.equals("S.D")) {
                            fileOfRegisters.get(j).setValue(setOfInstructions.get(i).result);
                            fileOfRegisters.get(j).setQueue("0");
                            break;
                        }
                    }
                }
                String instructionName = "";

                if (myInstruction.reservationName.charAt(0) == 'L') {
                    for (int j = 0; j < LoadStation.length; j++) {
                        if (LoadStation[j].getBusy() == 1) {
                            if (myInstruction.instructionId == LoadStation[j].getInstructionId()) {
                                LoadStation[j].setBusy(0);
                                instructionName = "L" + j;
                                break;
                            }
                        }
                    }
                    for (int j = 0; j < AddStation.length; j++) {
                        if (AddStation[j].getQueue_j().equals(instructionName)) {
                            AddStation[j].setQueue_j("0");
                            AddStation[j].setValue_j(myInstruction.result);
                        }
                        if (AddStation[j].getQueue_k().equals(instructionName)) {
                            AddStation[j].setQueue_k("0");
                            AddStation[j].setValue_k(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < MultiplyStation.length; j++) {
                        if (MultiplyStation[j].getQueue_j().equals(instructionName)) {
                            MultiplyStation[j].setQueue_j("0");
                            MultiplyStation[j].setValue_j(myInstruction.result);
                        }
                        if (MultiplyStation[j].getQueue_k().equals(instructionName)) {
                            MultiplyStation[j].setQueue_k("0");
                            MultiplyStation[j].setValue_k(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < StoreStation.length; j++) {
                        if (StoreStation[j].getQueue().equals(instructionName)) {
                            StoreStation[j].setQueue("0");
                            StoreStation[j].setValue(myInstruction.result);
                        }
                    }
                }

                if (myInstruction.reservationName.charAt(0) == 'S') {
                    float myValue = -1;
                    for (int j = 0; j < fileOfRegisters.size(); j++) {
                        if (myInstruction.dest.equals(fileOfRegisters.get(j).getName())) {
                            myValue = fileOfRegisters.get(j).getValue();
                            break;
                        }
                    }
                    memory.memory[Integer.parseInt(myInstruction.src1) + 1024] = myValue + "";
                    for (int j = 0; j < StoreStation.length; j++) {
                        if (StoreStation[j].getBusy() == 1) {
                            if (myInstruction.instructionId == StoreStation[j].getInstructionId()) {
                                StoreStation[j].setBusy(0);
                            }
                        }
                    }
                }

                if (myInstruction.reservationName.charAt(0) == 'A') {
                    for (int j = 0; j < AddStation.length; j++) {
                        if (AddStation[j].getBusy() == 1) {
                            if (myInstruction.instructionId == AddStation[j].getInstructionId()) {
                                AddStation[j].setBusy(0);
                                instructionName = "A" + j;
                            }
                        }
                    }
                    for (int j = 0; j < StoreStation.length; j++) {
                        if (StoreStation[j].getQueue().equals(instructionName)) {
                            StoreStation[j].setQueue("0");
                            StoreStation[j].setValue(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < AddStation.length; j++) {
                        if (AddStation[j].getQueue_j().equals(instructionName)) {
                            AddStation[j].setQueue_j("0");
                            AddStation[j].setValue_j(myInstruction.result);
                        }
                        if (AddStation[j].getQueue_k().equals(instructionName)) {
                            AddStation[j].setQueue_k("0");
                            AddStation[j].setValue_k(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < MultiplyStation.length; j++) {
                        if (MultiplyStation[j].getQueue_j().equals(instructionName)) {
                            MultiplyStation[j].setQueue_j("0");
                            MultiplyStation[j].setValue_j(myInstruction.result);
                        }
                        if (MultiplyStation[j].getQueue_k().equals(instructionName)) {
                            MultiplyStation[j].setQueue_k("0");
                            MultiplyStation[j].setValue_k(myInstruction.result);
                        }
                    }

                }

                if (myInstruction.reservationName.charAt(0) == 'M') {
                    for (int j = 0; j < MultiplyStation.length; j++) {
                        if (MultiplyStation[j].getBusy() == 1) {
                            if (myInstruction.instructionId == MultiplyStation[j].getInstructionId()) {
                                MultiplyStation[j].setBusy(0);
                                instructionName = "M" + j;
                            }
                        }
                    }
                    for (int j = 0; j < StoreStation.length; j++) {
                        if (StoreStation[j].getQueue().equals(instructionName)) {
                            StoreStation[j].setQueue("0");
                            StoreStation[j].setValue(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < AddStation.length; j++) {
                        if (AddStation[j].getQueue_j().equals(instructionName)) {
                            AddStation[j].setQueue_j("0");
                            AddStation[j].setValue_j(myInstruction.result);
                        }
                        if (AddStation[j].getQueue_k().equals(instructionName)) {
                            AddStation[j].setQueue_k("0");
                            AddStation[j].setValue_k(myInstruction.result);
                        }
                    }
                    for (int j = 0; j < MultiplyStation.length; j++) {
                        if (MultiplyStation[j].getQueue_j().equals(instructionName)) {
                            MultiplyStation[j].setQueue_j("0");
                            MultiplyStation[j].setValue_j(myInstruction.result);
                        }
                        if (MultiplyStation[j].getQueue_k().equals(instructionName)) {
                            MultiplyStation[j].setQueue_k("0");
                            MultiplyStation[j].setValue_k(myInstruction.result);
                        }
                    }
                }
                setOfInstructions.remove(i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Enter the Multiply Station number");
            // int multiplyNumber = sc.nextInt();
            int multiplyNumber = 1;
            System.out.println("Enter the Add Station number");
            // int addNumber = sc.nextInt();
            int addNumber = 1;
            System.out.println("Enter the Load Station number");
            // int loadNumber = sc.nextInt();
            int loadNumber = 3;
            System.out.println("Enter the Store Station number");
            // int storeNumber = sc.nextInt();
            int storeNumber = 3;
            System.out.println("Enter the latency of Mul");
            // latencyMul = sc.nextInt();
            latencyMul = 3;
            System.out.println("Enter the latency of Add.D or Sub.D");
            // latencyAdd = sc.nextInt();
            latencyAddD = 3;
            System.out.println("Enter the latency of DAdd");
            // latencyDAdd = sc.nextInt();
            latencyDAdd = 3;
            System.out.println("Enter the latency of Load");
            // latencyLoad = sc.nextInt();
            latencyLoad = 3;
            System.out.println("Enter the latency of Store");
            // latencyStore = sc.nextInt();
            latencyStore = 3;

            System.out.println("Enter the latency of Div");
            // latencyDiv = sc.nextInt();
            latencyDiv = 3;

            latencyAddi = 1;
            latencySubi = 1;
            latencyBranch = 1;

            MultiplyStation = new ReservationArea[multiplyNumber];
            AddStation = new ReservationArea[addNumber];
            LoadStation = new LoadBuffer[loadNumber];
            StoreStation = new StoreBuffer[storeNumber];

            Main main = new Main();
            // printProcessorState();
        }
    }

    private void initializeJavaFx() {
        // Set up the frame
        setTitle("Reservation Areas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600); // Adjusted size to accommodate the new tables

        // Initialize tables and models
        addSubTableModel = new DefaultTableModel(
                new Object[] { "ID", "Opcode", "Value_j", "Value_k", "Queue_j", "Queue_k", "Busy" }, 3);
        mulDivTableModel = new DefaultTableModel(
                new Object[] { "ID", "Opcode", "Value_j", "Value_k", "Queue_j", "Queue_k", "Busy" }, 2);
        loadBufferTableModel = new DefaultTableModel(new Object[] { "ID", "Reservation Area ID", "Address", "Busy" },
                3);
        storeBufferTableModel = new DefaultTableModel(
                new Object[] { "ID", "Reservation Area ID", "Address", "Value", "Queue", "Busy" }, 3);
        registerRTableModel = new DefaultTableModel(new Object[] { "Name", "Value", "Queue" }, 32); // 32 rows for
                                                                                                    // R0-R31
        registerFTableModel = new DefaultTableModel(new Object[] { "Name", "Value", "Queue" }, 32); // 32 rows for
                                                                                                    // F0-F31
        instructionsTableModel = new DefaultTableModel(
                new Object[] { "Opcode", "Dest", "Src1", "Src2", "Issue", "Start Exec", "End Exec",
                        "Write Result Clock", "Result" },
                0);

        queuDefaultTableModel = new DefaultTableModel(
                new Object[] { "Opcode", "Dest", "Src1", "Src2", "Issue", "Start Exec", "End Exec",
                        "Write Result Clock", "Result" },
                0);

        inputsTableModel = new DefaultTableModel(new Object[] { "Key", "Value" }, 10) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        addSubTable = new JTable(addSubTableModel);
        mulDivTable = new JTable(mulDivTableModel);
        loadBufferTable = new JTable(loadBufferTableModel);
        storeBufferTable = new JTable(storeBufferTableModel);
        registerRTable = new JTable(registerRTableModel);
        registerFTable = new JTable(registerFTableModel);
        instructionsTable = new JTable(instructionsTableModel);
        inputsTable = new JTable(inputsTableModel);
        queueTable = new JTable(queuDefaultTableModel);

        // Set grid color to black
        addSubTable.setGridColor(Color.BLACK);
        mulDivTable.setGridColor(Color.BLACK);
        loadBufferTable.setGridColor(Color.BLACK);
        storeBufferTable.setGridColor(Color.BLACK);
        registerRTable.setGridColor(Color.BLACK);
        registerFTable.setGridColor(Color.BLACK);
        instructionsTable.setGridColor(Color.BLACK);
        inputsTable.setGridColor(Color.BLACK);

        // Add tables to the frame
        JPanel panel = new JPanel(new GridLayout(4, 1));

        // Create table panels with preferred sizes
        panel.add(createTablePanel("Addition/Subtraction Table", addSubTable, 300, 150));
        panel.add(createTablePanel("Multiplication/Division Table", mulDivTable, 300, 100));
        panel.add(createTablePanel("Load Buffer Table", loadBufferTable, 300, 150));
        panel.add(createTablePanel("Store Buffer Table", storeBufferTable, 300, 150));
        panel.add(createTablePanel("Register R Table", registerRTable, 150, 200));
        panel.add(createTablePanel("Register F Table", registerFTable, 150, 200));
        panel.add(createTablePanel("Instructions Table", instructionsTable, 2000, 200));
        panel.add(createTablePanel("Inputs Table", inputsTable, 200, 200));
        panel.add(createTablePanel("Queue Table", queueTable, 200, 200));

        panel.repaint();
        panel.revalidate();

        // Add cycle label
        cycleLabel = new JLabel("Cycle: 0", SwingConstants.CENTER);
        cycleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(cycleLabel);

        // Add next and previous buttons
        nextButton = new JButton("Next Cycle");

        nextButton.addActionListener(e -> {
            currentCycle++;
            updateCycleLabel(currentCycle);
            fetch();
            issue();

            execute();

            // 3lshan 2abl ma 2write lazem 2t2kd en el instruction msh f el execution
            updateExecution();

            write();
            Main.clk++;
            updateInstructionTable();
            updateReservationAreaTables();
            updateRegisterTables();
            updateQueueTable();
            // nextButton.setEnabled(!Main.setOfInstructions.isEmpty());
        });

        JPanel buttonPanel = new JPanel();
        // buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel);

        add(panel);

        // Display the frame
        setVisible(true);

        // Populate Register R Table and Register F Table
        updateRegisterTables();

        populateInputTable();
    }

    private void updateQueueTable() {
        removeAllRows(queuDefaultTableModel);
        Queue<Instruction> temp = new LinkedList<>(Main.queueInstructions);
        for (int i = 0; i < queueInstructions.size(); i++) {
            queuDefaultTableModel.addRow(new Object[] {
                    queueInstructions.peek().opcode,
                    queueInstructions.peek().dest,
                    queueInstructions.peek().src1,
                    queueInstructions.peek().src2,
                    queueInstructions.peek().issue,
                    queueInstructions.peek().startExec,
                    queueInstructions.peek().endExec,
                    queueInstructions.peek().writeResultClock,
                    queueInstructions.peek().result
            });
            temp.add(queueInstructions.remove());
        }
        while (!temp.isEmpty()) {
            queueInstructions.add(temp.remove());
        }
    }

    private JPanel createTablePanel(String title, JTable table, int width, int height) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(width, height));

        tablePanel.add(scrollPane);
        return tablePanel;
    }

    // Method to add a row to the addition/subtraction table
    public void addAddSubReservationArea(ReservationArea reservationArea) {
        addSubTableModel.addRow(new Object[] {
                reservationArea.getInstructionId(),
                reservationArea.getOpcode(),
                reservationArea.getValue_j(),
                reservationArea.getValue_k(),
                reservationArea.getQueue_j(),
                reservationArea.getQueue_k(),
                reservationArea.getBusy()
        });
    }

    // Method to add a row to the multiplication/division table
    public void addMulDivReservationArea(ReservationArea reservationArea) {
        mulDivTableModel.addRow(new Object[] {
                reservationArea.getInstructionId(),
                reservationArea.getOpcode(),
                reservationArea.getValue_j(),
                reservationArea.getValue_k(),
                reservationArea.getQueue_j(),
                reservationArea.getQueue_k(),
                reservationArea.getBusy()
        });
    }

    // Method to add a row to the load buffer table
    public void addLoadBuffer(LoadBuffer loadBuffer) {
        loadBufferTableModel.addRow(new Object[] {
                loadBuffer.getInstructionId(),
                loadBuffer.getReservationAreaId(),
                loadBuffer.getAddress(),
                loadBuffer.getBusy()
        });
    }

    // Method to add a row to the store buffer table
    public void addStoreBuffer(StoreBuffer storeBuffer) {
        storeBufferTableModel.addRow(new Object[] {
                storeBuffer.getInstructionId(),
                storeBuffer.getReservationAreaId(),
                storeBuffer.getAddress(),
                storeBuffer.getValue(),
                storeBuffer.getQueue(),
                storeBuffer.getBusy()
        });
    }

    // Method to add a row to the instructions table
    public void addInstruction(Instruction instruction) {
        instructionsTableModel.addRow(new Object[] {
                // instruction.instructionId,
                // instruction.reservationName,
                instruction.opcode,
                instruction.dest,
                instruction.src1,
                instruction.src2,
                instruction.issue,
                instruction.startExec,
                instruction.endExec,
                instruction.writeResultClock,
                instruction.result,
                // instruction.inExecution
        });
    }

    // Method to populate Register R Table and Register F Table
    // private void populateRegisterTables() {
    // FileOfRegisters fileOfRegisters = new FileOfRegisters();
    // for (int i = 0; i < 32; i++) {
    // Register registerR = fileOfRegisters.get(i);
    // Register registerF = fileOfRegisters.get(i + 32);
    // registerRTableModel.setValueAt(registerR.getName(), i, 0);
    // registerRTableModel.setValueAt(registerR.getValue(), i, 1);
    // System.out.println(registerR.getValue());
    // registerRTableModel.setValueAt(registerR.getQueue(), i, 2);
    // registerFTableModel.setValueAt(registerF.getName(), i, 0);
    // registerFTableModel.setValueAt(registerF.getValue(), i, 1);
    // registerFTableModel.setValueAt(registerF.getQueue(), i, 2);
    // }
    // }

    private void populateInputTable() {
        inputsTableModel.setValueAt("Multiply Station number", 0, 0);
        inputsTableModel.setValueAt("Add Station number", 1, 0);
        inputsTableModel.setValueAt("Load Station number", 2, 0);
        inputsTableModel.setValueAt("Store Station number", 3, 0);
        inputsTableModel.setValueAt("latency of Mul", 4, 0);
        inputsTableModel.setValueAt("latency of Add.D or Sub.D", 5, 0);
        inputsTableModel.setValueAt("latency of DAdd", 6, 0);
        inputsTableModel.setValueAt("latency of Load", 7, 0);
        inputsTableModel.setValueAt("latency of Store", 8, 0);
        inputsTableModel.setValueAt("latency of Div", 9, 0);

        inputsTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            Object newValue = inputsTableModel.getValueAt(row, column);
            System.out.println("Cell updated - Row: " + row + ", Column: " + column + ", New Value: " + newValue);
            if (row == 0) {
                MultiplyStation = new ReservationArea[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 1) {
                AddStation = new ReservationArea[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 2) {
                LoadStation = new LoadBuffer[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 3) {
                StoreStation = new StoreBuffer[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 4) {
                latencyMul = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 5) {
                latencyAddD = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 6) {
                latencyDAdd = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 7) {
                latencyLoad = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 8) {
                latencyStore = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 9) {
                latencyDiv = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (inputValues == 10) {
                System.out.println("All inputs are set");
                initialize();
            }
        });
    }

    // Method to update the cycle label
    public void updateCycleLabel(int cycle) {
        cycleLabel.setText("Cycle: " + cycle);
    }

    // Method to update the instruction table
    public void updateInstructionTable() {
        // Clear existing rows in the table
        instructionsTableModel.setRowCount(0);

        // Iterate through the set of instructions and add each instruction to the table
        for (Instruction instruction : tableOfInstructions) {
            System.out.println(instruction);
            addInstruction(instruction);
        }
    }

    // Method to update the instruction queue table
    public void updateInstructionQueueTable() {
        // Implement this method based on the data structures and logic in your program
        // Queue<Instruction> temp = new LinkedList<Instruction>();
        // while (!queueInstructions.isEmpty()) {
        // System.out.println(queueInstructions.peek());
        // System.out.println("-----------------------");
        // temp.add(queueInstructions.remove());
        // }
        // while (!temp.isEmpty()) {
        // queueInstructions.add(temp.remove());
        // }
    }

    public void updateReservationAreaTables() {
        System.out.println("updateReservationAreaTables");

        // Remove all rows from each table
        removeAllRows(addSubTableModel);
        removeAllRows(mulDivTableModel);
        removeAllRows(loadBufferTableModel);
        removeAllRows(storeBufferTableModel);

        // Add new rows to each table
        for (int i = 0; i < Main.MultiplyStation.length; i++) {
            addMulDivReservationArea(Main.MultiplyStation[i]);
        }
        for (int i = 0; i < Main.AddStation.length; i++) {
            addAddSubReservationArea(Main.AddStation[i]);
        }
        for (int i = 0; i < Main.LoadStation.length; i++) {
            addLoadBuffer(Main.LoadStation[i]);
        }
        for (int i = 0; i < Main.StoreStation.length; i++) {
            addStoreBuffer(Main.StoreStation[i]);
        }
    }

    public void updateRegisterTables() {
        // Implement this method based on the data structures and logic in your program
        removeAllRows(registerRTableModel);
        removeAllRows(registerFTableModel);

        for (int i = 0; i < 32; i++) {
            Register registerR = fileOfRegisters.get(i);
            Register registerF = fileOfRegisters.get(i + 32);
            registerRTableModel.addRow(new Object[] {
                    registerR.getName(),
                    registerR.getValue(),
                    registerR.getQueue()
            });
            registerFTableModel.addRow(new Object[] {
                    registerF.getName(),
                    registerF.getValue(),
                    registerF.getQueue()
            });
        }
    }

    // Helper method to remove all rows from a table model
    private void removeAllRows(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

}