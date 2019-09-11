import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Map;

/**
 * Prefix-Trie. Supports linear time find() and insert().
 * Should support determining whether a word is a full word in the
 * Trie or a prefix.
 *
 * @author
 */
public class Trie {

    private Node root;

    HashMap<String, Double> weightMap;
    public Trie() {
        root = new Node();
    }

    public boolean find(String s, boolean isFullWord) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException();
        }
        return root.find(s, isFullWord, 0);
    }

    public void insert(String s) {
        insert(s, 0.0);
    }
    public void insert(String s, double weight) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException();
        }
        root.insert(s, 0, weight);
    }



    public String getTopMatch(String prefix) {
        return root.getTopMatch(prefix);
    }


    public SortedSet<String> topMatches(String prefix, int k) {
        Node searchNode = root.getPrefixNode(prefix, 0);
        if (searchNode == null) {
            return new TreeSet<>();

        }
        PrefixNode firstNode = new PrefixNode(searchNode, prefix);
        PriorityQueue<PrefixNode> candidates = new PriorityQueue<>(
                new Comparator<PrefixNode>() {
                    @Override public int compare(PrefixNode p1, PrefixNode p2) {
                        double res = (p1.node.highestWeight - p2.node.highestWeight);
                        return res > 0 ? -1 : (res < 0 ? 1 : 0);
                    }
                }
        );
        SortedSet<String> words = new TreeSet<>((s1, s2) -> {
            double res = (weightMap.get(s1) - weightMap.get(s2));
            return res > 0 ? -1 : (res < 0 ? 1 : 0);
        });
        candidates.add(firstNode);
        topMatches(candidates, k, words);
        return words;
    }

    public void topMatches(PriorityQueue<PrefixNode> candidates, int k, SortedSet<String>
            words) {
        while (!candidates.isEmpty()) {
            PrefixNode topNode = candidates.poll();
            String w = topNode.prefix;
            Node parentNode = topNode.node;
            if (words.size() < k || parentNode.highestWeight > weightMap.get(words.last())) {
                if (parentNode.exists) {
                    // it's either the list is not full or the weight is heavier
                    if (words.size() < k) {
                        words.add(w);
                    } else {
                        words.remove(words.last());
                        words.add(w);
                    }
                }
                for (char c : parentNode.links.keySet()) {
                    Node nxtNode = parentNode.links.get(c);
                    PrefixNode childPrefixNode = new PrefixNode(nxtNode, w + c);
                    candidates.add(childPrefixNode);
                }

            } else {
                break;
            }
        }

    }

    private class Node {
        boolean exists;
        Map<Character, Node> links;
        double selfWeight;
        double highestWeight;

        Node() {
            links = new HashMap<>();
            selfWeight = 0.0;
            highestWeight = 0.0;
        }


        public boolean find(String s, boolean isFullWord, int count) {

            char comparedCharacter = s.charAt(count);
            if (links.containsKey(comparedCharacter)) {
                Node nextNode = links.get(comparedCharacter);

                // if the searched word reaches end
                if (count == s.length() - 1) {
                    if (isFullWord) {
                        return nextNode.exists;
                    }
                    return true;
                } else {
                    return nextNode.find(s, isFullWord, count + 1);
                }
            }
            return false;
        }

        public void insert(String s, int charPosition, double weight) {
            char comparedCharacter = s.charAt(charPosition);
            // reset the highest weight if necessary
            highestWeight = Math.max(highestWeight, weight);
            if (!links.containsKey(comparedCharacter)) {  //make a new branch
                Node nxtNode = new Node();
                nxtNode.highestWeight = weight;
                // put in the new pair of mapping
                links.put(comparedCharacter, nxtNode);
                // if reaching the last character then mark it exists
                if (charPosition == s.length() - 1) {
                    // the weight is then what is passed in
                    nxtNode.selfWeight = weight;
                    nxtNode.exists = true;
                } else {
                    nxtNode.insert(s, charPosition + 1, weight);
                }
            } else {
                Node followingNode = links.get(comparedCharacter);
                if (charPosition == s.length() - 1) {
                    followingNode.selfWeight = weight;
                    followingNode.highestWeight = Math.max(followingNode.highestWeight, weight);
                    followingNode.exists = true;
                } else {
                    followingNode.insert(s, charPosition + 1, weight);
                }
            }
        }

        public Node getPrefixNode(String prefix, int count) {
            Node point = this;
            if (count == prefix.length()) {
                return point;
            }
            char compare = prefix.charAt(count);
            if (!point.links.containsKey(compare)) {
                return null;
            } else {
                return point.links.get(compare).getPrefixNode(prefix, count + 1);
            }
        }

        public String getTopMatch(String prefix) {
            Node point = getPrefixNode(prefix, 0);
            if (point == null) {
                return null;
            }
            double highestweight = point.highestWeight;
            if (point.highestWeight != point.selfWeight) {
                while (point.selfWeight != highestweight) {
                    for (char key : point.links.keySet()) {
                        if (point.links.get(key).highestWeight == highestweight) {
                            point = point.links.get(key);
                            prefix = prefix + key;
                            break;
                        }
                    }
                }
            }
            return prefix;

        }
    }

    private class PrefixNode {
        private Node node;
        private String prefix;
        private PrefixNode(Node n, String pre) {
            node = n;
            prefix = pre;
        }
    }

}
