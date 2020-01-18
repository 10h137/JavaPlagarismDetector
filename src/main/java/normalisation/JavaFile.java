package normalisation;

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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imports.size(); i++) {
            String anImport = imports.get(i);
            sb.append(anImport);
            if(i!=imports.size()-1) sb.append("\n");
        }
        return sb.toString() + super.toString();
    }
    /**
     * Normalises whitespace and numbers lines
     *
     * @param lines
     * @return
     */
    static List<String> preProcess(List<String> lines) {


        // pads brackets with spaces
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).replaceAll("\\(", " ( "));
            lines.set(i, lines.get(i).replaceAll("\\)", " ) "));
            lines.set(i, lines.get(i).replaceAll("\\{", " { "));
            lines.set(i, lines.get(i).replaceAll("}", " } "));
            lines.set(i, lines.get(i).replaceAll(";", " ; "));
            lines.set(i, lines.get(i).replaceAll("=", " = "));
            lines.set(i, lines.get(i).replaceAll("=\\s*=", " == "));

            String line = lines.get(i);
            char[] charArray = line.toCharArray();
            for (int j = 0; j < charArray.length; j++) {
                char c = charArray[j];
                if(c == '{') {
                    String start = line.substring(0, j+1);
                    String end = line.substring(j+1, line.length() - 1);
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


        // number lines in file to allow for comparison to original after normalisation
        for (int i = 0; i < lines.size(); i++) {
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

