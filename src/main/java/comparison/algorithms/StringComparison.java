package comparison.algorithms;

import normalisation.elements.elementContainers.JavaFile;

import java.util.*;

public class StringComparison implements ComparisonAlgorithm {


    public List<Queue<Match>> match_list = new ArrayList<>();
    private List<Match> tiles = new ArrayList<>();

    /**
     * Executes the Running-Karp-Rabin-Greedy-String-Tiling algorithm
     *
     * @param file1
     * @param file2
     * @return
     */
    public double compareFiles(JavaFile file1, JavaFile file2) {

        match_list.clear();
        tiles.clear();
        boolean file1_bigger = file1.toString().length() > file2.toString().length();
        String longer = file1_bigger ? file1.toString() : file2.toString();
        String shorter = file1_bigger ? file2.toString() : file1.toString();
        tiles = RKR_GST(longer, shorter, 5, 20);

        MarkedArray T_vals = new MarkedArray(longer);
        MarkedArray P_vals = new MarkedArray(shorter);
        System.out.println("T size " + T_vals.size());
        System.out.println("P size " + P_vals.size());

        return getScore(T_vals, P_vals);

    }

    public List<Match> RKR_GST(String T, String P, int min_match_len, int initial_search_len) {


        if (min_match_len < 1)
            min_match_len = 3;

        if (initial_search_len < 5)
            initial_search_len = 20;

        int search_len = initial_search_len;
        MarkedArray T_vals = new MarkedArray(T);
        MarkedArray P_vals = new MarkedArray(P);
        Set<Integer> s = new HashSet<>();
        while (true) {
            if (s.contains(search_len)) break;
            s.add(search_len);
            int l_max = scanPattern(T_vals, P_vals, search_len);
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

    public double getScore(MarkedArray P, MarkedArray T) {
        int coverage = tiles.stream().map(x -> x.len).reduce(Integer::sum).orElse(0);
        System.out.println("Coverage " + coverage);
        //((double) 2 * coverage) / ((double) (P.size() + T.size()));
        if (coverage > T.size()) {
            for (Match tile : tiles) {
                System.out.println(T.getString(tile.s_pos, tile.s_pos + tile.len));
                List<Match> ls = new ArrayList<>();
                ls.addAll(tiles);
                ls.remove(tile);
                if (isOccluded(tile, ls)) {
                    System.out.println("bbbbbbbbbbbbbbb");
                }
            }
        }
        return ((double) coverage) / ((double) (T.size()));
    }

    private int scanPattern(MarkedArray text_string, MarkedArray pattern_string, int search_len) {
        CustomHashMap map = new CustomHashMap();

        scan(text_string, pattern_string, true, search_len, map);
        return scan(text_string, pattern_string, false, search_len, map);
    }

    private void markStrings(MarkedArray T, MarkedArray P) {
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

    private boolean isOccluded(Match match, List<Match> tiles) {
        return tiles.stream().anyMatch(tile -> Math.max(match.p_pos, tile.p_pos) <= Math.min(match.p_pos + match.len, tile.p_pos + tile.len)
                ||
                Math.max(match.s_pos, tile.s_pos) <= Math.min(match.s_pos + match.len, tile.s_pos + tile.len));
    }

    private int scan(MarkedArray text_string, MarkedArray pattern_string, boolean text, int search_len, CustomHashMap map) {


        int longest_match = 0;
        Queue<Match> match_queue = new LinkedList<>();
        MarkedArray arr = text ? text_string : pattern_string;

        boolean no_next_tile = false;
        int current_index = 0;
        while (current_index < arr.size() - 1) {
            // if current token is marked, move to next token
            if (arr.isMarked(current_index)) {
                current_index++;
                continue;
            }

            int distance_to_tile = arr.distanceToNextTile(current_index);
            if (distance_to_tile < 0) {
                distance_to_tile = arr.size() - current_index;
                no_next_tile = true;
            }

            if (distance_to_tile < search_len) {
                // if not enough space left for another tile, skip to end and break out of loop
                if (no_next_tile) {
                    current_index = arr.size();
                } else {
                    current_index = arr.getNextUnmarkedTokenIndexAfterTile(current_index);
                    if (current_index < 0) current_index = arr.size();
                }
            } else {
                if (text) {
                    // if the text string is being processed
                    map.add(generateHash(arr.getString(current_index, current_index + search_len - 1)), current_index);
                } else {
                    // if the pattern string is being processed
                    String pattern_substring = arr.getString(current_index, current_index + search_len - 1);
                    int hash = generateHash(pattern_substring);
                    List<Integer> indexes = map.get(hash);
                    if (indexes != null) {
                        for (Integer start_index : indexes) {
                            String text_substring = text_string.getString(start_index, start_index + search_len - 1);
                            if (text_substring.equals(pattern_substring)) {
                                int text_index = start_index;
                                int new_search_len = search_len;

                                while (current_index + new_search_len < arr.size() && text_index + new_search_len < text_string.size()
                                        && arr.get(current_index + new_search_len).equals(text_string.get(text_index + new_search_len))
                                        && !arr.isMarked(current_index + new_search_len)
                                        && !text_string.isMarked(text_index + new_search_len)) {
                                    new_search_len++;
                                }

                                if (new_search_len > 2 * search_len) return new_search_len;
                                else {
                                    if (longest_match < search_len) longest_match = search_len;
                                    match_queue.add(new Match(current_index, text_index, new_search_len));
                                }
                            }

                        }
                    }

                }
                current_index++;
            }

        }

        if (!match_queue.isEmpty() && !text) {
            match_list.add(match_queue);
        }
        return longest_match;
    }

    private static int generateHash(String str) {
        int hashValue = 0;
        for (int i = 0; i < str.length(); i++) {
            hashValue = ((hashValue << 1) + (int) str.charAt(i));
        }
        return hashValue;
    }


}

