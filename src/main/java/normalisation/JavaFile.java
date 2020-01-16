package normalisation;

import normalisation.util.JavaElement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static normalisation.Util.getElements;

public class JavaFile extends ElementContainer {

    private List<String> imports;

    public JavaFile(File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<String> lines = preProcess(Files.readAllLines(Paths.get(file.getAbsolutePath())));
        body = getElements(".*class\\s+.*\\{\\s*", lines, ClassObject.class);
        imports = getImports(lines);
        combineComments();
    }


    /**
     * Normalises whitespace and numbers lines
     *
     * @param lines
     * @return
     */
    static List<String> preProcess(List<String> lines) {


        // remove empty lines and normalise whitespace
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String new_line = line.replaceAll("\\s+", " ").strip().trim();
            if (new_line.isBlank()) {
                lines.remove(i);
                i--;
            }
            else lines.set(lines.indexOf(line), new_line);
        }


        // number lines in file to allow for comparison to original after normalisation
        for (int i = 0; i < lines.size(); i++) {
            // add exception if starts with number
            String line = i + " " + lines.get(i);
            lines.set(i, line);
        }


        return lines;
    }

    /**
     * Extracts import lines and stores them in list
     *
     * @param lines
     * @return
     */
    public List<String> getImports(List<String> lines) {
        List<String> import_lines = new ArrayList<>();
        for (String line : lines) {
            if (line.matches("^[0-9]*\\s*import\\s+.*")) {
                import_lines.add(line);
            }
        }
        return import_lines;
    }


    public void sortImports() {
        Collections.sort(imports);
    }


}

