package comparison.resultObjects;

import com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash;
import normalisation.elements.Variable;
import normalisation.elements.elementContainers.Method;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash.SimilarityTypes.ARITHMETIC_MEAN;

/**
 *
 */
public class MethodComparison implements Serializable {

    public final Method m1;
    public final Method m2;
    private final int var_count_score;
    private final int var_type_score;
    private final int var_name_score;
    private final int exact_declaration_score;
    private final int line_count_score;
    private final int method_size_score;
    private final int string_similarity;

    /**
     * Compares two methods on a range of properties (matching variable count etc) and calculates set of scores
     *
     * @param m1 1st method
     * @param m2 2nd method
     */ 
    public MethodComparison(Method m1, Method m2) {

        this.m1 = m1;
        this.m2 = m2;

        int m1_var_count = m1.getVariables().size();
        int m2_var_count = m2.getVariables().size();
        var_count_score = (int) (((double) Math.min(m1_var_count, m2_var_count) / (double) Math.max(m1_var_count, m2_var_count)) * 100);

        List<String> m1_types = m1.getTypeList();
        List<String> m2_types = m2.getTypeList();
        var_type_score = getMatchScore(m1_types, m2_types);

        List<String> m1_names = m1.getNameList();
        List<String> m2_names = m2.getNameList();
        var_name_score = getMatchScore(m1_names, m2_names);

        List<String> m1_dec = m1.getDecList();
        List<String> m2_dec = m2.getDecList();
        exact_declaration_score = getMatchScore(m1_dec, m2_dec);

        int m1_line_count = m1.body.size();
        int m2_line_count = m2.body.size();
        line_count_score = (int) (((double) Math.min(m1_line_count, m2_line_count) / (double) Math.max(m1_line_count, m2_line_count)) * 100);

        method_size_score = (int) (((double) Math.min(m1.length(), m2.length()) / (double) Math.max(m1.length(), m2.length())) * 100);

        string_similarity = (int) (m1.getHash().similarity(m2.getHash(),ARITHMETIC_MEAN) * 100);
    }

    /**
     * Counts the number of matching pairs between two string lists and returns a match percentage
     *
     * @param s1 1st list of strings
     * @param s2 2nd list of strings
     * @return integer 0-100%
     */
    private int getMatchScore(List<String> s1, List<String> s2) {
        int s1_size = s1.size();
        int s2_size = s2.size();

        Map<String, Integer> p1 = new HashMap<>();
        Map<String, Integer> p2 = new HashMap<>();

        s1.forEach(s -> p1.put(s, p1.containsKey(s) ? p1.get(s) + 1 : 0));

        s2.forEach(s -> p2.put(s, p2.containsKey(s) ? p2.get(s) + 1 : 0));

        int count = 0;
        for (String s : s1) {
          if(p2.containsKey(s) && p1.containsKey(s)){
              int new_p2_count = p2.get(s)- 1;
              int new_p1_count = p1.get(s)- 1;
              count++;
              if(new_p2_count == 0 || new_p1_count == 0){
                  p2.remove(s);
                  p1.remove(s);
              }else{
                  p1.put(s,new_p1_count);
                  p2.put(s,new_p2_count);
              }

          }
        }

        return (int) (((double) count / (double) Math.max(s1_size, s2_size)) * 100);
    }

    public Method getM1() {
        return m1;
    }

    public Method getM2() {
        return m2;
    }

    /**
     * Combines all scores into a singe int value
     *
     * @return combined comparison score 0-100
     */
    public int getTotalScore() {
        return (var_count_score + var_type_score + var_name_score + exact_declaration_score + line_count_score + method_size_score + string_similarity) / 7;
    }


    public String getReport() {

        StringBuilder sb = new StringBuilder();
        sb.append("Comparing ").append(m1.getName()).append(" and ").append(m2.getName()).append("\n");
        sb.append("Variable count ");
        sb.append(var_count_score).append("%\n");
        sb.append("Type count ");
        sb.append(var_type_score).append("%\n");
        sb.append("Variable Name match ");
        sb.append(var_name_score).append("%\n");
        sb.append("Declaration match ");
        sb.append(exact_declaration_score).append("%\n");
        sb.append("Line count ");
        sb.append(line_count_score).append("%\n");
        sb.append("Method Size ");
        sb.append(method_size_score).append("%\n");
        sb.append("String comparison ");
        sb.append(string_similarity).append("%\n");

        return sb.toString();

    }

    /**
     * Generates a name string for the method comparison
     *
     * @return comparison name
     */
    public String getName() {
        return m1.getName() + " <--> " + m2.getName();
    }

}


