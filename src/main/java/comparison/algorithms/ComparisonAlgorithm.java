package comparison.algorithms;

import normalisation.elements.elementContainers.JavaFile;

public interface ComparisonAlgorithm {



    default double compareFiles(JavaFile file1, JavaFile file2) {
        return 0;
    }


}
