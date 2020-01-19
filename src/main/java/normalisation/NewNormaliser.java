package normalisation;

import com.google.common.reflect.ClassPath;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;

import javax.tools.FileObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class NewNormaliser {

    final Set<Features> enabled_features;
    final String output_directory;

    /**
     * Constructor for normaliser sets teh enabled features and the output directory for the normalised files
     *
     * @param enabled_features set of feature enums representing steps to be applied during the normalisation process
     * @param output_directory directory for the normalised files to be stored
     */
    public NewNormaliser(Set<Features> enabled_features, String output_directory) {
        this.enabled_features = enabled_features;
        this.output_directory = output_directory;
    }


    public List<MethodComparison> compareMethods(JavaFile file1, JavaFile file2){
        List<Method> methods1 = file1.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Method> methods2 = file2.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<Method> methods_to_be_processed = new HashSet<>(methods1);
        methods_to_be_processed.addAll(methods2);

        List<MethodComparison> comparisons = new ArrayList<>();
        for (int i = 0; i < methods1.size(); i++) {
            for (int j = i+1; j < methods2.size(); j++) {
                comparisons.add(new MethodComparison(methods1.get(i), methods2.get(j)));
            }
        }

        List<MethodComparison> best_comparisons = new ArrayList<>();
        // TODO check if in descending order
        comparisons.sort(Collections.reverseOrder());


        for (MethodComparison comparison : comparisons) {
            if(methods_to_be_processed.contains(comparison.m1) && methods_to_be_processed.contains(comparison.m1)){
                best_comparisons.add(comparison);
            }
            methods_to_be_processed.remove(comparison.m1);
            methods_to_be_processed.remove(comparison.m2);
        }

        return  best_comparisons;

    }


    /**
     * Performs normalisation on a file by first copying it and then applying all enabled features
     *
     * @param input
     * @return
     * @throws IOException
     */
    public java.io.File normaliseFile(java.io.File input) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // make copy of file
        File output_file = new File(output_directory + "");
        FileUtils.copyFile(input, output_file);
        JavaFile file = new JavaFile(output_file);

        // perform enabled normalisation features
        enabled_features.forEach(enabled_feature -> enabled_feature.perform(file));

        // writes normalised file
        PrintWriter wr = new PrintWriter(output_file);
        wr.write(file.toString());

        return output_file;
    }


    /**
     * Enums representing the normalisation features, each one has a connected method
     */
    public enum Features {
        REMOVE_COMMENTS,
        SORT_CLASS_MEMBERS,
        ORDER_IMPORTS,
        STANDARDISE_METHOD_NAMES,
        REDUCE_DATA_TYPES,
        REDUCE_STRUCTURES;

        /**
         * Methods to perform the corresponding normalisation process
         *
         * @param file list of file file
         */
        void perform(JavaFile file) {
            switch (this) {

                case REMOVE_COMMENTS:
                    file.removeComments();
                    break;
                case SORT_CLASS_MEMBERS:
                    file.sortElements();
                    break;
                case ORDER_IMPORTS:
                    file.sortImports();
                    break;
                case STANDARDISE_METHOD_NAMES:
                    file.normaliseMethodNames();
                    break;
                case REDUCE_DATA_TYPES:
                    file.replaceInterfaces();
                    break;
                case REDUCE_STRUCTURES:
                default:
            }
        }

    }
}






