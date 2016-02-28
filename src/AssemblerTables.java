

import java.util.Hashtable;
import java.util.Stack;

public class AssemblerTables {
    public static final Hashtable<String, Integer> OpTable = makeOpTab();
    public static Hashtable<String, Literal> LitTable = new Hashtable<>();
    public static Stack<Literal> LitPool = new Stack<>();
    public static final Hashtable<String, String> EQUTab = new Hashtable<>();
    public static Hashtable<String, Integer> makeOpTab() {
        Hashtable<String, Integer> OpTable = new Hashtable<>();
        OpTable.put("ADD", 24);
        OpTable.put("AND", 64);
        OpTable.put("COMP", 40);
        OpTable.put("DIV", 36);
        OpTable.put("J", 60);
        OpTable.put("JEQ", 0x30);
        OpTable.put("JGT", 52);
        OpTable.put("JLT", 56);
        OpTable.put("JSUB", 72);
        OpTable.put("LDA", 0x0);
        OpTable.put("LDCH", 80);
        OpTable.put("LDL", 8);
        OpTable.put("LDX", 4);
        OpTable.put("MUL", 32);
        OpTable.put("OR", 68);
        OpTable.put("RD", 216);
        OpTable.put("RSUB", 76);
        OpTable.put("STA", 12);
        OpTable.put("STCH", 84);
        OpTable.put("STL", 20);
        OpTable.put("STX", 16);
        OpTable.put("SUB", 28);
        OpTable.put("TD", 224);
        OpTable.put("TIX", 44);
        OpTable.put("WD", 220);
        return OpTable;
    }

    public static Hashtable<String, Address> SymTable = setRegisters();
    
    private static Hashtable<String, Address> setRegisters() {
        Hashtable<String, Address> regTAB = new Hashtable<>();
        Address A = new Address(0);
        Address X = new Address(1);
        Address L = new Address(2);
        Address B = new Address(3);
        Address S = new Address(4);
        Address T = new Address(5);
        Address F = new Address(6);
        Address PC = new Address(8);
        Address SW = new Address(9);
        regTAB.put("A", A);
        regTAB.put("X", X);
        regTAB.put("L", L);
        regTAB.put("B", B);
        regTAB.put("S", S);
        regTAB.put("T", T);
        regTAB.put("F", F);
        regTAB.put("PC", PC);
        regTAB.put("SW", SW);
        return regTAB;
    }
}
