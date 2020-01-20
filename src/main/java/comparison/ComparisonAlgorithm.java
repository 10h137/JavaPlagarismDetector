package comparison;

import normalisation.elements.elementContainers.JavaFile;

public interface ComparisonAlgorithm {


    default ComparisonResult compareFiles(JavaFile file1, JavaFile file2) {
        return null;
    }


}
