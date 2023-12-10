import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class ReservationAreaGUI extends JFrame {

    private DefaultTableModel addSubTableModel;
    private DefaultTableModel mulDivTableModel;
    private DefaultTableModel loadBufferTableModel;
    private DefaultTableModel storeBufferTableModel;
    private DefaultTableModel registerRTableModel;
    private DefaultTableModel registerFTableModel;
    private DefaultTableModel instructionsTableModel;
    private DefaultTableModel inputsTableModel;
    private JTable addSubTable;
    private JTable mulDivTable;
    private JTable loadBufferTable;
    private JTable storeBufferTable;
    private JTable registerRTable;
    private JTable registerFTable;
    private JTable instructionsTable;
    private JTable inputsTable;
    private JLabel cycleLabel;
    private JButton nextButton;
    private int currentCycle = 0;
    private Main main;
    private static int inputValues = 0;

    public ReservationAreaGUI() {
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
            Main.fetch();
            Main.issue();
            Main.execute();

            // 3lshan 2abl ma 2write lazem 2t2kd en el instruction msh f el execution
            Main.updateExecution();

            Main.write();
            Main.clk++;
            updateInstructionTable();
            updateReservationAreaTables();
            updateRegisterTables();
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
        populateRegisterTables();

        populateInputTable();

        // Initialize the main class
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
    private void populateRegisterTables() {
        FileOfRegisters fileOfRegisters = new FileOfRegisters();
        for (int i = 0; i < 32; i++) {
            Register registerR = fileOfRegisters.get(i);
            Register registerF = fileOfRegisters.get(i + 32);
            registerRTableModel.setValueAt(registerR.getName(), i, 0);
            registerRTableModel.setValueAt(registerR.getValue(), i, 1);
            System.out.println(registerR.getValue());
            registerRTableModel.setValueAt(registerR.getQueue(), i, 2);
            registerFTableModel.setValueAt(registerF.getName(), i, 0);
            registerFTableModel.setValueAt(registerF.getValue(), i, 1);
            registerFTableModel.setValueAt(registerF.getQueue(), i, 2);
        }
    }

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
                Main.MultiplyStation = new ReservationArea[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 1) {
                Main.AddStation = new ReservationArea[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 2) {
                Main.LoadStation = new LoadBuffer[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 3) {
                Main.StoreStation = new StoreBuffer[Integer.parseInt(newValue.toString())];
                inputValues++;
            }
            if (row == 4) {
                Main.latencyMul = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 5) {
                Main.latencyAddD = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 6) {
                Main.latencyDAdd = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 7) {
                Main.latencyLoad = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 8) {
                Main.latencyStore = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (row == 9) {
                Main.latencyDiv = Integer.parseInt(newValue.toString());
                inputValues++;
            }
            if (inputValues == 10) {
                main = new Main();
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
        for (Instruction instruction : Main.tableOfInstructions) {
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
        populateRegisterTables();
    }

    // Helper method to remove all rows from a table model
    private void removeAllRows(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReservationAreaGUI();
        });
    }
}