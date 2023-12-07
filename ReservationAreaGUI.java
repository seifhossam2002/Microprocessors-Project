import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class ReservationAreaGUI extends JFrame {

    private DefaultTableModel addSubTableModel;
    private DefaultTableModel mulDivTableModel;
    private DefaultTableModel loadBufferTableModel;
    private DefaultTableModel storeBufferTableModel;
    private DefaultTableModel registerRTableModel;
    private DefaultTableModel registerFTableModel;
    private DefaultTableModel instructionsTableModel;
    private JTable addSubTable;
    private JTable mulDivTable;
    private JTable loadBufferTable;
    private JTable storeBufferTable;
    private JTable registerRTable;
    private JTable registerFTable;
    private JTable instructionsTable;
    private JLabel cycleLabel;
    private int currentCycle = 0;

    public ReservationAreaGUI() {
        // Set up the frame
        setTitle("Reservation Areas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600); // Adjusted size to accommodate the new tables

        // Initialize tables and models
        addSubTableModel = new DefaultTableModel(new Object[]{"ID", "Opcode", "Value_j", "Value_k", "Queue_j", "Queue_k", "Busy"}, 3);
        mulDivTableModel = new DefaultTableModel(new Object[]{"ID", "Opcode", "Value_j", "Value_k", "Queue_j", "Queue_k", "Busy"}, 2);
        loadBufferTableModel = new DefaultTableModel(new Object[]{"ID", "Reservation Area ID", "Address", "Busy"}, 3);
        storeBufferTableModel = new DefaultTableModel(new Object[]{"ID", "Reservation Area ID", "Address", "Value", "Queue", "Busy"}, 3);
        registerRTableModel = new DefaultTableModel(new Object[]{"Name", "Value", "Queue"}, 32); // 32 rows for R0-R31
        registerFTableModel = new DefaultTableModel(new Object[]{"Name", "Value", "Queue"}, 32); // 32 rows for F0-F31
        instructionsTableModel = new DefaultTableModel(
                new Object[]{"ID", "Reservation Name", "Opcode", "Dest", "Src1", "Src2", "Issue", "Start Exec", "End Exec", "Write Result Clock", "Result", "In Execution"},
                0
        );

        addSubTable = new JTable(addSubTableModel);
        mulDivTable = new JTable(mulDivTableModel);
        loadBufferTable = new JTable(loadBufferTableModel);
        storeBufferTable = new JTable(storeBufferTableModel);
        registerRTable = new JTable(registerRTableModel);
        registerFTable = new JTable(registerFTableModel);
        instructionsTable = new JTable(instructionsTableModel);

        // Set grid color to black
        addSubTable.setGridColor(Color.BLACK);
        mulDivTable.setGridColor(Color.BLACK);
        loadBufferTable.setGridColor(Color.BLACK);
        storeBufferTable.setGridColor(Color.BLACK);
        registerRTable.setGridColor(Color.BLACK);
        registerFTable.setGridColor(Color.BLACK);
        instructionsTable.setGridColor(Color.BLACK);

        // Add tables to the frame
        JPanel panel = new JPanel(new GridLayout(3, 3));

        // Create table panels with preferred sizes
        panel.add(createTablePanel("Addition/Subtraction Table", addSubTable, 300, 150));
        panel.add(createTablePanel("Multiplication/Division Table", mulDivTable, 300, 100));
        panel.add(createTablePanel("Load Buffer Table", loadBufferTable, 300, 150));
        panel.add(createTablePanel("Store Buffer Table", storeBufferTable, 300, 150));
        panel.add(createTablePanel("Register R Table", registerRTable, 150, 200));
        panel.add(createTablePanel("Register F Table", registerFTable, 150, 200));
        panel.add(createTablePanel("Instructions Table", instructionsTable, 500, 200));

        // Add cycle label
        cycleLabel = new JLabel("Cycle: 0", SwingConstants.CENTER);
        cycleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(cycleLabel);

        // Add next and previous buttons
        JButton nextButton = new JButton("Next Cycle");
        JButton prevButton = new JButton("Previous Cycle");

        nextButton.addActionListener(e -> {
            currentCycle++;
            updateCycleLabel(currentCycle);
        });

        prevButton.addActionListener(e -> {
            if (currentCycle > 0) {
                currentCycle--;
                updateCycleLabel(currentCycle);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel);

        add(panel);

        // Display the frame
        setVisible(true);

        // Populate Register R Table and Register F Table
        populateRegisterTables();
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
        addSubTableModel.addRow(new Object[]{
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
        mulDivTableModel.addRow(new Object[]{
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
        loadBufferTableModel.addRow(new Object[]{
                loadBuffer.getInstructionId(),
                loadBuffer.getReservationAreaId(),
                loadBuffer.getAddress(),
                loadBuffer.getBusy()
        });
    }

    // Method to add a row to the store buffer table
    public void addStoreBuffer(StoreBuffer storeBuffer) {
        storeBufferTableModel.addRow(new Object[]{
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
        instructionsTableModel.addRow(new Object[]{
                instruction.instructionId,
                instruction.reservationName,
                instruction.opcode,
                instruction.dest,
                instruction.src1,
                instruction.src2,
                instruction.issue,
                instruction.startExec,
                instruction.endExec,
                instruction.writeResultClock,
                instruction.result,
                instruction.inExecution
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
            registerRTableModel.setValueAt(registerR.getQueue(), i, 2);
            registerFTableModel.setValueAt(registerF.getName(), i, 0);
            registerFTableModel.setValueAt(registerF.getValue(), i, 1);
            registerFTableModel.setValueAt(registerF.getQueue(), i, 2);
        }
    }

    // Method to update the cycle label
    public void updateCycleLabel(int cycle) {
        cycleLabel.setText("Cycle: " + cycle);
    }

    // Method to update the instruction table
    public void updateInstructionTable() {
        // Implement this method based on the data structures and logic in your program
    }

    // Method to update the instruction queue table
    public void updateInstructionQueueTable() {
        // Implement this method based on the data structures and logic in your program
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ReservationAreaGUI();
        });
    }
}