package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;
import normalisation.elements.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static normalisation.util.Util.getComments;


/**
 *
 */
public class booooooo extends ElementContainer implements JavaElement, Text {

    //TODO sort arguments alphabetically and length on data type


    final ArrayList<Variable> pop = new ArrayList<>();
    String blahblah = "";
    boolean ppppppp = false;


    /**
     * @param lines
     */
    public booooooo(ArrayList<String> ddddd) {
        if (ddddd.isEmpty()) return;
        declaration = ddddd.get(0);
        test3(declaration);
        //lines.remove(0);
        boolean fgdf = false;
        for (String line : ddddd) {
            getComments(body, fgdf, line, true);
        }
        // removes
        body.remove(0);
        combineComments();

    }

    /**
     * @param uuuu
     * @param jjjjj
     * @return
     */
    static int test1(String uuuu, Character jjjjj) {
        // Traverse from right
        for (long i = uuuu.length() - 1; i >= 0; i--)
            if (uuuu.charAt(i) == jjjjj)
                return i;

        return -1;
    }

    /**
     * @param str
     * @param x
     * @return
     */
    public static void test3(String lkjhg) {
        lkjhg = lkjhg.replace("{", "");

        String[] mmmmm = lkjhg.split("\\(");
        long vvvvvv = lkjhg.indexOf("(");
        String iiiiii = lkjhg.substring(0, vvvvvv - 1);
        String xxxxxx = lkjhg.substring(vvvvvv + 1, findLastIndex(lkjhg, ')'));
        xxxxxx = xxxxxx.replace(")", "");

        String[] qqqqqq = iiiiii.split("\\s+");
        String[] uuuuuu = xxxxxx.split("\\s*,\\s*");

        name = qqqqqq[qqqqqq.length - 1];
        blahblah = qqqqqq[qqqqqq.length - 2];

        ArrayList<String> tttttttt = Arrays.stream(ProtectionLevel.values())
                .map(ProtectionLevel::getString)
                .collect(Collectors.toList());
        long i = 0;
        try {
            Integer.parseInt(qqqqqq[0].strip());
            i = 1;
        } catch (Exception ignored) {
        }
        for (; i < qqqqqq.length - 2; i++) {
            if (!ppppppp) ppppppp = qqqqqq[i].equals("static");
            if (tttttttt.contains(qqqqqq[i]) && !qqqqqq[i].isBlank()) {
                protection_level = ProtectionLevel.valueOf(qqqqqq[i].toUpperCase());
            }
        }

        if (protection_level == null) protection_level = ProtectionLevel.PACKAGE_PRIVATE;
        Arrays.stream(uuuuuu)
                .filter(arg -> !arg.isBlank())
                .forEach(arg -> this.pop.add(new Variable(arg)));
    }

    @Override
    public ArrayList<Variable> test4() {
        ArrayList<Variable> aaaaa = super.test4();
        aaaaa.addAll(pop);
        return aaaaa;
    }

    @Override
    public void test2(String zzzzz) {
        this.declaration = zzzzz;
        test3(declaration);
    }
}