package normalisation.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Variable implements JavaElement, Text{
    boolean is_final;
    ProtectionLevel protection_level = null;
    boolean is_static;
    boolean global;
    // change to enum or object
    private String type;
    private String name;
    private String declaration = "";

    public Variable(String line) {
    parseDeclaration(line);
    }


    public void setName(String name){
        declaration = declaration.replace(this.name, name);
        this.name = name;
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


    //TODO  fix for multi line variable declaration

    public void parseDeclaration(String line){
        this.declaration = line.trim().strip();
        // add variable parsing

        String dec = declaration.split("(\\s*=)|(\\s*;)")[0];
        String[] s = dec.split("\\s+");
        name = s[s.length - 1];
        type = s[s.length - 2];

        List<String> protection_strings = Arrays.stream(ProtectionLevel.values())
                .map(ProtectionLevel::getString)
                .collect(Collectors.toList());
        int i = 0;
        try {
            Integer.parseInt(s[0].strip());
            i = 1;
        } catch (Exception ignored) {}
        for (; i < s.length - 2; i++) {
            if (!is_final) is_final = s[i].equals("final");
            if (!is_static) is_static = s[i].equals("static");
            if (protection_strings.contains(s[i]) && !s[i].isBlank()) {
                protection_level = ProtectionLevel.valueOf(s[i].toUpperCase());
            }
        }

        if (protection_level == null) protection_level = ProtectionLevel.PACKAGE_PRIVATE;

    }

    @Override
    public void setText(String text) {
        this.declaration = text;
        parseDeclaration(declaration);
    }
}
