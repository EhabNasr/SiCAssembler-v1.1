
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {
    public static final File lisFile = new File("LISFILE");
    public static ArrayList<SourceLine> sourceLines = new ArrayList<>();
    public static Address locator = new Address();
    private static int num_Errors = 0;
    public static int Line_num = 0;
    public static void main(String[] args)throws Exception{
      try{
          Assembler.generatePass1();
      Assembler.generatePass2();
      Assembler.generateListFile(Assembler.makeListFile());
      if(num_Errors == 0)
          Converter.getInstance().createObjFile();
      }catch (Exception e){
          FileWriter fileWriter = null;
          try {
              fileWriter = new FileWriter(lisFile);
          } catch (IOException e1) {
              e1.printStackTrace();
          }
          assert fileWriter != null;
          BufferedWriter writer = new BufferedWriter(fileWriter);
          try {
              writer.write("Errors found; please re-check your code.\n");
          String listFile = new String();
          for(int k = 0 ;k<Assembler.Line_num-1; k++) {
          
          if (sourceLines.get(k).getSicObjectCode().length() > 6) {

              listFile += (sourceLines.get(k).address.getAddress() + " " + sourceLines.get(k).getSicObjectCode().substring(0, 6));
              listFile += (" " + sourceLines.get(k).getLabel() + " ");
              for (int i = 0; i < (8 - (sourceLines.get(k).getLabel().length())); i++) {
                  listFile += (" ");
              }
              listFile += (sourceLines.get(k).getOpCode());
              for (int i = 0; i < (8 - (sourceLines.get(k).getOpCode().length())); i++) {
                  listFile += (" ");
              }
              listFile += (sourceLines.get(k).getOperand()) + '\n';
              int j = 6;
              while (j < (sourceLines.get(k).getSicObjectCode().length())) {
                  int i = 0;
                  listFile += ("     ");
                  while (i < 6 && j < (sourceLines.get(k).getSicObjectCode().length())) {
                      listFile += (sourceLines.get(k).getSicObjectCode().charAt(j));
                      j++;
                      i++;
                  }
                  listFile += '\n';
              }
          } else {
              listFile += (sourceLines.get(k).address.getAddress() + " ");
              listFile += (sourceLines.get(k).getSicObjectCode() + " ");
              for (int i = 0; i < (6 - (sourceLines.get(k).getSicObjectCode().length())); i++) {
                  listFile += (" ");
              }
              listFile += (sourceLines.get(k).getLabel() + " ");
              for (int i = 0; i < (8 - (sourceLines.get(k).getLabel().length())); i++) {
                  listFile += (" ");
              }
              listFile += (sourceLines.get(k).getOpCode());
              for (int i = 0; i < (8 - (sourceLines.get(k).getOpCode().length())); i++) {
                  listFile += (" ");
              }
              listFile += (sourceLines.get(k).getOperand()) + '\n';
          }
          
          if(!sourceLines.get(k).getErrors().isEmpty()){
              listFile += sourceLines.get(k).getErrors();
          }
          
      }
          writer.write(listFile);
          writer.write("Unrecognized Operand/Label" + '\n');
          for(int i = Assembler.Line_num; i<sourceLines.size(); i++){
              String RestOfErrors = new String();
              if((AssemblerTables.OpTable.get(sourceLines.get(i).OpCode) == null)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("START")!= 0)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("WORD")!= 0)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("BYTE")!= 0)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("END")!= 0)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("RESW")!= 0)
               &&(sourceLines.get(i).OpCode.compareToIgnoreCase("RESB")!= 0)
             ){
                  Assembler.addError();
                  RestOfErrors+="Unrecognized OpCode";
                  RestOfErrors+='\n'; 
              }
              if((AssemblerTables.SymTable.get(sourceLines.get(i).Operand) == null)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("START")!= 0)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("WORD")!= 0)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("BYTE")!= 0)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("END")!= 0)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("RESW")!= 0)
                   &&(sourceLines.get(i).OpCode.compareToIgnoreCase("RESB")!= 0)
                 )
              {
                  Assembler.addError();
                  RestOfErrors+="Unrecognized Operand/Label";
                  RestOfErrors+='\n';
              }
              writer.write(sourceLines.get(i).address.getAddress()+ '\t' + '\t' + sourceLines.get(i).Line + '\n' + RestOfErrors);
              
          }
          } catch (IOException e1) {
              e1.printStackTrace();
          }
          try {
              writer.close();
          } catch (IOException e1) {
              e1.printStackTrace();
          }
      }
        
    }
    public static void generatePass1() throws Exception {
        File sourceFile = new File("SRCFILE");
        Address prevORG = new Address(0);
        Scanner getInstructions = new Scanner(new FileReader(sourceFile));
        sourceLines.add(new SourceLine(getInstructions.nextLine()));
        if (sourceLines.get(0).getOpCode().compareToIgnoreCase("START") == 0) {
            locator.setAddress(sourceLines.get(0).getOperand());
            sourceLines.get(0).address.setAddress(locator.getAddress());
        } else {
            locator.setAddress("0");
        }
        if (!sourceLines.get(0).getLabel().isEmpty()){
            AssemblerTables.SymTable.put(sourceLines.get(0).getLabel(), locator);
        }
        System.err.println(sourceLines.get(0).address.getAddress() + '\t' + sourceLines.get(0).Label + 
                    '\t' + sourceLines.get(0).OpCode+ '\t' + sourceLines.get(0).Operand);
        int i = 0;
        while (getInstructions.hasNextLine()) {
            i++;
            sourceLines.add(new SourceLine(getInstructions.nextLine()));
                        
            sourceLines.get(i).address.setAddress(locator.getAddress());
            if(sourceLines.get(i).getOpCode().compareToIgnoreCase("EQU") == 0){
                if(sourceLines.get(i).getOperand().contains("*")){
                    AssemblerTables.EQUTab.put(sourceLines.get(i).getLabel(), sourceLines.get(i).address.getAddress());
                }
                else{
                    AssemblerTables.EQUTab.put(sourceLines.get(i).getLabel(), sourceLines.get(i).getOperand());
                }
            }
            if(AssemblerTables.EQUTab.containsKey(sourceLines.get(i).getOperand())){
                sourceLines.get(i).Operand = AssemblerTables.EQUTab.get(sourceLines.get(i).getOperand());
            }
            if (!sourceLines.get(i).getLabel().isEmpty()) {
                AssemblerTables.SymTable.put(sourceLines.get(i).getLabel(), locator);
            }
                locator = locator.add(sourceLines.get(i).size());
            
            if(sourceLines.get(i).getOpCode().compareToIgnoreCase("ORG") ==0){
                //no previous org statement
                if(sourceLines.get(i).Line.length()>17){//means it has an operand
                    prevORG = locator.add(0);
                    if(sourceLines.get(i).getOperand().contains("+")
                        || sourceLines.get(i).getOperand().contains("-")){   //check for expression
                            //do expressions
                            if(Assembler.checkExpression_Absolute(sourceLines.get(i).getOperand())){
                                locator = new Address();
                                locator.setAddress(Assembler.getExpressions_Ans(sourceLines.get(i).getOperand()));
                            }
                            else{
                                //generate error
                                Assembler.addError();
                                sourceLines.get(i).Errors+= "Invalid Expression (Relative reference)";
                                sourceLines.get(i).Errors+='\n';
                            }
                            
                        }else{
                                locator = new Address();
                                locator.setAddress(sourceLines.get(i).getOperand());
                        }
                        sourceLines.get(i).address.setAddress(locator.getAddress());

                    }
                    else{
                        locator.setAddress(prevORG.getAddress());
                        sourceLines.get(i).address.setAddress(locator.getAddress());
                        prevORG.setAddress(0);
                    }
            }
            
            System.err.println(sourceLines.get(i).address.getAddress() + '\t' + sourceLines.get(i).Label + 
                    '\t' + sourceLines.get(i).OpCode+ '\t' + sourceLines.get(i).Operand);
        if(sourceLines.get(i).getOpCode().compareToIgnoreCase("LTORG") == 0 
             ||sourceLines.get(i).getOpCode().compareToIgnoreCase("END") == 0){
                int j = i+1;
                while(!AssemblerTables.LitPool.isEmpty()){
                    
                    Literal temp = AssemblerTables.LitPool.pop();
                    
                    sourceLines.add(new SourceLine("*" , temp.getName()));
                    sourceLines.get(j).address.setAddress(locator.getAddress());
                    AssemblerTables.LitTable.get(temp.getName()).setAddress(locator);
                    AssemblerTables.SymTable.put(temp.getName(), locator);
                    locator = locator.add(AssemblerTables.LitTable.get(temp.name).getSize());
                    System.err.println(sourceLines.get(j).address.getAddress() + '\t' + sourceLines.get(j).Label + 
                    '\t' + sourceLines.get(j).OpCode);
                    j++;
                }
                i =j-1;
            }

        }
    }
    public static void printPass1(){
        for(SourceLine x: sourceLines){
            System.err.println(x.address.getAddress() + '\t' + x.Label + '\t' + x.OpCode+ '\t' + x.Operand);
        }
    }
    public static void generatePass2() {
        sourceLines.forEach(SourceLine::setSicObjectCode);
    }

    public static String makeListFile() {
        String listFile = "";
        for (SourceLine x : sourceLines) {
            if (x.getSicObjectCode().length() > 6) {

                listFile += (x.address.getAddress() + " " + x.getSicObjectCode().substring(0, 6));
                listFile += (" " + x.getLabel() + " ");
                for (int i = 0; i < (8 - (x.getLabel().length())); i++) {
                    listFile += (" ");
                }
                listFile += (x.getOpCode());
                for (int i = 0; i < (8 - (x.getOpCode().length())); i++) {
                    listFile += (" ");
                }
                if(!(x.Operand.isEmpty()))
                    listFile += (x.getOperand()) + '\n';
                else
                    listFile += '\n';
                int j = 6;
                while (j < (x.getSicObjectCode().length())) {
                    int i = 0;
                    listFile += ("     ");
                    while (i < 6 && j < (x.getSicObjectCode().length())) {
                        listFile += (x.getSicObjectCode().charAt(j));
                        j++;
                        i++;
                    }
                    listFile += '\n';
                }
            } else {
                listFile += (x.address.getAddress() + " ");
                listFile += (x.getSicObjectCode() + " ");
                for (int i = 0; i < (6 - (x.getSicObjectCode().length())); i++) {
                    listFile += (" ");
                }
                listFile += (x.getLabel() + " ");
                for (int i = 0; i < (8 - (x.getLabel().length())); i++) {
                    listFile += (" ");
                }
                listFile += (x.getOpCode());
                for (int i = 0; i < (8 - (x.getOpCode().length())); i++) {
                    listFile += (" ");
                }
                listFile += (x.getOperand()) + '\n';
            }

            if(!x.getErrors().isEmpty()){
                listFile += x.getErrors();
            }
        }
        return listFile;
    }

    public static void generateListFile(String LF) throws IOException {
        FileWriter fileWriter = new FileWriter(lisFile);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.write(LF);
        writer.close();
    }
    public static void addError(){
        num_Errors++;
    }
    
    public static boolean checkExpression_Absolute(String exprtn){
        //check if the expression is absolute or relative
        String Expression = exprtn.replaceAll("\\s", "");
        String temp[] = Expression.replace('+', '-').split("-");  //we replaced all signes by - in order to split the operands in the expressions but the original expression is still the same      
        String crnt;
        int index = 0;
        crnt = temp[0];
        if(AssemblerTables.EQUTab.containsKey(temp[0])){
                crnt = AssemblerTables.EQUTab.get(temp[0]);
            }
        boolean state = AssemblerTables.SymTable.containsKey(crnt);
          index = index + temp[0].length();
        for(int i = 1 ; i<temp.length && index < Expression.length(); i++){
            if(AssemblerTables.EQUTab.containsKey(temp[i])){
                crnt = AssemblerTables.EQUTab.get(temp[i]);
            }
            if(Expression.charAt(index) == '+'){
                //or
                state = state || AssemblerTables.SymTable.containsKey(crnt);
            }
            if(Expression.charAt(index) == '-'){
                //xor
                state = state ^ AssemblerTables.SymTable.containsKey(crnt);
            }
            index = index + temp[i].length()+1;
        }
        state = !state;
        return state;
    }
    public static String getExpressions_Ans(String exprtn){
        int ans;
        String Expression = exprtn.replaceAll("\\s", "");
        String temp[] = Expression.replace('+', '-').split("-");  //we replaced all signes by - in order to split the operands in the expressions but the original expression is still the same      
        String crnt = new String();
        int index = 0;
        crnt = temp[0];
        if(AssemblerTables.EQUTab.contains(temp[0])){
            crnt = AssemblerTables.EQUTab.get(temp[0]);
        }
        
        if(AssemblerTables.SymTable.containsKey(crnt)){
            ans = AssemblerTables.SymTable.get(crnt).getAddress_dec();
        }else{
            //it's absolute
            ans = Integer.decode(crnt);
        }
        for(int i = 1 ; i<temp.length && index < Expression.length(); i++){
            if(AssemblerTables.EQUTab.contains(temp[i])){
                crnt = AssemblerTables.EQUTab.get(temp[i]);
            }
            if(AssemblerTables.SymTable.containsKey(crnt)){
            crnt = AssemblerTables.SymTable.get(temp[i]).getAddress();
            }else{
                //it's absolute
                crnt = (temp[i]);
            }
            if(Expression.charAt(index) == '+'){
                //or
                ans = ans + Integer.decode(crnt);
            }
            if(Expression.charAt(index) == '-'){
                //xor
                ans = ans - Integer.decode(crnt);
            }
            index = index + temp[i].length()+1;
        }
        return Integer.toHexString(ans);
    }
}
