import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Memory {
    String[] memory;
    int counter = 0;
    // ArrayList<potentialHazard> hazards = new ArrayList<potentialHazard>();
    // ArrayList<jumpCondition> jumps = new ArrayList<jumpCondition>();

    public Memory() {
        memory = new String[2048];
        for(int i=1024;i<1024+64;i++){
            memory[i] = "0";
        }
        try {
            readassembltfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readassembltfile() throws FileNotFoundException {
        BufferedReader reader;
        int counter = 0;

        try {
            reader = new BufferedReader(new FileReader("config.txt"));
            String line = reader.readLine();

            while (line != null) {
                if (counter == 1024) {
                    break;
                }
                // String[] parts = line.split(" ");
                memory[counter] = line;
                
                line = reader.readLine();
                counter++;
            }
            reader.close();
            this.counter = counter;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File does not exist.");
        }
    }

    // public String changetobinary(String assembly) {
    //     // ADD.D,SUB.D,MUL.D,DIV.D,ADDI,SUBI,BNEZ
    //     if (assembly.equals("ADD.D")) {
    //         return "000";
    //     }
    //     if (assembly.equals("SUB.D")) {
    //         return "001";
    //     }
    //     if (assembly.equals("MUL.D")) {
    //         return "010";
    //     }
    //     if (assembly.equals("DIV.D")) {
    //         return "011";
    //     }
    //     if (assembly.equals("ADDI")) {
    //         this.flagimm = true;
    //         return "100";
    //     }
    //     if (assembly.equals("SUBI")) {
    //         this.flagimm = true;
    //         return "101";
    //     }
    //     if (assembly.equals("BNEZ")) {
    //         return "110";
    //     }
    //     // R0 to R31 in 5 bits
    //     if (assembly.equals("R0")) {
    //         return "00000";
    //     }
    //     if (assembly.equals("R1")) {
    //         return "00001";
    //     }
    //     if (assembly.equals("R2")) {
    //         return "00010";
    //     }
    //     if (assembly.equals("R3")) {
    //         return "00011";
    //     }
    //     if (assembly.equals("R4")) {
    //         return "00100";
    //     }
    //     if (assembly.equals("R5")) {
    //         return "00101";
    //     }
    //     if (assembly.equals("R6")) {
    //         return "00110";
    //     }
    //     if (assembly.equals("R7")) {
    //         return "00111";
    //     }
    //     if (assembly.equals("R8")) {
    //         return "01000";
    //     }
    //     if (assembly.equals("R9")) {
    //         return "01001";
    //     }
    //     if (assembly.equals("R10")) {
    //         return "01010";
    //     }
    //     if (assembly.equals("R11")) {
    //         return "01011";
    //     }
    //     if (assembly.equals("R12")) {
    //         return "01100";
    //     }
    //     if (assembly.equals("R13")) {
    //         return "01101";
    //     }
    //     if (assembly.equals("R14")) {
    //         return "01110";
    //     }
    //     if (assembly.equals("R15")) {
    //         return "01111";
    //     }
    //     if (assembly.equals("R16")) {
    //         return "10000";
    //     }
    //     if (assembly.equals("R17")) {
    //         return "10001";
    //     }
    //     if (assembly.equals("R18")) {
    //         return "10010";
    //     }
    //     if (assembly.equals("R19")) {
    //         return "10011";
    //     }
    //     if (assembly.equals("R20")) {
    //         return "10100";
    //     }
    //     if (assembly.equals("R21")) {
    //         return "10101";
    //     }
    //     if (assembly.equals("R22")) {
    //         return "10110";
    //     }
    //     if (assembly.equals("R23")) {
    //         return "10111";
    //     }
    //     if (assembly.equals("R24")) {
    //         return "11000";
    //     }
    //     if (assembly.equals("R25")) {
    //         return "11001";
    //     }
    //     if (assembly.equals("R26")) {
    //         return "11010";
    //     }
    //     if (assembly.equals("R27")) {
    //         return "11011";
    //     }
    //     if (assembly.equals("R28")) {
    //         return "11100";
    //     }
    //     if (assembly.equals("R29")) {
    //         return "11101";
    //     }
    //     if (assembly.equals("R30")) {
    //         return "11110";
    //     }
    //     if (assembly.equals("R31")) {
    //         return "11111";
    //     }
    //     // F0 to F31 in 5 bits
    //     if (assembly.equals("F0")) {
    //         return "00000";
    //     }
    //     if (assembly.equals("F1")) {
    //         return "00001";
    //     }
    //     if (assembly.equals("F2")) {
    //         return "00010";
    //     }
    //     if (assembly.equals("F3")) {
    //         return "00011";
    //     }
    //     if (assembly.equals("F4")) {
    //         return "00100";
    //     }
    //     if (assembly.equals("F5")) {
    //         return "00101";
    //     }
    //     if (assembly.equals("F6")) {
    //         return "00110";
    //     }
    //     if (assembly.equals("F7")) {
    //         return "00111";
    //     }
    //     if (assembly.equals("F8")) {
    //         return "01000";
    //     }
    //     if (assembly.equals("F9")) {
    //         return "01001";
    //     }
    //     if (assembly.equals("F10")) {
    //         return "01010";
    //     }
    //     if (assembly.equals("F11")) {
    //         return "01011";
    //     }
    //     if (assembly.equals("F12")) {
    //         return "01100";
    //     }
    //     if (assembly.equals("F13")) {
    //         return "01101";
    //     }
    //     if (assembly.equals("F14")) {
    //         return "01110";
    //     }
    //     if (assembly.equals("F15")) {
    //         return "01111";
    //     }
    //     if (assembly.equals("F16")) {
    //         return "10000";
    //     }
    //     if (assembly.equals("F17")) {
    //         return "10001";
    //     }
    //     if (assembly.equals("F18")) {
    //         return "10010";
    //     }
    //     if (assembly.equals("F19")) {
    //         return "10011";
    //     }
    //     if (assembly.equals("F20")) {
    //         return "10100";
    //     }
    //     if (assembly.equals("F21")) {
    //         return "10101";
    //     }
    //     if (assembly.equals("F22")) {
    //         return "10110";
    //     }
    //     if (assembly.equals("F23")) {
    //         return "10111";
    //     }
    //     if (assembly.equals("F24")) {
    //         return "11000";
    //     }
    //     if (assembly.equals("F25")) {
    //         return "11001";
    //     }
    //     if (assembly.equals("F26")) {
    //         return "11010";
    //     }
    //     if (assembly.equals("F27")) {
    //         return "11011";
    //     }
    //     if (assembly.equals("F28")) {
    //         return "11100";
    //     }
    //     if (assembly.equals("F29")) {
    //         return "11101";
    //     }
    //     if (assembly.equals("F30")) {
    //         return "11110";
    //     }
    //     if (assembly.equals("F31")) {
    //         return "11111";
    //     }
    //     return null;
    // }

    public void printMemory() {
        for (int i = 0; i < counter; i++) {
            System.out.println("memory[ " + i + " ] :" + memory[i]);
        }
    }

    public static void main(String[] args) {
        Memory memory = new Memory();
        memory.printMemory();
    }
}