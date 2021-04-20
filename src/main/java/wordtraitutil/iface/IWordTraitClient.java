package wordtraitutil.iface;

import wordtraitutil.WORD_TRAIT;

/** A visitor pattern for clients of WordTrait utilities to extract content from found pattern */
public interface IWordTraitClient {
    void receiveContent(String... content);
    void receiveContent(int... content);
    void receiveContent(WORD_TRAIT content);
}
