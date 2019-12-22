package normalisation;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.*;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Normaliser {

    private final Set<Features> enabled_features;
    private final String output_directory;

    /**
     * Constructor for normaliser sets teh enabled features and the output directory for the normalised files
     *
     * @param enabled_features set of feature enums representing steps to be applied during the normalisation process
     * @param output_directory directory for the normalised files to be stored
     */
    public Normaliser(Set<Features> enabled_features, String output_directory) {
        this.enabled_features = enabled_features;
        this.output_directory = output_directory;
    }

    /**
     * Removes all comments from a file, this includes single & multi-line
     *
     * @param lines list of file lines
     */
    private static void removeComments(List<String> lines) {
        boolean searching = false;
        for (String line : lines) {
            //multi-line comments open -> /*
            String multi_open = "^[0-9]*(\\s*/\\*.*)";
            // multi-line comments close -> */
            String multi_close = "^[0-9]*(\\s*\\*/.*)";
            // regular comment -> //
            String regular_comment = "^[0-9]*(\\s*//.*)";
            // check if it is instance of single line 'multi-line comment' e.g.
            /* comment */
            String multi_close_single = ".*\\*/$";


            if (searching) {
                if (line.matches(multi_close)) searching = false;
                lines.remove(line);
            } else if (line.matches(multi_open)) {
                if (line.matches(multi_close_single)) lines.remove(line);
                else searching = true;
            } else if (line.matches(regular_comment)) {
                lines.remove(line);
            }
        }

    }

    /**
     * Standardises the whitespace in the file by reducing any amount of whitespace to a single space
     * as well as removing any leading or trailing whitespace
     *
     * @param lines list of file lines
     */
    private static void standardiseWhitespace(List<String> lines) {
        for (String line : lines) {
            if (!line.isBlank()) {
                lines.set(lines.indexOf(line), line.replaceAll("\\s+", " ").strip().trim());
            }
        }
    }

    /**
     * Orders the class members (methods, global variables) by size
     *
     * @param lines list of file lines
     */
    private static void sortClassMembers(List<String> lines) {

        Stack<Character> brackets = new Stack<>();
        List<Pair<Integer, Integer>> methods = new ArrayList<>();
        List<String> other_lines = new ArrayList<>();
        boolean in_method = false;
        Pair<Integer, Integer> current_method = new Pair<>(0, 0);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.matches(".+\\(.*\\)\\s*\\{")) {
                in_method = true;
                current_method = current_method.setAt0(i);
            }

            if (in_method) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    if (aChar == '{' || aChar == '}') {
                        if (aChar != brackets.peek()) {
                            brackets.pop();
                            if (brackets.empty()) {
                                current_method = current_method.setAt1(i);
                                methods.add(current_method);
                                current_method = new Pair<>(0, 0);
                                in_method = false;
                                break;
                            }

                        } else {
                            brackets.push(aChar);
                        }
                    }
                }

            }

            if (!in_method) {
                other_lines.add(line);
            }
        }

        methods.sort(Comparator.comparingInt(objects -> (objects.getValue1() - objects.getValue0())));
        List<String> import_lines = other_lines.stream().filter(line -> line.contains("import")).collect(Collectors.toList());
        other_lines = other_lines.stream().filter(line -> !line.contains("import")).sorted().collect(Collectors.toList());

        List<String> new_order = new ArrayList<>(import_lines);
        new_order.addAll(other_lines);
        for (Pair<Integer, Integer> method : methods) {
            int start = method.getValue0();
            int end = method.getValue1();
            while (start < end) {
                new_order.add(lines.get(start));
                start++;
            }
        }

        lines = new_order;

    }


    /**
     * Orders the import lines alphabetically
     *
     * @param lines list of file lines
     */
    private static void orderImports(List<String> lines) {

        Map<String, Integer> import_lines = new TreeMap<>();
        lines.forEach(line -> {
            if (line.matches("^[0-9]*\\s*import.*")) {
                import_lines.put(line, lines.indexOf(line));
            }
        });
        import_lines.keySet()
                .forEach(line -> lines.set(import_lines.get(line), line));

    }

    /**
     * Standardises the method names
     *
     * @param lines list of file lines
     */
    private static void standardiseMethodNames(List<String> lines) {

    }


    /**
     * Reduces data types to their base types e.g. long -> Integer ; double -> Float
     *
     * @param lines list of file lines
     */
     static void reduceDataTypes(List<String> lines) {
        lines = replaceInterfaces(lines, "java.lang");
    }

    /**
     * Reduces data structures to their interfaces e.g. HashMap -> Map ; LinkedList -> List
     *
     * @param lines list of file lines
     */
     static void reduceStructures(List<String> lines) {
        lines = replaceInterfaces(lines, "java.util");
    }


    // not working
    private static List<String> replaceInterfaces(List<String> lines, String package_dir) {
        List<String> result = new ArrayList<>();
        Map<String, Set<String>> sub_class_map = new HashMap<>();
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .addUrls(ClasspathHelper.forJavaClassPath())
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(package_dir))));

        // gets all interface class objects from the java.util package
        // excludes the Collection interface as too generic
        Set<Class> allClasses =
                reflections.getSubTypesOf(Object.class)
                        .stream()
                        .filter(Class::isInterface)
                        .filter(c -> c != Collection.class)
                        .collect(Collectors.toSet());



        // for every interface, get all implementations and store the mapping in hash map
        for (Class<?> current_class : allClasses) {
            Set<String> current_sub_classes = reflections.getSubTypesOf(current_class).stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.toSet());

            sub_class_map.put(current_class.getSimpleName(), current_sub_classes);
        }


        // removes sub-interface implementations from higher level interfaces
        // e.g all ordered map implementations will be removed from the Map interfaces set of subclasses
        // TODO May not be necessary ??
        for (String current_class : sub_class_map.keySet()) {

            Set<String> current_sub_classes = sub_class_map.get(current_class);
            for (Set<String> class_list : sub_class_map.values()) {
                if (class_list.contains(current_class)) {
                    class_list.removeAll(current_sub_classes);
                    class_list.remove(current_class);
                }
            }
        }


        // checks for occurrences of all mapped subclasses and replaces them with interface names
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (String interface_class : sub_class_map.keySet()) {
                Set<String> sub_class_set = sub_class_map.get(interface_class);
                for (String sub_class : sub_class_set) {
                    result.add(i, line.replaceAll(sub_class, interface_class));
                }
            }
        }
        return result;
    }


    /**
     * Performs normalisation on a file by first copying it and then applying all enabled features
     *
     * @param input
     * @return
     * @throws IOException
     */
    public java.io.File normaliseFile(java.io.File input) throws IOException {
        // make copy of file
        File output_file = new File(output_directory + "");
        FileUtils.copyFile(input, output_file);

        Map<Integer, String> line_index_map = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(output_file.getAbsolutePath()));
        // number lines in file to allow for comparison to original after normalisation
        for (int i = 0; i < lines.size(); i++) {
            // add exception if starts with number
            String line = i + " " + lines.get(i);
            lines.set(i, line);
        }

        // perform enabled normalisation features
        enabled_features.forEach(feature -> feature.perform(lines));

        // writes normalised file
        Files.write(output_file.toPath(), lines, Charset.defaultCharset());

        return output_file;
    }


    /**
     * Enums representing the normalisation features, each one has a connected method
     */
    public enum Features {
        REMOVE_COMMENTS,
        STANDARDISE_WHITESPACE,
        SORT_CLASS_MEMBERS,
        ORDER_IMPORTS,
        STANDARDISE_METHOD_NAMES,
        REDUCE_DATA_TYPES,
        REDUCE_STRUCTURES;

        /**
         * Methods to perform the corresponding normalisation process
         *
         * @param lines list of file lines
         */
        private void perform(List<String> lines) {
            switch (this) {

                case REMOVE_COMMENTS:
                    removeComments(lines);
                    break;
                case STANDARDISE_WHITESPACE:
                    standardiseWhitespace(lines);
                    break;
                case SORT_CLASS_MEMBERS:
                    sortClassMembers(lines);
                    break;
                case ORDER_IMPORTS:
                    orderImports(lines);
                    break;
                case STANDARDISE_METHOD_NAMES:
                    standardiseMethodNames(lines);
                    break;
                case REDUCE_DATA_TYPES:
                    reduceDataTypes(lines);
                    break;
                case REDUCE_STRUCTURES:
                    reduceStructures(lines);
                    break;
            }
        }

    }
}






