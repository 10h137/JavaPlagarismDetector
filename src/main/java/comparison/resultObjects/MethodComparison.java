package comparison.resultObjects;

import normalisation.elements.elementContainers.Method;
import normalisation.elements.Variable;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class MethodComparison  implements Comparable<MethodComparison>{

    int var_count_score;
    int var_type_score;
    int var_name_score;
    int exact_declaration_score;
    int line_count_score;
    int method_size_score;
    int string_similarity;
    public Method m1;
    public Method m2;

    /**
     *  Compares two methods on a range of properties (matching variable count etc) and calculates set of scores
     * @param m1 1st method
     * @param m2 2nd method
     */
    public MethodComparison(Method m1, Method m2){

        this.m1 = m1;
        this.m2 = m2;

        int m1_var_count = m1.getVariables().size();
        int m2_var_count = m2.getVariables().size();
        var_count_score = (int) (((double) Math.min(m1_var_count, m2_var_count) / (double) Math.max(m1_var_count, m2_var_count)) * 100);

        List<String> m1_types = m1.getVariables().stream().map(Variable::getType).collect(Collectors.toList());
        List<String> m2_types = m1.getVariables().stream().map(Variable::getType).collect(Collectors.toList());
        var_type_score = getMatchScore(m1_types, m2_types);

        List<String> m1_names = m1.getVariables().stream().map(Variable::getName).collect(Collectors.toList());
        List<String> m2_names = m1.getVariables().stream().map(Variable::getName).collect(Collectors.toList());
        var_name_score = getMatchScore(m1_names, m2_names);

        List<String> m1_dec = m1.getVariables().stream().map(Variable::getDeclaration).collect(Collectors.toList());
        List<String> m2_dec = m1.getVariables().stream().map(Variable::getDeclaration).collect(Collectors.toList());
        exact_declaration_score = getMatchScore(m1_dec, m2_dec);

        int m1_line_count = m1.body.size();
        int m2_line_count = m2.body.size();
        line_count_score = (int) (((double) Math.min(m1_line_count, m2_line_count) / (double) Math.max(m1_line_count, m2_line_count)) * 100);

        method_size_score = (int) (((double) Math.min(m1.length(), m2.length()) / (double) Math.max(m1.length(), m2.length())) * 100);

        string_similarity = (int) (StringUtils.getJaroWinklerDistance(m1.toString(), m2.toString()) * 100);
    }

    /**
     * This method counts the number of matching pairs between two string lists and returns a match percentage
     * @param s1 1st list of strings
     * @param s2 2nd list of strings
     * @return integer 0-100%
     */
    public int getMatchScore(List<String> s1, List<String> s2) {
        int var_type_match_count = 0;
        for (int i = 0; i < s1.size(); i++) {
            String m1_type = s1.get(i);
            for (int j = 0; j < s2.size(); j++) {
                String m2_type = s2.get(j);
                if (m1_type.equals(m2_type)) {
                    s1.remove(i);
                    s2.remove(j);
                    var_type_match_count++;
                    break;
                }

            }
        }
        return (int) (((double) var_type_match_count / (double) Math.max(s1.size(), s2.size())) * 100);
    }




    @Override
    public int compareTo(MethodComparison methodComparison) {
        return this.getTotalScore() - methodComparison.getTotalScore();
    }

    /**
     *  Combines all scores into a singe int value
     * @return combined comparison score 0-100
     */
    public int getTotalScore(){
        return (var_count_score + var_type_score + var_name_score + exact_declaration_score + line_count_score + method_size_score + string_similarity) / 7;
    }



    public String getReport(){

        StringBuilder sb = new StringBuilder();
        sb.append("Comparing " + m1.getName() + " and " + m2.getName() + "\n");
        sb.append("Variable count ");
        sb.append(var_count_score + "\n");
        sb.append("Type count ");
        sb.append(var_type_score + "\n");
        sb.append("Variable Name match ");
        sb.append(var_name_score + "\n");
        sb.append("Declaration match ");
        sb.append(exact_declaration_score + "\n");
        sb.append("Line count ");
        sb.append(line_count_score + "\n");
        sb.append("Method Size ");
        sb.append(method_size_score + "\n");
        sb.append("String comparison ");
        sb.append(string_similarity + "\n");

        return sb.toString();

    }

}
