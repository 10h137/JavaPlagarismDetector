package comparison.algorithms;

import comparison.resultObjects.ComparisonResult;
import normalisation.elements.elementContainers.JavaFile;

public interface ComparisonAlgorithm {


    default ComparisonResult compareFiles(JavaFile file1, JavaFile file2) {
        return null;
    }


}