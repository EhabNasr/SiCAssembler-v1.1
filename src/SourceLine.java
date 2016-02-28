

public class SourceLine {
    protected String Line;
    protected String Label;
    protected String OpCode;
    protected String Operand;
    protected InstructionFormat Format;
    protected AddressingMode Mode;
    protected boolean X;
    protected Address address = new Address();
    protected String sicObjectCode;
    protected int size;
    protected String Errors = new String();
    public SourceLine(String Line) {
        this.Line = Line;
        this.Label = "";
        this.OpCode = "";
        this.Operand = "";
        if(Line.length()>=17){ //has operand
        this.Label = Line.substring(0, 7).replaceAll("\\s", "");
        this.OpCode = Line.substring(9, 14).replaceAll("\\s", "");
        
        if (this.Operand.indexOf('\'') == -1) {
            this.Operand = Line.substring(17);
        } else this.Operand = Line.substring(17).replaceAll("\\s", "");
        }
        else{ //has no operand
            this.Label = Line.substring(0, 7).replaceAll("\\s", "");
        this.OpCode = Line.substring(9).replaceAll("\\s", "");
        }
        this.setFormat();
        this.setMode();
        this.size = this.size();
        if(Line.contains("=")){
            this.Operand = "="+this.Operand;
            if(AssemblerTables.LitTable.containsKey(this.Operand)){
                
            }else{
                AssemblerTables.LitTable.put(this.Operand, new Literal(this.Operand));
                AssemblerTables.LitPool.add(new Literal(this.Operand));
            }
        }
    }
    public SourceLine(String label, String opcode){
        this.Operand = "";
        this.Label = label;
        this.OpCode = opcode;
        this.setFormat();
    }
    public void setSicObjectCode() {
      Assembler.Line_num++;
        this.sicObjectCode = "";
        if(this.Label.contains("*")){
            if(this.OpCode.contains("X")){
                this.sicObjectCode += (Integer.toHexString(AssemblerTables.LitTable.get(this.OpCode).value));
            }
            else if(this.OpCode.charAt(1)=='C'){
                for (int i = 3; i < OpCode.length() - 1; i++) {
                    String temp = Integer.toHexString((int) OpCode.charAt(i));
                    int l = 2 - temp.length();
                    for (int j = 0; j < l; j++)
                        this.sicObjectCode += "0";
                    this.sicObjectCode += temp;
                }
            }
        }
        else if (OpCode.compareToIgnoreCase("WORD") == 0) {
            String temp;
            temp = Integer.toHexString(Integer.decode(Operand));
            int l = 6 - temp.length();
            for (int i = 0; i < l; i++)
                this.sicObjectCode += "0";
            this.sicObjectCode += temp;
        } else if (OpCode.compareToIgnoreCase("BYTE") == 0) {
            if (Operand.startsWith("x") || Operand.startsWith("X")) {
                this.sicObjectCode = Operand.substring(2, Operand.length() - 1);
            } else if (Operand.startsWith("c") || Operand.startsWith("C")) {
                for (int i = 2; i < Operand.length() - 1; i++) {
                    String temp = Integer.toHexString((int) Operand.charAt(i));
                    int l = 2 - temp.length();
                    for (int j = 0; j < l; j++)
                        this.sicObjectCode += "0";
                    this.sicObjectCode += temp;
                }
            }
        } else if (getStdOpCode() != null) {
            String stdAddress_withX = getStdOperandAddress_withX();
            if(stdAddress_withX!=null)
                this.sicObjectCode = getStdOpCode() + stdAddress_withX;
        }
        else{
                if((AssemblerTables.OpTable.get(this.OpCode) == null)
                 &&(this.OpCode.compareToIgnoreCase("START")!= 0)
                 &&(this.OpCode.compareToIgnoreCase("WORD")!= 0)
                 &&(this.OpCode.compareToIgnoreCase("BYTE")!= 0)
                 &&(this.OpCode.compareToIgnoreCase("END")!= 0)
                 &&(this.OpCode.compareToIgnoreCase("RESW")!= 0)
                 &&(this.OpCode.compareToIgnoreCase("RESB")!= 0)
               ){
                    Assembler.addError();
                    this.Errors+="Unrecognized OpCode";
                    this.Errors+='\n'; 
                }
                if((AssemblerTables.SymTable.get(this.Operand) == null)
                     &&(this.OpCode.compareToIgnoreCase("START")!= 0)
                     &&(this.OpCode.compareToIgnoreCase("WORD")!= 0)
                     &&(this.OpCode.compareToIgnoreCase("BYTE")!= 0)
                     &&(this.OpCode.compareToIgnoreCase("END")!= 0)
                     &&(this.OpCode.compareToIgnoreCase("RESW")!= 0)
                     &&(this.OpCode.compareToIgnoreCase("RESB")!= 0)
                   )
                {
                    Assembler.addError();
                    this.Errors+="Unrecognized Operand/Label";
                    this.Errors+='\n';
                }
        }
    }
    public String getStdOperandAddress_withX() {
        Address temp;
        if(this.Operand.contains("*")){
            //ana mekassel a7ot getter :"D
            temp = this.address.add(0);
        }
        else{
            temp = AssemblerTables.SymTable.get(this.getOperand());
        }
        if (this.X) {
            temp.setAddress(Integer.valueOf(temp.getAddress(), 16) | 0x8000);
        }
        String pex = temp.getAddress();
        while (pex.length() < 4) {
            pex += "0";
        }
        return pex;
    }

