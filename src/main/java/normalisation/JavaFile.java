package normalisation;

import normalisation.util.JavaElement;
import normalisation.util.Result;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static normalisation.Util.getElements;

public class JavaFile {

    String file_name;
    List<JavaElement> java_elements;
    List<String> imports;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        imports.forEach(s -> sb.append(s + "\n"));
        java_elements.stream().map(JavaElement::toString).forEach(s -> sb.append(s + "\n"));
        return sb.toString();
    }

    public List<String> getImports(List<String> lines) {
        List<String> import_lines = new ArrayList<>();
        for (String line : lines) {
            if (line.matches("^[0-9]*\\s*import\\s+.*")) {
                import_lines.add(line);
            }
        }
        return import_lines;
    }

    public JavaFile(File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<String> lines = preProcess(Files.readAllLines(Paths.get(file.getAbsolutePath())));
        Result res = getElements(".*class\\s+.*\\{\\s*", lines, ClassObject.class);
        java_elements = res.elements;
        imports = getImports(lines);
    }

    static List<String> preProcess(List<String> lines){

        // remove empty lines and normalise whitespace
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                lines.set(lines.indexOf(line), line.replaceAll("\\s+", " ").strip().trim());
            } else {
                lines.remove(line);
            }
        }

        // number lines in file to allow for comparison to original after normalisation
        for (int i = 0; i < lines.size(); i++) {
            // add exception if starts with number
            String line = i + " " + lines.get(i);
            lines.set(i, line);
        }

        return lines;
    }


}

