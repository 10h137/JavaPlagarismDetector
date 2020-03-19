package comparison.algorithms;

import normalisation.elements.elementContainers.JavaFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StringComparison implements ComparisonAlgorithm {


    public static List<Queue<Match>> match_list = new ArrayList<>();
    private static List<Match> tiles;

    /**
     * Executes the Running-Karp-Rabin-Greedy-String-Tiling algorithm
     *
     * @param file1
     * @param file2
     * @return
     */
    public double compareFiles(JavaFile file1, JavaFile file2) {

        String longer=(file1.toString().length() >= file2.toString().length()) ? file1.toString() : file2.toString();
        String shorter = (file1.toString().length() > file2.toString().length()) ? file2.toString() : file1.toString();
        tiles = RKR_GST(longer, shorter, 5, 20);

        MarkedArray T_vals = new MarkedArray(longer);
        MarkedArray P_vals = new MarkedArray(shorter);
        return getScore(T_vals, P_vals);


//        return 0d;
    }

    public double getScore(MarkedArray P, MarkedArray T){
        int coverage = tiles.stream().map(x -> x.len).reduce(Integer::sum).orElse(0);
        return ((double) 2* coverage)/(double)(P.size() + T.size());
    }

    public List<Match> RKR_GST(String T, String P, int min_match_len, int initial_search_len) {

        List<Match> tiles = new ArrayList<>();
        List<Queue<Match>> match_queues = new ArrayList<>();

        int search_len = initial_search_len;
        MarkedArray T_vals = new MarkedArray(T);
        MarkedArray P_vals = new MarkedArray(P);
        while (true) {

            int l_max = scanPattern(match_queues, T_vals, P_vals, search_len);
            if (l_max > (2 * search_len)) search_len = l_max;
            else {
                markStrings(T_vals, P_vals);
                if (search_len > 2 * min_match_len) search_len /= 2;
                else if (search_len > min_match_len) search_len = min_match_len;
                else break;
            }

        }

        return tiles;
    }

    private static int scanPattern(List<Queue<Match>> match_queues, MarkedArray TextString, MarkedArray PatternString, int search_len) {

        int longest_match = 0;
        Queue<Match> match_queue = new LinkedList<>();
        CustomHashMap map = new CustomHashMap();

        boolean no_next_tile = false;
        int current_index = 0;
        while (current_index < TextString.size()-1) {
            if (TextString.isMarked(current_index)) {
                current_index++;
                continue;
            }

            int distance_to_tile = TextString.getNextTileIndex(current_index) - current_index;
            if (distance_to_tile < 0) {
                distance_to_tile = TextString.size() - current_index;
                no_next_tile = true;
            }

            if (distance_to_tile < search_len) {
                if (no_next_tile) {
                    current_index = TextString.size();
                } else {
                    current_index = TextString.getNextUnmarkedTokenIndex(current_index);
                    if (current_index == -1) current_index = TextString.size();
                }
            } else {

                map.add(generateHash(TextString.getString(current_index, current_index + search_len - 1)), current_index);
                current_index++;
            }

        }


        no_next_tile = false;
        current_index = 0;
        while (current_index < PatternString.size()) {

            if (PatternString.isMarked(current_index)) {
                current_index++;
                continue;
            }

            int distance_to_tile = PatternString.getNextTileIndex(current_index) - current_index;
            if (distance_to_tile < 0) {
                distance_to_tile = PatternString.size() - current_index;
                no_next_tile = true;
            }

            if (distance_to_tile < search_len) {
                if (no_next_tile) {
                    current_index = PatternString.size();
                } else {
                    current_index = PatternString.getNextUnmarkedTokenIndex(current_index);
                    if (current_index < 0) {
                        current_index = PatternString.size();
                    }
                }
            } else {

                String str = PatternString.getString(current_index, current_index + search_len - 1);
                int hash = generateHash(str);
                List<Integer> vals = map.get(hash);
                if (vals != null) {
                    for (Integer val : vals) {
                        String T_string = TextString.getString(val, val + search_len - 1);
                        if (T_string.equals(str)) {
                            int T_index = val;
                            int new_search_len = search_len;

                            while (current_index + new_search_len < PatternString.size() && T_index + new_search_len < TextString.size()
                                    && PatternString.get(current_index + new_search_len).equals(TextString.get(T_index + new_search_len))
                                    && !PatternString.isMarked(current_index + new_search_len)
                                    && !TextString.isMarked(T_index + new_search_len)) {
                                new_search_len++;
                            }

                            if (new_search_len > 2 * search_len) return new_search_len;
                            else {
                                if (longest_match < search_len) {
                                    longest_match = search_len;
                                }
                                Match match = new Match(current_index, T_index, new_search_len);
                                match_queue.add(match);
                            }
                        }

                    }
                }

                current_index++;

            }



        }


        if (match_queue.isEmpty()) {
            match_list.add(match_queue);
        }
        return longest_match;
    }

    private static void markStrings(MarkedArray T, MarkedArray P) {
        for (Queue<Match> queue : match_list) {
            while (!queue.isEmpty()) {
                Match match = queue.poll();
                if (!isOccluded(match, tiles)) {
                    for (int j = 0; j < match.len; j++) {
                        P.mark(match.p_pos + j);
                        T.mark(match.s_pos + j);
                    }
                    tiles.add(match);
                }
            }
        }
        match_list.clear();
    }

    private static int generateHash(String str) {
        int hashValue = 0;
        for (int i = 0; i < str.length(); i++) {
            hashValue = ((hashValue << 1) + (int) str.charAt(i));
        }
        return hashValue;
    }

    private static boolean isOccluded(Match match, List<Match> tiles) {
        for (Match tile : tiles) {
            return (Math.max(match.p_pos, tile.p_pos) <= Math.min(match.p_pos + match.len, tile.p_pos + tile.len)
                    ||
                    Math.max(match.s_pos, tile.s_pos) <= Math.min(match.s_pos + match.len, tile.s_pos + tile.len)
            );

        }
        return false;
    }


}

