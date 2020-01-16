package normalisation.util;

import java.util.List;

public class Result {
    public List<JavaElement> elements;
    public List<Variable> variables;

    public Result(List<JavaElement> elements, List<Variable> variables) {
        this.elements = elements;
        this.variables = variables;
    }
}
