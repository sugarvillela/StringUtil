package tokenizer.impl;

import tokenizer.iface.ITokenizer;
import tokenizer.iface.IWhitespace;

import java.util.ArrayList;

/**A simple string tokenizer.
 * Supports single delimiter
 * Ignores adjacent delimiters to prevent empty elements
 * Option to limit number of splits */
public class SimpleTok implements ITokenizer {
    private static final char escape = '\\';
    private IWhitespace whitespace;
    private final int limit;
    private String text;
    private char delimiter;
    private String[] tokens;

    public SimpleTok(){
        this( ' ', 0x7FFFFFFF );
    }
    public SimpleTok(char delimiter ){
        this( delimiter, 0x7FFFFFFF );
    }
    public SimpleTok(char delimiter, int limit ){
        this.setDelimiter(delimiter);
        this.limit = limit;
    }

    @Override
    public ITokenizer setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public ITokenizer setDelimiter(char... delimiter) {
        this.delimiter = (delimiter.length == 0)? '\0' : delimiter[0];
        if(this.delimiter == ' '){
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

    private boolean isEscape(char symbol){
        return symbol == escape;
    }
    private boolean isDelimiter(char symbol){
        return symbol == delimiter || whitespace.isWhitespace(symbol);
    }

    @Override
    public ITokenizer parse() {
        // Rehearse to get size
        int count = 0, len = text.length();
        boolean escaped = false;
        int i, j = 0, k = 0;
        for(i = 0; i < len; i++){
            if(isEscape(text.charAt(i))){
                escaped = true;
            }
            else if(escaped){
                escaped = false;
            }
            else if(isDelimiter(text.charAt(i))){
                if( i != j ){
                    count++;
                    // Limit size, if limit passed
                    if( count == limit ){
                        i = j;
                        break;
                    }
                }
                j=i+1;
            }
        }
        if( i != j ){
            count++;
        }
        // Set array and run again to populate
        tokens = new String[count];
        j = 0;
        escaped = false;
        for(i = 0; i < len; i++){
            if(isEscape(text.charAt(i))){
                text = text.substring(0, i) + text.substring(i + 1);
                System.out.println("new text:" + text);
                len--;
                i--;
                escaped = true;
            }
            else if(escaped){
                escaped = false;
            }
            else if(isDelimiter(text.charAt(i))){
                if( i != j ){
                    if( k >= limit-1){
                        break;
                    }
                    tokens[k] = text.substring(j, i);
                    k++;

                }
                j=i+1;
            }
        }
        if( i != j ){
            tokens[k] = text.substring(j);
        }
        return this;
    }

    @Override
    public ArrayList<String> toList() {
        ArrayList<String> out = new ArrayList<>(tokens.length);
        for(String tok: tokens) {
            out.add(tok);
        }
        return out;
    }

    @Override
    public String[] toArray() {
        return tokens;
    }

    @Override
    public int[] indents() {
        throw new IllegalStateException("SimpleTok does not implement indents()");
    }
}
