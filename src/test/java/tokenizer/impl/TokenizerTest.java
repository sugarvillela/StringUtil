package tokenizer.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tokenizer.iface.ITokenizer;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {
    @Test
    void simpleTok_givenTooManyDelimiters_shouldTokenizeWithoutExtraElements() {
        String text = "Sentence__with_(too_many_'delims')__and_quotes__";
        String[] tok = new SimpleTok('_').setText(text).parse().toArray();
        String unTok = String.join("|", tok);
        assertEquals("Sentence|with|(too|many|'delims')|and|quotes", unTok);
    }
    @Test
    void simpleTok_givenEscapeChar_shouldIgnoreDelimiter() {
        String text = "Sentence__\\_with_(too_many_'delims')__and_quotes__";
        String[] tok = new SimpleTok('_').setText(text).parse().toArray();
        String unTok = String.join("|", tok);
        assertEquals("Sentence|_with|(too|many|'delims')|and|quotes", unTok);
    }
    @Test
    void givenSkipArea_shouldNotTokenizeInSkipArea() {
        String text = "Sentence__with_(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|too_many_'delims'|and|quotes", unTok);
    }
    @Test
    void givenNestedSkipAreas_shouldHandleOuterSymbols() {
        String text = "Sentence__with_(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").keepSkipSymbol().build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|(too_many_'delims')|and|quotes", unTok);
    }
    @Test
    void givenSequentialSkipAreas_shouldHandleBothSetsOfSymbols() {
        String text = "Sentence__with_(many_delims)__and_'stuff_in_quotes'";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|many_delims|and|stuff_in_quotes", unTok);
    }
    @Test
    void givenTokenizeDelimiter_shouldKeepAllDelimiter() {
        String text = "Sentence__with_(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").tokenizeDelimiter().build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|_|_|with|_|too_many_'delims'|_|_|and|_|quotes|_|_", unTok);
    }
    @Test
    void givenTokenizeDelimiterOnce_shouldKeepDelimiterOnce() {
        String text = "Sentence__with_(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").tokenizeDelimiterOnce().build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|_|with|_|too_many_'delims'|and|_|quotes|_", unTok);
    }
    @Test
    void givenKeepSkipsAndConnectedChar_leavesCharConnected() {
        String text = "Sentence__with_a(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").keepSkipSymbol().build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|a(too_many_'delims')|and|quotes", unTok);
    }
    @Test
    void givenKeepSkipsAndConnectedCharAfter_leavesCharConnected() {
        String text = "Sentence__with_(too_many_'delims')a__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").keepSkipSymbol().build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|(too_many_'delims')a|and|quotes", unTok);
    }
    @Test
    void givenNoKeepSkipsAndConnectedChar_splitsOnChar() {
        String text = "Sentence__with_a(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|a|too_many_'delims'|and|quotes", unTok);
    }
    @Test
    void givenNoKeepSkipsAndConnectedCharAfter_splitsOnChar() {
        String text = "Sentence__with_(too_many_'delims')a__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters(' ', '_').skipSymbols("('").build().setText(text).parse().toArray();

        String unTok = String.join("|", tok);
        System.out.println(unTok);
        assertEquals("Sentence|with|too_many_'delims'|a|and|quotes", unTok);
    }
    @Test
    void tokenizer_givenEscapeChar_shouldIgnoreDelimiter() {
        String text = "Sentence__\\_with_(too_many_'delims')__and_quotes__";
        String[] tok = Tokenizer.builder().delimiters('_').build().setText(text).parse().toArray();
        String unTok = String.join("|", tok);
        assertEquals("Sentence|_with|(too|many|'delims')|and|quotes", unTok);
    }
    @Test
    void tokenizer_givenEscapeChar_shouldIgnoreSkip() {
        String text = "I skip 'tokenizing \\'in here\\' but not' out here";
        String[] tok = Tokenizer.builder().delimiters(' ').skipSymbols("'").keepSkipSymbol().
                build().setText(text).parse().toArray();
        String unTok = String.join("|", tok);
        assertEquals("I|skip|'tokenizing 'in here' but not'|out|here", unTok);
    }

    @Test
    void tokenizer_givenEscapeChar_shouldIgnoreSkipAndKeepEscape() {
        String text = "I skip 'tokenizing \\'in here\\' but not' out here";
        String[] tok = Tokenizer.builder().delimiters(' ').skipSymbols("'").keepSkipSymbol().
                keepEscapeSymbol().build().setText(text).parse().toArray();
        String unTok = String.join("|", tok);
        assertEquals("I|skip|'tokenizing \\'in here\\' but not'|out|here", unTok);
    }
    /*=====GTree ParseTree uses=======================================================================================*/

    @Test
    void givenSublang_splitOnAssignedDelim() {
        char AND = '&', OR = '|';
        String text = "zero|one&two|three";
        ITokenizer tokenizer = Tokenizer.builder().skipSymbols("('").keepSkipSymbol().build();

        tokenizer.setDelimiter(AND);
        String[] splitAnd = tokenizer.setText(text).parse().toArray();

        String unSplitAnd = String.join("-", splitAnd);
        System.out.println(unSplitAnd);
        assertEquals("zero|one-two|three", unSplitAnd);

        tokenizer.setDelimiter(OR);
        String[] splitOr = tokenizer.setText(splitAnd[0]).parse().toArray();
        String unSplitOr = String.join("-", splitOr);
        System.out.println(unSplitOr);
        assertEquals("zero-one", unSplitOr);

        splitOr = tokenizer.setText(splitAnd[1]).parse().toArray();
        unSplitOr = String.join("-", splitOr);
        System.out.println(unSplitOr);
        assertEquals("two-three", unSplitOr);
    }

    @Test
    void givenSublangKeepSkipsAndConnectedChar_leaveCharConnected() {
        char AND = '&', OR = '|';
        String text = "zero|!(one&two)|three";
        ITokenizer tokenizer = Tokenizer.builder().skipSymbols("('").keepSkipSymbol().build();

        tokenizer.setDelimiter(OR);
        String[] splitOr = tokenizer.setText(text).parse().toArray();

        String unSplitOr = String.join("-", splitOr);
        System.out.println(unSplitOr);
        assertEquals("zero-!(one&two)-three", unSplitOr);

        splitOr[1] = splitOr[1].substring(2, splitOr[1].length() - 1);
        assertEquals("one&two", splitOr[1]);

        tokenizer.setDelimiter(AND);
        String[] splitAnd = tokenizer.setText(splitOr[1]).parse().toArray();
        String unSplitAnd = String.join("-", splitAnd);
        System.out.println(unSplitAnd);
        assertEquals("one-two", unSplitAnd);
    }

    /*=====SubLang RxPattern uses=====================================================================================*/

    @Test
    void givenValidSubLangPattern_tokenize(){
        char[] allChars = {'=', '>', '<'};
        String text = "firstHalf>secondHalf '>' thirdHalf";
        String actual = String.join("|", Tokenizer.builder().delimiters(allChars).
                skipSymbols('\'').tokenizeDelimiter().keepSkipSymbol().build().setText(text).parse().toList());
        String expected = "firstHalf|>|secondHalf '>' thirdHalf";
        Assertions.assertEquals(expected, actual);
    }
}