/*
 * Copyright 2019 java-diff-utils.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.difflib.unifieddiff;

import com.github.difflib.patch.AbstractDelta;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Tobias Warneke (t.warneke@gmx.net)
 */
public class UnifiedDiffReaderTest {

    @Test
    public void testSimpleParse() throws IOException {
        UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(UnifiedDiffReaderTest.class.getResourceAsStream("jsqlparser_patch_1.diff"));

        System.out.println(diff);

        assertThat(diff.getFiles().size()).isEqualTo(2);

        UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertThat(file1.getFromFile()).isEqualTo("src/main/jjtree/net/sf/jsqlparser/parser/JSqlParserCC.jjt");
        assertThat(file1.getPatch().getDeltas().size()).isEqualTo(3);

        assertThat(diff.getTail()).isEqualTo("2.17.1.windows.2\n\n");
    }

    @Test
    public void testParseDiffBlock() {
        String[] files = UnifiedDiffReader.parseFileNames("diff --git a/src/test/java/net/sf/jsqlparser/statement/select/SelectTest.java b/src/test/java/net/sf/jsqlparser/statement/select/SelectTest.java");
        assertThat(files).containsExactly("src/test/java/net/sf/jsqlparser/statement/select/SelectTest.java", "src/test/java/net/sf/jsqlparser/statement/select/SelectTest.java");
    }

    @Test
    public void testChunkHeaderParsing() {
        Pattern pattern = UnifiedDiffReader.UNIFIED_DIFF_CHUNK_REGEXP;
        Matcher matcher = pattern.matcher("@@ -189,6 +189,7 @@ TOKEN: /* SQL Keywords. prefixed with K_ to avoid name clashes */");

        assertTrue(matcher.find());
        assertEquals("189", matcher.group(1));
        assertEquals("189", matcher.group(3));
    }

    @Test
    public void testChunkHeaderParsing2() {
        //"^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@.*$"
        Pattern pattern = UnifiedDiffReader.UNIFIED_DIFF_CHUNK_REGEXP;
        Matcher matcher = pattern.matcher("@@ -189,6 +189,7 @@");

        assertTrue(matcher.find());
        assertEquals("189", matcher.group(1));
        assertEquals("189", matcher.group(3));
    }

    @Test
    public void testChunkHeaderParsing3() {
        //"^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@.*$"
        Pattern pattern = UnifiedDiffReader.UNIFIED_DIFF_CHUNK_REGEXP;
        Matcher matcher = pattern.matcher("@@ -1,27 +1,27 @@");

        assertTrue(matcher.find());
        assertEquals("1", matcher.group(1));
        assertEquals("1", matcher.group(3));
    }

    @Test
    public void testSimpleParse2() throws IOException {
        UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(UnifiedDiffReaderTest.class.getResourceAsStream("jsqlparser_patch_1.diff"));

        System.out.println(diff);

        assertThat(diff.getFiles().size()).isEqualTo(2);

        UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertThat(file1.getFromFile()).isEqualTo("src/main/jjtree/net/sf/jsqlparser/parser/JSqlParserCC.jjt");
        assertThat(file1.getPatch().getDeltas().size()).isEqualTo(3);

        AbstractDelta<String> first = file1.getPatch().getDeltas().get(0);

        assertThat(first.getSource().size()).isGreaterThan(0);
        assertThat(first.getTarget().size()).isGreaterThan(0);

        assertThat(diff.getTail()).isEqualTo("2.17.1.windows.2\n\n");
    }

    @Test
    public void testSimplePattern() {
        Pattern pattern = Pattern.compile("^\\+\\+\\+\\s");

        Matcher m = pattern.matcher("+++ revised.txt");
        assertTrue(m.find());
    }

    @Test
    public void testParseIssue46() throws IOException {
        UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(
                UnifiedDiffReaderTest.class.getResourceAsStream("problem_diff_issue46.diff"));

        System.out.println(diff);

        assertThat(diff.getFiles().size()).isEqualTo(1);

        UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertThat(file1.getFromFile()).isEqualTo(".vhd");
        assertThat(file1.getPatch().getDeltas().size()).isEqualTo(1);

        assertThat(diff.getTail()).isNull();
    }

    @Test
    public void testParseIssue33() throws IOException {
        UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(
                UnifiedDiffReaderTest.class.getResourceAsStream("problem_diff_issue33.diff"));

        assertThat(diff.getFiles().size()).isEqualTo(1);

        UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertThat(file1.getFromFile()).isEqualTo("Main.java");
        assertThat(file1.getPatch().getDeltas().size()).isEqualTo(1);

        assertThat(diff.getTail()).isNull();
        assertThat(diff.getHeader()).isNull();
    }
    
    @Test
    public void testParseIssue51() throws IOException {
        UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(
                UnifiedDiffReaderTest.class.getResourceAsStream("problem_diff_issue51.diff"));

        System.out.println(diff);

        assertThat(diff.getFiles().size()).isEqualTo(2);

        UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertThat(file1.getFromFile()).isEqualTo("f1");
        assertThat(file1.getPatch().getDeltas().size()).isEqualTo(1);

        assertThat(diff.getTail()).isNull();
    }
}
