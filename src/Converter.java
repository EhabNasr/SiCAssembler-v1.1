
import java.io.*;
import java.util.ArrayList;

public class Converter {
    private static Converter instance = new Converter();
    public static Converter getInstance() {
        return instance;
    }
    private final File listFile = new File("LISFILE");
    private final File objFile = new File("OBJFILE");
    private ArrayList<Long> addresses= new ArrayList<>();
    private ArrayList<String> obCodes= new ArrayList<>();
    private ArrayList<Integer> sizes = new ArrayList<>();
    private ArrayList<Boolean> shouldEndT = new ArrayList<>();
    private String programName;
    private Converter(){
    }
    private void parse()throws Exception{
        String line;
        ArrayList<String> arguments = new ArrayList<>();
        String[] temp;
        FileReader fileReader = new FileReader(listFile);
        BufferedReader reader = new BufferedReader(fileReader);
        line = reader.readLine();
        temp = line.split(" ");
        for(String s:temp)
            if (!s.equals(""))
                arguments.add(s);
        programName=arguments.get(1);
        System.err.println(line);
        while((line = reader.readLine()) != null){
            System.err.println(line);
            arguments.clear();
            temp=line.split(" ");
            for(String s:temp)
                if (!s.equals(""))
                    arguments.add(s);
            System.err.println("Size = "+ arguments.size());
            if(arguments.size()==1) {
                obCodes.set(obCodes.size() - 1,obCodes.get(obCodes.size() - 1).concat(arguments.get(0)));
                sizes.set(sizes.size() - 1, obCodes.get(sizes.size() - 1).length() / 2);
                continue;
            }
            if(arguments.get(1).equalsIgnoreCase("ORG")||arguments.get(1).equalsIgnoreCase("LTORG")){
                continue;
            }
            if(arguments.get(2).equalsIgnoreCase("END")||arguments.get(1).equalsIgnoreCase("END")) {
                addresses.add(Long.decode("0x" + arguments.get(0)));
                continue;
            }
            if (arguments.get(2).equalsIgnoreCase("RESW")||arguments.get(2).equalsIgnoreCase("RESB")) {
                if(!shouldEndT.get(shouldEndT.size() - 1))
                    shouldEndT.add(true);
                continue;
            }
            
            addresses.add(Long.decode("0x"+arguments.get(0)));
            obCodes.add(arguments.get(1));
            sizes.add(obCodes.get(obCodes.size()-1).length()/2);
            shouldEndT.add(false);
            
        }
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void createObjFile() throws Exception{
        parse();
        int currentIndex=0;
        int desiredIndex;
        int currentTsize=0;
        String temp= "";
        int l;
        long programSize =addresses.get(addresses.size()-1)-addresses.get(0);
        FileWriter fileWriter = new FileWriter(objFile);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        writer.write("H" + programName);
        l=6-programName.length();
        for(int i=0;i<l;i++)
            writer.write(" ");
        l=6-Long.toHexString(addresses.get(0)).length();
        for(int i=0;i<l;i++)
            writer.write("0");
        writer.write(Long.toHexString(addresses.get(0)).toUpperCase());
        l=6-Long.toHexString(programSize).length();
        for (int i=0;i<l;i++)
            writer.write("0");
        writer.write(Long.toHexString(programSize).toUpperCase());
        writer.newLine();
        while (currentIndex<obCodes.size()) {
            writer.write("T");
            desiredIndex = currentIndex;
            do {
                currentTsize += sizes.get(desiredIndex);
                desiredIndex += 1;
            } while (currentTsize < 30 && !shouldEndT.get(desiredIndex)&&desiredIndex<obCodes.size());
            if (currentTsize > 30) {
                desiredIndex -= 2;
                currentTsize -= sizes.get(desiredIndex + 1);
            }
            l = 6 - Long.toHexString(addresses.get(currentIndex)).length();
            for (int i = 0; i < l; i++)
                writer.write("0");
            writer.write(Long.toHexString(addresses.get(currentIndex)).toUpperCase());
            while (currentIndex != desiredIndex) {
                currentTsize += sizes.get(currentIndex);
                temp += obCodes.get(currentIndex);
                currentIndex++;
            }
            l=2-Integer.toHexString(currentTsize / 2).length();
            for (int i = 0; i < l; i++)
                writer.write("0");
            writer.write(Integer.toHexString(currentTsize / 2).toUpperCase());
            writer.write(temp);
            temp="";
            currentTsize=0;
            writer.newLine();
        }
        writer.write("E");
        l=6-Long.toHexString(addresses.get(0)).length();
        for(int i=0;i<l;i++)
            writer.write("0");
        writer.write(Long.toHexString(addresses.get(0)).toUpperCase());
        writer.close();
    }
}
