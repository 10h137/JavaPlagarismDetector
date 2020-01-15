package normalisation;

import normalisation.util.Variable;

import java.util.List;

public class Method implements JavaElement {

    String name;
    String return_type;
    ProtectionLevel protection_level;
    List<Variable> args;
    List<JavaElement> method_body;
    private List<String> lines;

    @Override

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // appends method signature
        sb.append(protection_level.toString().toLowerCase() + " " + return_type + name + " (");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).toString());
            if (i != args.size() - 1) sb.append(", ");
        }
        sb.append(") {\n");

        // appends method body
        method_body.stream().map(JavaElement::toString).forEach(sb::append);
        sb.append("\n}");

        return sb.toString();
    }

    @Override
    public int size() {
        return method_body.stream().map(JavaElement::size).reduce(Integer::sum).orElse(0);
    }


    public Method(List<String> method_text){



    }



}



