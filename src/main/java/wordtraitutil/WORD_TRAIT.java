package wordtraitutil;

import wordtraitutil.iface.IExtract;
import wordtraitutil.iface.ITraitPatternUtil;
import wordtraitutil.iface.IWordTraitParser;
import wordtraitutil.iface.IWordTraitClient;
import wordtraitutil.impl.CharTraitImplGroup;
import wordtraitutil.impl.TraitPatternUtil;
import wordtraitutil.impl.WordTraitParser;

import static wordtraitutil.impl.ExtractImplGroup.*;

public enum WORD_TRAIT {
    NUM_PAR         ("-'-*-,-.-:-A+C+N-[-_", ExtractNumber.initInstance()),   // "CN"
    NUM_LIST        ("-'-*+,-.-:-A+C+N-[-_", ExtractNumbers.initInstance(',')),   // ",CN"
    STR_PAR         ("+'-*-,-.-:-A+C-N-[-_", ExtractQuoted.initInstance()),   // "'C"
    STR_LIST        ("+'-*+,-.-:-A+C-N-[-_", ExtractQuotedList.initInstance()),   // "',C"
    ID_PAR          ("-'-*-,-.-:+A+C.N-[._", ExtractString.initInstance()),   // "ACN_"
    ID_LIST         ("-'-*+,-.-:+A+C.N-[._", ExtractStrings.initInstance(',')),   // ",ACN_"
    ID_SEP          ("-'-*-,+.-:+A+C.N-[._", ExtractStrings.initInstance('.')),   // ".ACN_"
    STAR_ID         ("-'+*-,-.-:+A+C.N-[._", ExtractString.initInstance()),   // *ACN_
    NUM_RANGE       ("-'-*-,-.+:-A+C+N-[-_", ExtractNumbers.initInstance(':')),   // ":CN"
    STAR_NUM        ("-'+*-,-.-:-A+C+N+[-_", ExtractNumber.initInstance()),   // "*CN["
    STAR_RANGE      ("-'+*-,-.+:-A+C+N+[-_", ExtractNumbers.initInstance(':')),   // "*:CN["
    ;

    private static final IWordTraitParser wordTraitParser = WordTraitParser.builder().
        skipSymbols('\'').keepEscapeSymbol().
        traits(
                new CharTraitImplGroup.CharTrait('\''),
                new CharTraitImplGroup.CharTrait('.'),
                new CharTraitImplGroup.CharTrait(','),
                new CharTraitImplGroup.CharTrait(':'),
                new CharTraitImplGroup.CharTrait('*'),
                new CharTraitImplGroup.CharTrait('['),
                new CharTraitImplGroup.CharTrait('_'),
                new CharTraitImplGroup.CharTraitAlpha('A'),
                new CharTraitImplGroup.CharTraitVisibleAscii('C'),
                new CharTraitImplGroup.CharTraitNumeric('N')
        ).build();

    private final ITraitPatternUtil traitPatternUtil;
    private final IExtract extract;
    private final String p;

    WORD_TRAIT(String p, IExtract extract) {
        this.extract = extract;
        traitPatternUtil = TraitPatternUtil.initInstance();
        this.p = traitPatternUtil.sortPattern(p);
    }

    public static IWordTraitParser getWordTraitParser(){
        return wordTraitParser;
    }

    public static boolean tryParse(IWordTraitClient client, String text) {
        String traitText = wordTraitParser.setText(text).parse().getFoundTraits();
        WORD_TRAIT traitEnum = fromTraitText(traitText);
        if(traitEnum != null){
            client.receiveContent(traitEnum);
            return traitEnum.extract.getContent(client, text);
        }
        return false;
    }

    public static WORD_TRAIT fromTraitText(String text){
        for(WORD_TRAIT wordTraitEnum : values()){
            if(wordTraitEnum.traitPatternUtil.match(text, wordTraitEnum.p)){
                return wordTraitEnum;
            }
        }
        return null;
    }
}
