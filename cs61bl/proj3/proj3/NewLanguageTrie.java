
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by yonzhang on 8/3/17.
 */

public class NewLanguageTrie {

    private ArrayList<Character> alphabets;
    private AlphabetNode root;

    public static final String ENG_ALPHABET =
            "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    public NewLanguageTrie(String alphabetOrder) {
        alphabets = new ArrayList<>();
        for (char c : alphabetOrder.toCharArray()) {
            if (alphabets.contains(c)) {
                throw  new IllegalArgumentException();
            }
            alphabets.add(c);
        }
        root = new AlphabetNode();
    }

    public NewLanguageTrie() {
        alphabets = new ArrayList<>();
        for (char c : ENG_ALPHABET.toCharArray()) {
            if (alphabets.contains(c)) {
                throw  new IllegalArgumentException();
            }
            alphabets.add(c);
        }
        root = new AlphabetNode();
    }


    public void insert(String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException();
        }
        for (char c : s.toCharArray()) {
            if (!alphabets.contains(c)) {
                return;
            }
        }
        root.insert(s, 0);
    }

    public boolean find(String s, boolean isFullWord) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException();
        }
        return root.find(s, isFullWord, 0);
    }

    public ArrayList<String> sortedWords() {
        return root.wordsInOrder("");
    }


    private class AlphabetNode {
        boolean exists;
        SortedMap<Integer, AlphabetNode> links;

        AlphabetNode() {
            links = new TreeMap<>();
        }

        /**
         * insert the character in s at charposition
         */
        public void insert(String s, int charPosition) {
            char comparedCharacter = s.charAt(charPosition);
            int comparedInt = alphabets.indexOf(comparedCharacter);
            if (!links.containsKey(comparedInt)) {  //make a new branch
                AlphabetNode nxtNode = new AlphabetNode();
                // put in the new pair of mapping
                links.put(comparedInt, nxtNode);
                // if reaching the last character then mark it exists
                if (charPosition == s.length() - 1) {
                    nxtNode.exists = true;
                } else {
                    nxtNode.insert(s, charPosition + 1);
                }
            } else {
                AlphabetNode followingNode = links.get(comparedInt);
                if (charPosition == s.length() - 1) {
                    followingNode.exists = true;
                } else {
                    followingNode.insert(s, charPosition + 1);
                }
            }
        }

        public AlphabetNode getPrefixNode(String prefix, int count) {
            AlphabetNode point = this;
            if (count == prefix.length()) {
                return point;
            }
            char compare = prefix.charAt(count);
            int charToInt = alphabets.indexOf(compare);
            if (!point.links.containsKey(charToInt)) {
                return null;
            } else {
                return point.links.get(charToInt).getPrefixNode(prefix, count + 1);
            }
        }



        /**
         * return if string s is found in the trie, depending on if it's also a fullword
         */
        public boolean find(String s, boolean isFullWord, int count) {
            char comparedCharacter = s.charAt(count);
            int comparedInt = alphabets.indexOf(comparedCharacter);
            if (links.containsKey(comparedInt)) {
                AlphabetNode nextNode = links.get(comparedInt);

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
        
        public ArrayList<String> wordsInOrder(String prefix) {
            ArrayList<String> words = new ArrayList<>();
            if (exists) {
                words.add(prefix);
            }
            if (links.size() != 0) {
                for (int i : links.keySet()) {
                    char nxt = alphabets.get(i);
                    AlphabetNode nxtNode = links.get(i);
                    String nxtPrefix = prefix + nxt;
                    words.addAll(nxtNode.wordsInOrder(nxtPrefix));
                }
            }
            return words;
        }
    }


    public static void main(String[] args) {
        NewLanguageTrie t = new NewLanguageTrie();
        t.insert("smog");
        t.insert("buck");

        t.insert("sad");

        t.insert("spite");
        t.insert("spy");

        System.out.println(t.sortedWords());

    }

}
