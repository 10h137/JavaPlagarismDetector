//package normalisation;
//
//import com.google.common.reflect.ClassPath;
//import org.apache.commons.io.FileUtils;
//import org.javatuples.Pair;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.lang.reflect.InvocationTargetException;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class NewNormaliser {
//
//    final Set<Features> enabled_features;
//    final String output_directory;
//
//    /**
//     * Constructor for normaliser sets teh enabled features and the output directory for the normalised files
//     *
//     * @param enabled_features set of feature enums representing steps to be applied during the normalisation process
//     * @param output_directory directory for the normalised files to be stored
//     */
//    public NewNormaliser(Set<Features> enabled_features, String output_directory) {
//        this.enabled_features = enabled_features;
//        this.output_directory = output_directory;
//    }
//
//    /**
//     * Removes all comment from a file, this includes single & multi-line
//     *
//     * @param file list of file file
//     */
//    static JavaFile removeComments(JavaFile file) {
//
//    }
//
//
//
//    /**
//     * Orders the class members (methods, global variables) by length
//     *
//     * @param file list of file file
//     */
//    static JavaFile sortClassMembers(JavaFile file) {
//
//
//    }
//
//
//    /**
//     * Orders the import file alphabetically
//     *
//     * @param file list of file file
//     */
//    static JavaFile orderImports(JavaFile file) {
//        file.sortImports();
//        return file;
//    }
//
//    /**
//     * Standardises the method names
//     *
//     * @param file list of file file
//     */
//    static JavaFile standardiseMethodNames(JavaFile file) {
//        return null;
//    }
//
//
//    /**
//     * Reduces data types to their base types e.g. long -> Integer ; double -> Float
//     *
//     * @param file list of file file
//     */
//    static JavaFile reduceDataTypes(JavaFile file) {
//        return replaceInterfaces(file, "java.lang");
//    }
//
//    /**
//     * Reduces data structures to their interfaces e.g. HashMap -> Map ; LinkedList -> List
//     *
//     * @param file list of file file
//     */
//    static JavaFile reduceStructures(JavaFile file) {
//        return replaceInterfaces(file, "java.util");
//    }
//
//
//    // not working
//    static JavaFile replaceInterfaces(JavaFile file, String package_dir) {
////        JavaFile result = new ArrayList<>();
////        Map<String, Set<String>> sub_class_map = new HashMap<>();
////
////
////        ClassPath classpath = null;
////        try {
////            classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        System.out.println(classpath.getTopLevelClasses("java.util").length());
////        Set<Class> allClasses = classpath.getTopLevelClasses()
////                .stream()
////                .peek(x -> System.out.println(x.getResourceName()))
////                .map(ClassPath.ClassInfo::load)
////                .filter(Class::isInterface)
////                .collect(Collectors.toSet());
////
////
//////        // for every interface, get all implementations and store the mapping in hash map
//////        for (Class current_class : allClasses) {
//////                    Set<String> current_sub_classes = reflections.getSubTypesOf(current_class).stream()
//////                    .map(Class::getSimpleName)
//////                    .collect(Collectors.toSet());
//////
//////            sub_class_map.put(current_class.getSimpleName(), current_sub_classes);
//////        }
////
////
////        // removes sub-interface implementations from higher level interfaces
////        // e.g all ordered map implementations will be removed from the Map interfaces set of subclasses
////        // TODO May not be necessary ??
////        for (String current_class : sub_class_map.keySet()) {
////
////            Set<String> current_sub_classes = sub_class_map.get(current_class);
////            for (Set<String> class_list : sub_class_map.values()) {
////                if (class_list.contains(current_class)) {
////                    class_list.removeAll(current_sub_classes);
////                    class_list.remove(current_class);
////                }
////            }
////        }
////
////
////        // checks for occurrences of all mapped subclasses and replaces them with interface names
////        for (int i = 0; i < file.length(); i++) {
////            String line = file.get(i);
////            for (String interface_class : sub_class_map.keySet()) {
////                Set<String> sub_class_set = sub_class_map.get(interface_class);
////                for (String sub_class : sub_class_set) {
////                    result.add(i, line.replaceAll(sub_class, interface_class));
////                }
////            }
////        }
//        return file;
//    }
//
//
//    /**
//     * Performs normalisation on a file by first copying it and then applying all enabled features
//     *
//     * @param input
//     * @return
//     * @throws IOException
//     */
//    public java.io.File normaliseFile(java.io.File input) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        // make copy of file
//        File output_file = new File(output_directory + "");
//        FileUtils.copyFile(input, output_file);
//        JavaFile file = new JavaFile(output_file);
//        // perform enabled normalisation features
//        for (Features enabled_feature : enabled_features) {
//            file = enabled_feature.perform(file);
//        }
//
//        // writes normalised file
//        PrintWriter wr = new PrintWriter(output_file);
//        wr.write(file.toString());
//
//        return output_file;
//    }
//
//
//    /**
//     * Enums representing the normalisation features, each one has a connected method
//     */
//    public enum Features {
//        REMOVE_COMMENTS,
//        SORT_CLASS_MEMBERS,
//        ORDER_IMPORTS,
//        STANDARDISE_METHOD_NAMES,
//        REDUCE_DATA_TYPES,
//        REDUCE_STRUCTURES;
//
//        /**
//         * Methods to perform the corresponding normalisation process
//         *
//         * @param file list of file file
//         */
//        JavaFile perform(JavaFile file) {
//            switch (this) {
//
//                case REMOVE_COMMENTS:
//                    return removeComments(file);
//                case SORT_CLASS_MEMBERS:
//                    return sortClassMembers(file);
//                case ORDER_IMPORTS:
//                    return orderImports(file);
//                case STANDARDISE_METHOD_NAMES:
//                    return standardiseMethodNames(file);
//                case REDUCE_DATA_TYPES:
//                    return reduceDataTypes(file);
//                case REDUCE_STRUCTURES:
//                    return reduceStructures(file);
//                default:
//                    return file;
//            }
//        }
//
//    }
//}
//
//
//
//
//
//
