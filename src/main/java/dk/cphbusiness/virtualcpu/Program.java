package dk.cphbusiness.virtualcpu;

import java.util.Iterator;
import java.util.List;

public class Program implements Iterable<Integer> {

    private String[] lines;

    public Program(String... lines) {
        this.lines = lines;
    }

    public Program(List<String> lines) {
        this.lines = lines.toArray(new String[lines.size()]);
    }

    public int get(int index) {
        String line = lines[index];
        if (line.charAt(0) == '0' || line.charAt(0) == '1') {
            return Integer.parseInt(line, 2);
        } else {
            return getFromCommand(line);
        }

    }

    public String getString(int index) {
        return lines[index];
    }

    private int getFromCommand(String line) {

        switch (line) {
            case "NOP":
                return 0b0000_0000;
            case "ADD":
                return 0b0000_0001;
            case "MUL":
                return 0b0000_0010;
            case "DIV":
                return 0b0000_0011;
            case "ZERO":
                return 0b0000_0100;
            case "NEG":
                return 0b0000_0101;
            case "POS":
                return 0b0000_0110;
            case "NZERO":
                return 0b0000_0111;
            case "EQ":
                return 0b0000_1000;
            case "LT":
                return 0b0000_1001;
            case "GT":
                return 0b0000_1010;
            case "NEQ":
                return 0b0000_1011;
            case "ALWAYS":
                return 0b0000_1100;
            case "HALT":
                return 0b0000_1111;
            case "PUSH A":
                return 0b0001_0000;
            case "PUSH B":
                return 0b0001_0001;
            case "POP A":
                return 0b0001_0010;
            case "POP B":
                return 0b0001_0011;
            case "MOV A B":
                return 0b0001_0100;
            case "MOV B A":
                return 0b0001_0101;
            case "INC" : 
                return 0b0001_0110;
            case "DEC" :
                return 0b0001_0111;
        }

       
        if (line.startsWith("RTN")) {
            String[] parts = line.split(" ");
            return 0b0001_1000 | Integer.parseInt(parts[1]);
        }

        if (line.startsWith("JMP")) {
            return 0b1000_0000 | Integer.parseInt(line.split(" ")[1]);
        }
        if (line.startsWith("CALL")) {
            return 0b1100_0000 | Integer.parseInt(line.split(" ")[1]);
        }

        if (line.startsWith("MOV ")) {
            String[] parts = line.split(" ");
            if (parts[1].equals("A") || parts[1].equals("B")) {
                int r = parts[1].equals("B") ? 1 : 0;
                int o = Integer.parseInt(parts[2]);
                return 0b0010_0000 | (r << 3) | o;
            } else if (parts[2].equals("A") || parts[2].equals("B")) {
                int r = parts[1].equals("B") ? 1 : 0;
                // 2 interpretations?!
                if (parts[1].startsWith("+")) {
                    return 0b0011_0000 | Integer.parseInt(parts[1]) << 1 | r;
                } else {
                    return 0b01000000 | convertValue2Complement(parts[1]) << 1 | r;
                }

            }
            throw new UnsupportedOperationException("Don't know " + line);
        } else {
            throw new UnsupportedOperationException("Don't know " + line);
        }
    }

    private int convertValue2Complement(String number) {
        int num = Integer.parseInt(number);
        if (num < 0) {
            return 32 - Math.abs(num);
        }
        return num;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new ProgramIterator();
    }

    private class ProgramIterator implements Iterator<Integer> {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < lines.length;
        }

        @Override
        public Integer next() {
            return get(current++);
        }

    }

}
