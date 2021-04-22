package wordtraitutil.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wordtraitutil.WORD_TRAIT;
import wordtraitutil.iface.ITraitPatternUtil;
import wordtraitutil.iface.IWordTraitClient;
import wordtraitutil.iface.IWordTraitParser;

import java.util.Arrays;

import static wordtraitutil.impl.CharTraitImplGroup.*;
import static wordtraitutil.WORD_TRAIT.*;

class WordTraitUtilTest {

    private IWordTraitParser getWordTraitUtil(){
        return WordTraitParser.builder().skipSymbols('\'').
                traits(
                        new CharTrait('\''),
                        new CharTrait('.'),
                        new CharTrait(','),
                        new CharTrait(':'),
                        new CharTrait('*'),
                        new CharTrait('['),
                        new CharTrait('_'),
                        new CharTraitAlpha('A'),
                        new CharTraitVisibleAscii('C'),
                        new CharTraitNumeric('N')
                ).build();
    }

    @Test
    void givenWordTraitUtil_returnAllTraitsAsString(){
        IWordTraitParser wordTrait = getWordTraitUtil();
        String actual, expected;
        actual = wordTrait.getWatchedTraits();
        expected = "'*,.:ACN[_";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenStringWithTraits_returnTraitString(){
        IWordTraitParser wordTrait = getWordTraitUtil();
        String text, actual, expected;

        text = "23";// num par
        expected = "CN";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "2,3,4"; // num list
        expected = ",CN";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "'a'"; // string
        expected = "'C";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "'Larry','Moe','Curly'"; // string list
        expected = "',C";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "abc_1"; // identifier
        expected = "ACN_";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "A,B_2,myVar"; // identifier list
        expected = ",ACN_";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "path1.path2.path_3";// identifier path
        expected = ".ACN_";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "2:3"; // numeric range
        expected = ":CN";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "*abc_1"; // id access
        expected = "*ACN_";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "*[2]"; // fx access
        expected = "*CN[";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);

        text = "*[8:11]"; // id access
        expected = "*:CN[";
        actual = wordTrait.setText(text).parse().getFoundTraits();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenOddballInput_returnCorrect(){
        IWordTraitParser wordTrait = getWordTraitUtil();
        String text, actual, expected;
        text = "''";// string par
        actual = wordTrait.setText(text).parse().getFoundTraits();
        expected = "'C";
        Assertions.assertEquals(expected, actual);

        text = "'',''";// string par
        actual = wordTrait.setText(text).parse().getFoundTraits();
        expected = "',C";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenFormattedString_sortInString(){
        ITraitPatternUtil traitPatternUtil = TraitPatternUtil.initInstance();
        String text, expected, actual;
        text = "vtdajcrwb";
        expected = "abcdjrtvw";
        actual = traitPatternUtil.sortText(text);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenPattern_sortByOddChars(){
        ITraitPatternUtil traitPatternUtil = TraitPatternUtil.initInstance();
        String text, expected, actual;
        text = ".a-c+b+e.d";
        expected = ".a+b-c.d+e";
        actual = traitPatternUtil.sortPattern(text);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenTextAndPattern_matchPattern(){
        ITraitPatternUtil traitPatternUtil = TraitPatternUtil.initInstance();
        String text, pattern;
        boolean actual;
        text = "abcd";
        pattern = "+a+b+c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertTrue(actual);

        text = "abd";
        pattern = "+a+b.c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertTrue(actual);

        text = "abd";
        pattern = "+a+b-c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertTrue(actual);

        text = "efg";
        pattern = ".a.b-c.d.e.f.g";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertTrue(actual);
    }

    @Test
    void givenTextAndPattern_noMatchPattern(){
        ITraitPatternUtil traitPatternUtil = TraitPatternUtil.initInstance();
        String text, pattern;
        boolean actual;
        text = "abcd";
        pattern = "+a+b-c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertFalse(actual);

        text = "abd";
        pattern = "+a+b+c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertFalse(actual);

        text = "abfd";
        pattern = "+a+b+c+d";
        pattern = traitPatternUtil.sortPattern(pattern);
        actual = traitPatternUtil.match(text, pattern);
        Assertions.assertFalse(actual);
    }

    @Test
    void givenText_matchEnum(){
        IWordTraitParser wordTrait = getWordTraitUtil();
        String text;
        WORD_TRAIT actual, expected;

        text = "23";// num par
        expected = NUM_PAR;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "2,3,4"; // num list
        expected = NUM_LIST;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "'a'"; // string
        expected = STR_PAR;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "'Larry','Moe','Curly'"; // string list
        expected = STR_LIST;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "abc_1"; // identifier
        expected = ID_PAR;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "MyId"; // identifier
        expected = ID_PAR;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "id1"; // identifier
        expected = ID_PAR;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "A,B_2,myVar"; // identifier list
        expected = ID_LIST;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "A,B,myVar"; // identifier list
        expected = ID_LIST;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "path1.path2.path_3";// identifier path
        expected = ID_SEP;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "2:3"; // numeric range
        expected = NUM_RANGE;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "*abc_1"; // id access
        expected = STAR_ID;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "*[2]"; // fx access
        expected = STAR_NUM;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);

        text = "*[8:11]"; // id access
        expected = STAR_RANGE;
        actual = WORD_TRAIT.fromTraitText(wordTrait.setText(text).parse().getFoundTraits());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void givenText_extractContent(){
        IWordTraitClient client = new TestClient();
        String text, expected, actual;

        text = "23";
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=null|numbers=[23]|wordTraitEnum=NUM_PAR";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "2,3,4"; // num list
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=null|numbers=[2, 3, 4]|wordTraitEnum=NUM_LIST";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "'a'"; // string
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[a]|numbers=null|wordTraitEnum=STR_PAR";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "'Larry','Moe','Curly'"; // string list
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[Larry, Moe, Curly]|numbers=null|wordTraitEnum=STR_LIST";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "abc_1"; // identifier
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[abc_1]|numbers=null|wordTraitEnum=ID_PAR";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "MyId"; // identifier
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[MyId]|numbers=null|wordTraitEnum=ID_PAR";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "id1"; // identifier
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[id1]|numbers=null|wordTraitEnum=ID_PAR";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "A,B_2,myVar"; // identifier list
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[A, B_2, myVar]|numbers=null|wordTraitEnum=ID_LIST";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "A,B,myVar"; // identifier list
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[A, B, myVar]|numbers=null|wordTraitEnum=ID_LIST";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "path1.path2.path_3";// identifier path
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[path1, path2, path_3]|numbers=null|wordTraitEnum=ID_SEP";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "2:3"; // numeric range
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=null|numbers=[2, 3]|wordTraitEnum=NUM_RANGE";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

        text = "*abc_1"; // id access
        WORD_TRAIT.tryParse(client, text);
        expected = "strings=[*abc_1]|numbers=null|wordTraitEnum=STAR_ID";
        actual = client.toString();
        Assertions.assertEquals(expected, actual);

//        text = "*[2]"; // fx access
//        WORD_TRAIT.tryParse(client, text);
//
//        text = "*[8:11]"; // id access
//        WORD_TRAIT.tryParse(client, text);
    }

    public static class TestClient implements IWordTraitClient {
        private String[] strings;
        private int[] numbers;
        private WORD_TRAIT wordTraitEnum;

        @Override
        public void receiveContent(String... content) {
            strings = content;
            numbers = null;
            //System.out.println("receive content: string");
            //System.out.println(Arrays.toString(content));
        }

        @Override
        public void receiveContent(int... content) {
            numbers = content;
            strings = null;
            //System.out.println("receive content: int");
            //System.out.println(Arrays.toString(content));
        }

        @Override
        public void receiveContent(WORD_TRAIT content) {
            wordTraitEnum = content;
            //System.out.println("receive content: enum");
            //System.out.println(content);
        }

        @Override
        public String toString() {
            return
                    "strings=" + Arrays.toString(strings) +
                    "|numbers=" + Arrays.toString(numbers) +
                    "|wordTraitEnum=" + wordTraitEnum;
        }
    }
}