package wordtraitutil.iface;

import wordtraitutil.WORD_TRAIT;

public interface IWordTraitParser {
    IWordTraitParser setText(String text);

    IWordTraitParser parse();

    boolean foundTrait();

    String getWatchedTraits();

    String getFoundTraits();

    interface IBuilder {

        IBuilder traits(ICharTrait... traits);

        IBuilder skipSymbols(String openingSymbols);

        IBuilder skipSymbols(char oneOpeningSymbol);

        IBuilder skipSymbols(char openingSymbol, char closingSymbol);

        IBuilder skipSymbols(char[] oMap, char[] cMap);

        IBuilder keepEscapeSymbol();

        IWordTraitParser build();
    }
}
