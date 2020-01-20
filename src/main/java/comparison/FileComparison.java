package comparison;

import normalisation.elements.elementContainers.JavaFile;
import normalisation.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FileComparison {


    private List<MethodComparison> method_comparisons = new ArrayList<>();
    private JavaFile file1, file2;
    double algorithm_score = 0;
    ComparisonResult res;


    public FileComparison(JavaFile file1, JavaFile file2, ComparisonAlgorithm alg){
        this.file1 = file1;
        this.file2 = file2;
        method_comparisons = Util.compareMethods(file1, file2);
        res = alg.compareFiles(file1, file2);
    }

}
