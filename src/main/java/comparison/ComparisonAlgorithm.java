package comparison;

public interface ComparisonAlgorithm {


    default java.util.List<ComparisonResult> compareFiles(java.util.List<java.io.File> files) {
        return null;
    }


}
