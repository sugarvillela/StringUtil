package tokenizer.impl;

import tokenizer.iface.ITokenizer;
import tokenizer.iface.IWhitespace;

import java.util.ArrayList;
import java.util.Stack;

/**A more complex string tokenizer.
 * Supports multiple delimiters
 * Ignores adjacent delimiters to prevent empty elements
 * Supports 'skip area' (quoted or bracketed text). Tokenizer leaves these areas joined.
 * Supports multiple, nested skip symbols. Outermost symbol defines skip area.
 * Option to keep or discard delimiters, skip symbols.
 * Use builder to set options.
 *
 * Sample usage:
 *   String text = "Sentence__with_(too_many_'delims')_and_quotes__";
 *   String[] tok = Tokenizer.builder().delimiters(" _").skipSymbols("('").build().parse(text).toArray();
 * Output:
 * 	  Sentence
 * 	  with
 * 	  too_many_'delims'
 * 	  and
 * 	  quotes
 */
public class Tokenizer implements ITokenizer {
    private static ITokenizer instance;

    public static ITokenizer initInstance(){
        return (instance == null)?
            (instance = Tokenizer.builder().delimiters(' ').skipSymbols("'").build())
                : instance;
    }

    private static final char escape = '\\';
    private IWhitespace whitespace;
    private String delimiters;              // input text, list of delimiters text,
    private char[] oMap, cMap;              // matched open/close skip char arrays
    private ArrayList<String> tokens;       // output
    private Stack<Character> cSymbols;      // Closing symbol during skip
    private boolean tokenizeDelimiter;      // save delimiter to own element
    private boolean delimiterOnce;          // save delimiter to own element, ignore duplicates
    private boolean keepSkipSymbol;         // obey skip symbols and leave in (default out)
    private boolean keepEscapeSymbol;       // obey escape symbol and leave in for later processing
    private String text;
    private int[] indents;

    private Tokenizer(){
        tokenizeDelimiter = false;
        keepSkipSymbol = false;
        keepEscapeSymbol = false;
    }

    /*====Private parts===============================================================================================*/

    private void setMap(String skips){
        // map openers to closers, using symbols from arg
        // if you want different symbols, pass arrays with Builder
        oMap =  new char[skips.length()];
        cMap =  new char[skips.length()];
        char[] openers = new char[]{'(','{','[','<','"','\''};
        char[] closers = new char[]{')','}',']','>','"','\''};
        int to = 0;
        for (int i = 0; i < openers.length; i++) {
            if(skips.indexOf(openers[i])!=-1){
                oMap[to]=openers[i];
                cMap[to]=closers[i];
                to++;
            }
        }
    }
    private boolean isEscape(char symbol){
        return symbol == escape;
    }
    private boolean isDelimiter(char symb){
        return delimiters.indexOf(symb) != -1 || whitespace.isWhitespace(symb);
    }

    private boolean haveText(int i, int j){
        return i != j;
    }

    private boolean enterSkipArea(char symbol){
        for(int i=0; i<oMap.length; i++){
            if(symbol == oMap[i]){
                this.cSymbols.push(cMap[i]);// important side effect
                return true;
            }
        }
        return false;
    }

    private boolean inSkipArea(){
        return !cSymbols.isEmpty();
    }

    private boolean leaveSkipArea(char symbol){
        if(cSymbols.peek().equals(symbol)){
            cSymbols.pop();
            return true;
        }
        return false;
    }

    private boolean noMoreSkips(){
        return cSymbols.empty();
    }

    /*====Public parts================================================================================================*/

    @Override
    public ITokenizer setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public ITokenizer setDelimiter(char... delimiter) {
        this.delimiters = new String(delimiter);
        if(this.delimiters.contains(" ")){
            whitespace = new IWhitespace() {
                @Override
                public boolean isWhitespace(char symbol) {
                    return ((int)symbol) < 33;
                }
            };
        }
        else{
            whitespace = new IWhitespace() {
                @Override
                public boolean isWhitespace(char symbol) {
                    return false;
                }
            };
        }
        return this;
    }

