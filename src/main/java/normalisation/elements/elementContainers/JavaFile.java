package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



/**
 *
 */
public class JavaFile extends ElementContainer implements JavaElement {

    private List<String> imports;
    private File file;

    public JavaFile(File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<String> lines = preProcess(Files.readAllLines(Paths.get(file.getAbsolutePath())));
        body = getElements(".*class\\s+.*\\{\\s*", lines, ClassObject.class);
        imports = getImports(lines);
        combineComments();
        this.file = file;
        this.original_string = this.toString();
    }

    /**
     * Normalises whitespace and numbers lines
     *
     * @param lines
     * @return
     */
    private static List<String> preProcess(List<String> lines) {


        // pads brackets with spaces
        for (int i = 0; i < lines.size(); i++) {
            String new_line = lines.get(i).replaceAll("\\(", " ( ")
                    .replaceAll("\\)", " ) ")
                    .replaceAll("\\{", " { ")
                    .replaceAll("}", " } ")
                    .replaceAll(";", " ; ")
                    .replaceAll("=", " = ")
                    .replaceAll("=\\s*=", " == ");
            lines.set(i, new_line);
            String line = lines.get(i);
            char[] charArray = line.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                if (c == '{' && !checkInString(line, j)) {
                    String start = line.substring(0, j + 1);
                    String end = line.substring(j + 1, line.length() - 1);
                    lines.set(i, start);
                    if (!end.strip().trim().isBlank()) lines.add(i + 1, end);
                    break;
                }

            }
        }

        // remove empty lines and normalise whitespace
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String new_line = line.replaceAll("\\s+", " ").strip().trim();
            if (new_line.isBlank()) {
                lines.remove(i);
                i--;
            } else lines.set(lines.indexOf(line), new_line);
        }


        return lines;
    }

    /**
     * Extracts import lines and stores them in list
     *
     * @param lines
     * @return
     */
    private List<String> getImports(List<String> lines) {
        return lines.stream()
                .filter(line -> line.matches("^\\s*import\\s+.*"))
                .collect(Collectors.toList());
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imports.size(); i++) {
            String anImport = imports.get(i);
            sb.append(anImport);
            if (i != imports.size() - 1) sb.append("\n");
        }
        return sb.toString() + super.toString();
    }



    public void sortImports() {
        Collections.sort(imports);
    }

    /**
     * Returns list of all classes within the file
     *
     * @return list of ClassObject's
     */
    public List<ClassObject> getClasses() {
        return body.stream().filter(x -> x instanceof ClassObject)
                .map(ClassObject.class::cast)
                .collect(Collectors.toList());
    }


}

