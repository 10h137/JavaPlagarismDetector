package normalisation.util;

public class Variable {
    boolean is_final;
    // change to enum or object
    private String type;
    private String name;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    public String toString() {

        String str = type + " " + name;
        return is_final ? "final " + str : str;
    }

    public Variable(String line){

    }


}
