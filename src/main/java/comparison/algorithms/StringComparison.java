package comparison.algorithms;

import normalisation.elements.elementContainers.JavaFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StringComparison implements ComparisonAlgorithm {


    /**
     * Executes the Running-Karp-Rabin-Greedy-String-Tiling algorithm
     *
     * @param file1
     * @param file2
     * @return
     */
    public double compareFiles(JavaFile file1, JavaFile file2) {
        return 0d;
    }


    public List<Match> RKR_GST(String T, String P, int min_match_len, int initial_search_len) {

        List<Match> tiles = new ArrayList();
        List<Queue<Match>> match_queues =new ArrayList<>();

        int search_len = initial_search_len;
        MarkedArray T_vals = new MarkedArray(T);
        MarkedArray P_vals = new MarkedArray(P);
        while (true) {

            int l_max = scanPattern(match_queues, T_vals, P_vals, search_len);
            if (l_max > (2 * search_len)) search_len = l_max;
            else {
                markStrings(search_len);
                if (search_len > 2 * min_match_len) search_len /= 2;
                else if (search_len > min_match_len) search_len = min_match_len;
                else break;
            }

        }

        return tiles;
    }


    private static void markStrings(int search_len) {

    }

    private static int scanPattern(List<Queue<Match>> match_queues, MarkedArray T, MarkedArray P, int search_len) {

        int longest_match = 0;
        Queue<Match> match_queue = new LinkedList<>();
        CustomHashMap map = new CustomHashMap();

        boolean no_next_tile = false;
        int i = 0;
        while(i<T.size()){
            if(T.isMarked(i)){
                i++;
                continue;
            }

            int distance_to_tile = T.getNextTileIndex(i);
            if(distance_to_tile == -1) {
                distance_to_tile = T.size() - i;
                no_next_tile = true;
            };
        }

        return 0;
    }


    private static boolean isOccluded(Match match, List<Match> tiles) {
        for (Match tile : tiles) {
            return (Math.max(match.p_pos, tile.p_pos) <= Math.min(match.p_pos+match.len, tile.p_pos+tile.len)
                    ||
                    Math.max(match.s_pos, tile.s_pos) <= Math.min(match.s_pos+match.len, tile.s_pos+tile.len)
            );

        }
        return false;
    }


}

