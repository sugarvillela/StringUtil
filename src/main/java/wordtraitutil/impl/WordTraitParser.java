package wordtraitutil.impl;

import wordtraitutil.iface.ICharTrait;
import wordtraitutil.iface.IWordTraitParser;

import java.util.Arrays;
import java.util.Stack;

/** Char search utility that obeys skip areas, escapes. Algorithm similar to Tokenizer class */
public class WordTraitParser implements IWordTraitParser {
    private static final char escape = '\\';
    private ICharTrait[] traits;           // List of traits to look for
    private char[] oMap, cMap;              // matched open/close skip char arrays
    private Stack<Character> cSymbols;      // Closing symbol during skip
    private boolean keepEscapeSymbol;       // obey escape symbol and leave in for later processing
    private boolean foundTrait;             // state, reset on every parse
    private String text;

    private WordTraitParser(){
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

    private void checkTrait(char curr){
        for(ICharTrait trait : traits){
            if(trait.checkTrait(curr)){
                foundTrait = true;
            }
        }
    }

    private void clear(){
        for(ICharTrait traitWatch : traits){
            traitWatch.clear();
        }
        cSymbols = new Stack<>();
        foundTrait = false;
    }

    /*====Public parts================================================================================================*/

    @Override
    public IWordTraitParser setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public IWordTraitParser parse() {
        this.clear();

        int len = text.length();
        boolean escaped = false;

        for (int i = 0; i < len; i++) {
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
                        if(noMoreSkips()){// outer skip symbol can also be a watched trait
                            checkTrait(curr);
                        }
                    }
                    else if(enterSkipArea(curr)){}// ignore inner skip symbols
                }
                else if(enterSkipArea(curr)){
                    checkTrait(curr); // outer skip symbol can also be a watched trait
                }
                else {
                    checkTrait(curr);
                }
            }
        }
        return this;
    }

    @Override
    public boolean foundTrait() {
        return foundTrait;
    }

    @Override
    public String getWatchedTraits(){// for dev, to get all traits sorted
        char[] chars = new char[traits.length];
        for(int i = 0; i < traits.length; i++){
            chars[i] = traits[i].watchedTrait();
        }
        return new String(chars);
    }

    @Override
    public String getFoundTraits(){
        int count = 0, k = 0;
        //System.out.println("getFoundTraits...");
        for(ICharTrait charTrait : traits){
            //System.out.println(charTrait.watchedTrait());
            if(charTrait.foundTrait()){
                //System.out.println("    found: " + charTrait.watchedTrait());
                count++;
            }
        }
        char[] chars = new char[count];
        for(ICharTrait charTrait : traits){
            if(charTrait.foundTrait()){
                chars[k++] = charTrait.watchedTrait();
            }
        }
        return new String(chars);
    }

    public static IBuilder builder(){
        return new Builder();
    }

    public static class Builder implements IBuilder{
        private final WordTraitParser built;

        public Builder() {
            built = new WordTraitParser();
        }

        @Override
        public IBuilder traits(ICharTrait... traits) {
            Arrays.sort(traits);
            built.traits = traits;
            return this;
        }

        @Override
        public IBuilder skipSymbols(String openingSymbols) {
            built.setMap(openingSymbols);
            return this;
        }

        @Override
        public IBuilder skipSymbols(char oneOpeningSymbol) {
            built.setMap(String.valueOf(oneOpeningSymbol));
            return this;
        }

        @Override
        public IBuilder skipSymbols(char openingSymbol, char closingSymbol) {
            built.oMap = new char[]{openingSymbol};
            built.cMap = new char[]{closingSymbol};
            return this;
        }

        @Override
        public IBuilder skipSymbols(char[] oMap, char[] cMap) {
            built.oMap = oMap;
            built.cMap = cMap;
            return this;
        }

        @Override
        public IBuilder keepEscapeSymbol() {
            built.keepEscapeSymbol = true;
            return this;
        }

        @Override
        public IWordTraitParser build(){
            return built;
        }
    }
}
