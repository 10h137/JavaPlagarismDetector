package normalisation.util;

public class GlobalVariable extends Variable{


    ProtectionLevel protection_level;
    boolean is_static;

    public GlobalVariable(String line) {
        super(line);
    }

//    public GlobalVariable(String variable_declaration){
//        String[] split = variable_declaration.split(" \\+");
//
//    }



}
