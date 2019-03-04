package net.tc;

import java.util.*;

public class PrefixTree {
    public static class BreakIndex {
        public int startPos = 0; // text index of a word start.
        public List<BreakIndex> tail = new LinkedList<>();
        public static BreakIndex newNode(BreakIndex parent, int pos) {
            BreakIndex n = new BreakIndex();
            n.startPos = pos;
            if (parent != null) {
                parent.tail.add(n);
            }
            return n;
        }
    }

    public static List<List<String>> makeWordLists(String text, PrefixTree.BreakIndex breaks) {
        List<List<String>> result = new LinkedList<>();
        LinkedList<Integer> indices = new LinkedList<>();
        wordList(text, breaks, indices, result);
        return result;
    }
    private static void wordList(String text, BreakIndex breaks, LinkedList<Integer> indices, List<List<String>> result) {
        indices.add(breaks.startPos);
        if (breaks.startPos >= text.length()) {
            List<String> wordList = null;
            int currentStart = 0;
            for (int wordIndex: indices) {
                if (wordList == null) {
                    wordList = new LinkedList<>();
                } else {
                    wordList.add(text.substring(currentStart, wordIndex));
                }
                currentStart = wordIndex;
            }
            result.add(wordList);
        } else {
            for (BreakIndex nextBreak : breaks.tail) {
                wordList(text, nextBreak, indices, result);
            }
        }
        indices.removeLast();
    }

    public List<List<String>> wordBreak(String text) {
        BreakIndex indexRoot = BreakIndex.newNode(null, 0);
        breakWords(this, text, 0, indexRoot);
        List<List<String>> result = makeWordLists(text, indexRoot);
        return result;
    }
    private static void breakWords(PrefixTree root, String text, int startPos, BreakIndex breakIndex) {
        if (text.length() == startPos) {
            return;
        }
        PrefixTree cur = root;
        for (int i = startPos; i < text.length(); i++) {
            Character c = text.charAt(i);
            PrefixTree next = cur.child(c);
            if (next == null) {
                return;
            }
            if (next.isEnd()) {
                breakWords(root, text, i+1, BreakIndex.newNode(breakIndex, i+1));
            }
            cur = next;
        }
    }

    public boolean canBreakIntoWords(CharSequence text) {
        return breakable(this, text, 0);
    }
    private static boolean breakable(PrefixTree root, CharSequence text, int startPos) {
        if (text.length() == startPos) {
            return true;
        }
        PrefixTree cur = root;
        for (int i = startPos; i < text.length(); i++) {
            Character c = text.charAt(i);
            PrefixTree next = cur.child(c);
            if (next == null) {
                return false;
            }
            if (next.isEnd() && breakable(root, text, i+1)) {
                return true;
            }
            cur = next;
        }
        return false;
    }

    public Set<String> getWords(CharSequence prefix) {
        Set<String> result = new TreeSet<>();
        PrefixTree suffixRoot = this;
        int len = prefix.length();
        for (int depth = 0; depth < len; depth++) {
            char k = prefix.charAt(depth);
            if (! suffixRoot.children.containsKey(k)) {
                return result;
            }
            suffixRoot = suffixRoot.children.get(k);
        }
        suffixRoot.getWordsHelper(prefix.toString(), result);
        return result;
    }
    private void getWordsHelper(String accum, Set<String> result) {
        if (wordEnd) {
            result.add(accum);
        }
        for (Map.Entry<Character, PrefixTree> kv : children.entrySet()) {
            String nextAccum = accum + kv.getKey();
            PrefixTree subTrie = kv.getValue();
            subTrie.getWordsHelper(nextAccum, result);
        }
    }
    public void addWord(CharSequence s) {
        int len = s.length();
        if (len == 0) {
            wordEnd = true;
            return;
        }
        char k = s.charAt(0);
        CharSequence rest = s.subSequence(1, len);
        PrefixTree child = children.get(k);
        if (child == null) {
            child = new PrefixTree();
            children.put(k, child);
        }
        child.addWord(rest);
    }
    public boolean hasWord(CharSequence s) {
        int len = s.length();
        if (len == 0) {
            return wordEnd;
        }
        char k = s.charAt(0);
        PrefixTree child = children.get(k);
        if (child == null) {
            return false;
        }
        CharSequence rest = s.subSequence(1, len);
        return child.hasWord(rest);
    }
    public boolean isEnd() {
        return wordEnd;
    }
    public PrefixTree child(Character c) {
        return children.get(c);
    }
    private TreeMap<Character, PrefixTree> children = new TreeMap<>();
    private boolean wordEnd = false;
}
