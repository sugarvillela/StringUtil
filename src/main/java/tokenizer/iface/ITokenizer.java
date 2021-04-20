package tokenizer.iface;

import java.util.ArrayList;

public interface ITokenizer {
    ITokenizer setText(String text);
    ITokenizer setDelimiter(char... delimiter);
    ITokenizer parse();
    ArrayList<String> toList();
    String[] toArray();
    int[] indents();

    /** Builder interface used to build the more complex implementation of Tokenizer */
    interface Builder{
        /**Supports multiple delimiters
         * @param delimiter All delimiters, for example: ' ', '-'
         */
        Builder delimiters(char... delimiter);

        /**Areas enclosed in symbols are skipped by the tokenizer
         * Supports '(','{','[','<', single- and double-quote
         * Automatically adds the appropriate closing symbols
         * @param openingSymbols All opening symbols, for example "({'"
         */
        Builder skipSymbols(String openingSymbols);

        /**Areas enclosed in symbols are skipped by the tokenizer
         * Supports '(','{','[','<', single- and double-quote
         * Automatically adds the appropriate closing symbols
         * @param openingSymbol A single supported opening symbol, like '{'
         */
        Builder skipSymbols(char openingSymbol);

        /**To use single unsupported open and close symbols, pass the symbols here
         * @param openingSymbol a single opening symbol
         * @param closingSymbol a single closing symbol
         */
        Builder skipSymbols(char openingSymbol, char closingSymbol);

        /**To use multiple unsupported symbols, pass your own char arrays
         * @param oMap opening symbols
         * @param cMap closing symbols, must match oMap index and size
         */
        Builder skipSymbols(char[] oMap, char[]cMap);

        /**Tokenizer removes outermost skip symbols by default
         * Setting keepSkipSymbol leaves the symbols in */
        Builder keepSkipSymbol();

        /**Tokenizer obeys and removes escape symbols by default
         * Setting keepEscapeSymbol leaves the symbols in
         * (for cases where further processing needs to obey them too) */
        Builder keepEscapeSymbol();

        /**Tokenizer discards delimiters by default
         * Setting delimiterToElement causes delimiter to be written to
         * its own element (repeated delimiters are not ignored) */
        Builder tokenizeDelimiter();

        /**Same as delimiterToElement() except repeated delimiters are ignored */
        Builder tokenizeDelimiterOnce();

        ITokenizer build();
    }
}
