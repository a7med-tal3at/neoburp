package burp.n0ptex.neoburp.AutoCompletion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCompletion {

    public class TrieNode {

        Map<Character, TrieNode> children;
        char c;
        boolean isWord;

        public TrieNode(char c) {
            this.c = c;
            children = new HashMap<>();
        }

        public TrieNode() {
            children = new HashMap<>();
        }

        public void insert(String word) {
            if (word == null || word.isEmpty()) {
                return;
            }

            char firstChar = word.charAt(0);
            TrieNode child = children.get(firstChar);
            if (child == null) {
                child = new TrieNode(firstChar);
                children.put(firstChar, child);
            }

            if (word.length() > 1) {
                child.insert(word.substring(1));
            } else {
                child.isWord = true;
            }
        }
    }

    TrieNode root;

    public void updateAutoCompletionWords(List<String> words) {
        root = new TrieNode();
        for (String word : words) {
            root.insert(word);
        }
    }

    public boolean find(String prefix, boolean exact) {
        prefix = prefix.toLowerCase();
        TrieNode lastNode = root;
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(Character.toLowerCase(c));
            if (lastNode == null) {
                return false;
            }
        }
        return !exact || lastNode.isWord;
    }

    public boolean find(String prefix) {
        return find(prefix, false);
    }

    public void suggestHelper(TrieNode root, List<String> list, StringBuffer curr, String pattern, int patternIndex) {
        if (root.isWord && patternIndex == pattern.length()) {
            list.add(curr.toString());
        }

        if (root.children == null || root.children.isEmpty()) {
            return;
        }

        for (TrieNode child : root.children.values()) {
            if (patternIndex < pattern.length()
                    && Character.toLowerCase(child.c) == Character.toLowerCase(pattern.charAt(patternIndex))) {
                suggestHelper(child, list, curr.append(child.c), pattern, patternIndex + 1);
            } else {
                suggestHelper(child, list, curr.append(child.c), pattern, patternIndex);
            }
            curr.setLength(curr.length() - 1);
        }
    }

    public List<String> suggest(String pattern) {
        pattern = pattern.toLowerCase();
        List<String> list = new ArrayList<>();
        StringBuffer curr = new StringBuffer();
        suggestHelper(root, list, curr, pattern, 0);
        return list;
    }
}