    @Override
    public ITokenizer parse() {
        cSymbols = new Stack<>();
        this.tokens = new ArrayList<>();
        this.indents = null;

        int i = 0, j = 0, len = text.length();
        boolean escaped = false;
        for (i = 0; i < len; i++) {
            char curr = text.charAt(i);

            if(isEscape(text.charAt(i))){
                escaped = true;
                if(!keepEscapeSymbol){
                    text = text.substring(0, i) + text.substring(i + 1);
                    len--;
                    i--;
                }
            }
            else if(escaped){
                escaped = false;
            }
            else{
                if(inSkipArea()){
                    if(leaveSkipArea(curr)){
                        if(noMoreSkips() && haveText(i, j) && !keepSkipSymbol){
                            tokens.add(text.substring(j, i));
                            j = i + 1;
                        }

                    }
                    else if(enterSkipArea(curr)){}
                }
                else if(enterSkipArea(curr)){

                    if(!keepSkipSymbol){
                        if(haveText(i, j)){
                            tokens.add(text.substring(j, i));
                            j = i;
                        }
                        j += 1;
                    }
                }
                else if(isDelimiter(curr)){
                    if(haveText(i, j)){
                        tokens.add(text.substring(j, i));
                    }
                    if(tokenizeDelimiter){
                        if(!delimiterOnce || i != j){
                            tokens.add(text.substring(i, i + 1));
                        }
                    }
                    j = i + 1;
                }
            }
        }

        if(haveText(i, j)){
            tokens.add(text.substring(j, i));
        }
        return this;
    }

    private void calculateIndents(){
        indents = new int[tokens.size() + 1];
        cSymbols = new Stack<>();
        boolean last = false;

        int k = (isDelimiter(text.charAt(0)))? 0 : 1;
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);

            if(inSkipArea()){
                if(leaveSkipArea(curr) || enterSkipArea(curr)){}
            }
            else if(enterSkipArea(curr)){
                if (last){
                    k++;
                    last = false;
                }
            }
            else if(isDelimiter(curr)){
                indents[k]++;
                last = true;
            }
            else if (last){
                k++;
                last = false;
            }
        }
    }

    @Override
    public ArrayList<String> toList() {
        return this.tokens;
    }

    @Override
    public String[] toArray() {
        return this.tokens.toArray(new String[0]);
    }

    @Override
    public int[] indents() {
        if(tokens == null){
            throw new IllegalStateException("Must parse text before calculating indents");
        }
        if(indents == null){
            this.calculateIndents();
        }
        return indents;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder implements ITokenizer.Builder {
        Tokenizer built;

        private Builder(){
            built = new Tokenizer();
        }

        @Override
        public ITokenizer.Builder delimiters(char... delimiter) {
            built.setDelimiter(delimiter);
            return this;
        }

        @Override
        public ITokenizer.Builder skipSymbols(String openingSymbols) {
            built.setMap(openingSymbols);
            return this;
        }

        @Override
        public ITokenizer.Builder skipSymbols(char oneOpeningSymbol) {
            built.setMap(String.valueOf(oneOpeningSymbol));
            return this;
        }

        @Override
        public ITokenizer.Builder skipSymbols(char openingSymbol, char closingSymbol) {
            built.oMap = new char[]{openingSymbol};
            built.cMap = new char[]{closingSymbol};
            return this;
        }

        @Override
        public ITokenizer.Builder skipSymbols(char[] oMap, char[] cMap) {
            built.oMap = oMap;
            built.cMap = cMap;
            return this;
        }

        @Override
        public ITokenizer.Builder keepSkipSymbol() {
            built.keepSkipSymbol = true;
            return this;
        }

        @Override
        public ITokenizer.Builder keepEscapeSymbol() {
            built.keepEscapeSymbol = true;
            return this;
        }

        @Override
        public ITokenizer.Builder tokenizeDelimiter() {
            built.tokenizeDelimiter = true;
            return this;
        }

        @Override
        public ITokenizer.Builder tokenizeDelimiterOnce() {
            built.tokenizeDelimiter = true;
            built.delimiterOnce = true;
            return this;
        }


        @Override
        public ITokenizer build() {
            if(built.oMap == null){
                built.oMap = new char[0];
            }
            if(built.delimiters == null){
                built.setDelimiter(' ');
            }
            return built;
        }
    }
}
