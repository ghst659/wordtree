package net.tc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class PrefixTreeTest {
    @Test
    public void testEmpty() {
        PrefixTree t = new PrefixTree();
        Assertions.assertFalse(t.hasWord("foo"));
    }
    @Test
    public void testTwoWords() {
        PrefixTree t = new PrefixTree();
        t.addWord("foo");
        t.addWord("bar");
        Assertions.assertTrue(t.hasWord("foo"));
        Assertions.assertTrue(t.hasWord("bar"));
        Assertions.assertFalse(t.hasWord("foobar"));
        Assertions.assertFalse(t.hasWord("fo"));
        Assertions.assertFalse(t.hasWord("ar"));
    }
    @Test
    public void testMidWord() {
        PrefixTree t = new PrefixTree();
        t.addWord("foobar");
        Assertions.assertTrue(t.hasWord("foobar"));
        Assertions.assertFalse(t.hasWord("foo"));
        t.addWord("foo");
        Assertions.assertTrue(t.hasWord("foo"));
    }
    @Test
    public void testPrefixes() {
        PrefixTree t = new PrefixTree();
        t.addWord("foobar");
        t.addWord("foobie");
        t.addWord("bar");
        Set<String> got = t.getWords("foo");
        Assertions.assertEquals(2, got.size());
        Assertions.assertTrue(got.contains("foobar"));
        Assertions.assertTrue(got.contains("foobie"));
        Set<String> empty = t.getWords("fx");
        Assertions.assertTrue(empty.isEmpty());
    }
    @Test
    public void testReadDict() {
        final String path = "/usr/share/dict/british-english";
        PrefixTree t = new PrefixTree();
        Runtime rt = Runtime.getRuntime();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            rt.gc();
            long memBefore = rt.totalMemory();
            for (String buf = br.readLine(); buf != null; buf = br.readLine()) {
                t.addWord(buf.trim());
            }
            long memAfter = rt.totalMemory();
            long delta = memAfter - memBefore;
            System.out.format("%s: memory: %d\n", path, delta);
        } catch (IOException e) {
            System.err.format("error: %s\n", e.toString());
        }
    }

    private static Stream<Arguments> breakableTest() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of("cat", true),
                Arguments.of("a", true),
                Arguments.of("acat", true),
                Arguments.of("isa", true),
                Arguments.of("isacat", true),
                Arguments.of("isacatacar", true),
                Arguments.of("issa", false),
                Arguments.of("cam", false),
                Arguments.of("o", false)
        );
    }
    @ParameterizedTest
    @MethodSource("breakableTest")
    public void testCanBeBroken(String text, boolean want) {
        PrefixTree t = new PrefixTree();
        t.addWord("a");
        t.addWord("cat");
        t.addWord("is");
        t.addWord("car");
        Assertions.assertEquals(want, t.canBreakIntoWords(text));
    }

    // @Test
    public void testMakeWordList() {
        String text = "0123456789abcdef";
        PrefixTree.BreakIndex a = PrefixTree.BreakIndex.newNode(null, 0);
        PrefixTree.BreakIndex b = PrefixTree.BreakIndex.newNode(a, 1);
        PrefixTree.BreakIndex c = PrefixTree.BreakIndex.newNode(a, 2);
        PrefixTree.BreakIndex d = PrefixTree.BreakIndex.newNode(b, 4);
        PrefixTree.BreakIndex e = PrefixTree.BreakIndex.newNode(b, 16);
        PrefixTree.BreakIndex f = PrefixTree.BreakIndex.newNode(c, 16);
        PrefixTree.BreakIndex g = PrefixTree.BreakIndex.newNode(c, 6);
        PrefixTree.BreakIndex h = PrefixTree.BreakIndex.newNode(g, 10);
        PrefixTree.BreakIndex i = PrefixTree.BreakIndex.newNode(h, 16);
        List<List<String>> got = PrefixTree.makeWordLists(text, a);
        for (List<String> s : got) {
            for (String w : s) {
                System.out.printf("'%s' ", w);
            }
            System.out.printf("\n");
        }
    }

    @Test
    public void testWordBreak() {
        PrefixTree t = new PrefixTree();
        t.addWord("the");
        t.addWord("a");
        t.addWord("cat");
        t.addWord("car");
        t.addWord("i");
        t.addWord("is");
        t.addWord("mat");
        t.addWord("mara");
        t.addWord("mar");
        t.addWord("chat");
        t.addWord("at");
        t.addWord("catch");
        t.addWord("in");
        t.addWord("visible");
        t.addWord("invisible");
        t.addWord("el");
        t.addWord("chattel");
        String text = "thecatchatinvisible";
        List<List<String>> got = t.wordBreak(text);
        for (List<String> s : got) {
            for (String w : s) {
                System.out.printf("'%s' ", w);
            }
            System.out.printf("\n");
        }
    }
}
