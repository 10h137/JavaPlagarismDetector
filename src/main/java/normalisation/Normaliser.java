package normalisation;

import normalisation.elements.elementContainers.JavaFile;

import java.util.EnumSet;

public class Normaliser {

    private final EnumSet<Features> enabled_features;

    /**
     * Constructor for normaliser sets teh enabled features and the output directory for the normalised files
     *
     * @param enabled_features set of feature enums representing steps to be applied during the normalisation process
     */
    public Normaliser(EnumSet<Features> enabled_features) {
        this.enabled_features = enabled_features;
    }


    /**
     * Performs normalisation on a file by first copying it and then applying all enabled features
     *
     * @param input
     */
    public void normaliseFile(JavaFile input) {
        // perform enabled normalisation features
        enabled_features.forEach(enabled_feature -> enabled_feature.perform(input));

    }


    /**
     * Enums representing the normalisation features, each one has a connected method
     */
    public enum Features {
        REMOVE_COMMENTS,
        SORT_CLASS_MEMBERS,
        ORDER_IMPORTS,
        STANDARDISE_METHOD_NAMES,
        REDUCE_TYPES,
        STANDARDISE_VARIABLE_NAMES;

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
                case REDUCE_TYPES:
                    file.replaceInterfaces();
                    break;
                case STANDARDISE_VARIABLE_NAMES:
                    file.normaliseVariables();
                default:
            }
        }

    }
}






