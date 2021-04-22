package wordtraitutil.impl;

import tokenizer.iface.ITokenizer;
import tokenizer.impl.Tokenizer;
import wordtraitutil.iface.IExtract;
import wordtraitutil.iface.IWordTraitParser;
import wordtraitutil.iface.IWordTraitClient;

import java.util.List;

public class ExtractImplGroup {
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
}
