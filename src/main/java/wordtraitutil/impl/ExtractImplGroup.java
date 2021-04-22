package wordtraitutil.impl;

import tokenizer.iface.ITokenizer;
import tokenizer.impl.Tokenizer;
import wordtraitutil.iface.IExtract;
import wordtraitutil.iface.IWordTraitParser;
import wordtraitutil.iface.IWordTraitClient;

import java.util.List;

public class ExtractImplGroup {

    /*=====Array extractors===========================================================================================*/

    public static abstract class ExtractList implements IExtract {
        protected final ITokenizer tokenizer;
        protected final char sep;

        public ExtractList(char sep) {
            this.sep = sep;
            tokenizer = Tokenizer.builder().delimiters(sep).skipSymbols('\'').keepSkipSymbol().
                    keepEscapeSymbol().build();
        }
    }

    public static class ExtractStrings extends ExtractList {
        public static ExtractStrings initInstance(char sep){
            return new ExtractStrings(sep);
        }

        private ExtractStrings(char sep) {
            super(sep);
        }

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            client.receiveContent(tokenizer.setText(text).parse().toArray());
            return true;
        }
    }

    public static class ExtractNumbers extends ExtractList {
        public static ExtractNumbers initInstance(char sep){
            return new ExtractNumbers(sep);
        }

        private ExtractNumbers(char sep) {
            super(sep);
        }

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            List<String> stringList = tokenizer.setText(text).parse().toList();
            int[] intList = new int[stringList.size()];
            for(int i = 0; i < stringList.size(); i++){
                intList[i] = Integer.parseInt(stringList.get(i)); // validated by WORD_TRAIT pattern match
            }
            client.receiveContent(intList);
            return true;
        }
    }

    /*=====Singular extractors========================================================================================*/

    public static class ExtractString implements IExtract {
        private static ExtractString instance;

        public static ExtractString initInstance(){
            return (instance == null)? (instance = new ExtractString()): instance;
        }

        private ExtractString(){}

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            client.receiveContent(text);
            return true;
        }
    }

    public static class ExtractNumber implements IExtract {
        private static ExtractNumber instance;

        public static ExtractNumber initInstance(){
            return (instance == null)? (instance = new ExtractNumber()): instance;
        }

        private ExtractNumber(){}

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            client.receiveContent(Integer.parseInt(text));
            return true;
        }
    }

    /*=====Quoted extractors==========================================================================================*/

    public static abstract class ExtractQuotedBase implements IExtract {
        protected final ITokenizer quoteTokenizer;
        protected final String quote;

        public ExtractQuotedBase() {
            this.quote = "'";
            quoteTokenizer = Tokenizer.builder().delimiters('\'').tokenizeDelimiter().
                    keepEscapeSymbol().build();
        }
        protected String unquote(String text){
            List<String> tok = quoteTokenizer.setText(text).parse().toList();
            if(tok.size() == 3 && quote.equals(tok.get(0)) &&  quote.equals(tok.get(2))){
                return tok.get(1);
            }
            return null;
        }
    }

    public static class ExtractQuoted extends ExtractQuotedBase {
        private static ExtractQuoted instance;

        public static ExtractQuoted initInstance(){
            return (instance == null)? (instance = new ExtractQuoted()): instance;
        }

        private ExtractQuoted() {}

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            String unquote = this.unquote(text);
            if(unquote != null){
                client.receiveContent(unquote);
                return true;
            }
            return false;
        }
    }

    public static class ExtractQuotedList extends ExtractQuotedBase {
        private static ExtractQuotedList instance;

        public static ExtractQuotedList initInstance(){
            return (instance == null)? (instance = new ExtractQuotedList()): instance;
        }

        private final ITokenizer listTokenizer;

        private ExtractQuotedList() {
            listTokenizer = Tokenizer.builder().delimiters(',').skipSymbols('\'').keepSkipSymbol().
                    keepEscapeSymbol().build();
        }

        @Override
        public boolean getContent(IWordTraitClient client, String text) {
            List<String> quotedList = listTokenizer.setText(text).parse().toList();
            String[] unquotedList = new String[quotedList.size()];
            for(int i = 0; i < quotedList.size(); i++){
                unquotedList[i] = this.unquote(quotedList.get(i));
                if(unquotedList[i] == null){
                    return false;
                }
            }
            client.receiveContent(unquotedList);
            return false;
        }
    }
}
