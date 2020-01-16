package normalisation.util;

public class Variable implements JavaElement {
    boolean is_final;
    ProtectionLevel protection_level;
    boolean is_static;
    boolean global;
    // change to enum or object
    private String type;
    private String name;
    private String declaration;

    public Variable(String line) {
        this.declaration = line;
        // add variable parsing

    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return declaration;
    }

    @Override
    public int length() {
        return declaration.length();
    }


}