    public String getStdOpCode() {
        String temp;
        if (OpCode.compareToIgnoreCase("START") == 0 || OpCode.compareToIgnoreCase("END") == 0 || OpCode.compareToIgnoreCase("RESW") == 0 || OpCode.compareToIgnoreCase("RESB") == 0) {
            return null;
        } else if (OpCode.compareToIgnoreCase("WORD") == 0) {
            temp = Integer.toHexString(Integer.parseInt(Operand, 16));
            System.out.println(Integer.toHexString(Integer.parseInt(Operand, 16)));
            System.out.println(temp);
            return temp;
        }

        if (AssemblerTables.OpTable.get(OpCode) == null) {
            return null;
        }

        temp = Integer.toHexString(AssemblerTables.OpTable.get(OpCode));
        while (temp.length() < 2) {
            temp = "0" + temp;
        }
        return temp;
    }

    public int size() {
        switch (this.Format) {
            case format1:
                return 1;
            case format2:
                return 2;
            case format3:
                return 3;
            case format4:
                return 4;
            case directive:
                if (this.OpCode.compareToIgnoreCase("Start") == 0) return 0;
                if (this.OpCode.compareToIgnoreCase("RESW") == 0) return 3 * Integer.decode(this.Operand);
                if (this.OpCode.compareToIgnoreCase("RESB") == 0) return Integer.decode(this.Operand);
                if (this.OpCode.compareToIgnoreCase("WORD") == 0) return 3;
                if (this.OpCode.compareToIgnoreCase("BYTE") == 0){
                    if (this.Operand.charAt(0) == 'X' || this.Operand.charAt(0) == 'x') return 1;
                if (this.Operand.charAt(0) == 'C' || this.Operand.charAt(0) == 'c') {
                    return this.Operand.substring(3, this.Operand.length()).length();
                }
                }
                if (this.OpCode.compareToIgnoreCase("LTORG") == 0
                 || this.OpCode.compareToIgnoreCase("ORG") == 0   
                 || this.OpCode.compareToIgnoreCase("EQU") == 0       ) return 0;
            default:
                throw new AssertionError(this.Format.name());

        }
    }

    public String getLabel() {
        return Label;
    }

    public String getOpCode() {
        return OpCode;
    }

    public String getOperand() {
        return Operand;
    }

    public void setFormat() {
        if ((this.OpCode.compareToIgnoreCase("Start") == 0)
                || (this.OpCode.compareToIgnoreCase("RESW") == 0)
                || (this.OpCode.compareToIgnoreCase("RESB") == 0)
                || (this.OpCode.compareToIgnoreCase("WORD") == 0)
                || (this.OpCode.compareToIgnoreCase("BYTE") == 0)
                || (this.OpCode.compareToIgnoreCase("LTORG") == 0)
                || (this.OpCode.compareToIgnoreCase("ORG") == 0)
                || (this.OpCode.compareToIgnoreCase("EQU") == 0)
                || (this.Label.contains("*"))
                ) {
            this.setFormat(InstructionFormat.directive);
            return;
        }
        if (Line.charAt(8) == '+') this.setFormat(InstructionFormat.format4);
        else {
            this.setFormat(InstructionFormat.format3);
            return;
        }
        if (this.Operand.indexOf(',') != -1) {
            if (AssemblerTables.SymTable.get(this.Operand.substring(0, this.Operand.indexOf(','))).getAddress_dec() <= 9) {
                this.setFormat(InstructionFormat.format2);
            } else if (AssemblerTables.SymTable.get(this.Operand.substring(0, this.Operand.indexOf(','))).getAddress_dec() > 9 && (this.Operand.charAt(this.Operand.indexOf(',') + 1) == 'X' || this.Operand.charAt(this.Operand.indexOf(',') + 1) == 'x')) {
                this.X = true;
                this.Operand = this.Operand.substring(0, this.Operand.indexOf(','));
                this.setFormat(InstructionFormat.format3);
                return;
            }
        }
        this.setFormat(InstructionFormat.format3);
    }

    public void setFormat(InstructionFormat Format) {
        this.Format = Format;
    }

    public void setMode() {
        if(this.Line.length()<17) {
            this.setMode(AddressingMode.directive); return;
        }
        if ((this.OpCode.compareToIgnoreCase("Start") == 0)
                || (this.OpCode.compareToIgnoreCase("RESW") == 0)
                || (this.OpCode.compareToIgnoreCase("RESB") == 0)
                || (this.OpCode.compareToIgnoreCase("WORD") == 0)
                || (this.OpCode.compareToIgnoreCase("BYTE") == 0)
                || (this.OpCode.compareToIgnoreCase("ORG") == 0)
                || (this.OpCode.compareToIgnoreCase("EQU") == 0)
                ){
            this.setMode(AddressingMode.directive);
            return;
        }
        if (Line.charAt(16) == '#') {
            this.setMode(AddressingMode.immediate);
        } else if (Line.charAt(16) == '@') {
            this.setMode(AddressingMode.indirect);
        } else if (this.Operand.indexOf(',') != -1) {
            if (this.Operand.charAt(this.Operand.indexOf(',') + 1) == 'X' || this.Operand.charAt(this.Operand.indexOf(',') + 1) == 'x') {
                this.X = true;
                this.Operand = this.Operand.substring(0, this.Operand.indexOf(','));
                this.setMode(AddressingMode.simple);
            } else if (AssemblerTables.SymTable.get(this.Operand.substring(0, this.Operand.indexOf(','))).getAddress_dec() <= 9) {
                this.setMode(AddressingMode.register_to_register);
                this.Operand = this.Operand.substring(0, 1) + this.Operand.substring(this.Operand.indexOf(',') + 1, this.Operand.length());
            }
        } else {
            this.setMode(AddressingMode.simple);
        }
    }

    private void setMode(AddressingMode Mode) {
        this.Mode = Mode;
    }

    public String getSicObjectCode() {
        return sicObjectCode;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    public String getErrors() {
        return Errors;
    }
}
