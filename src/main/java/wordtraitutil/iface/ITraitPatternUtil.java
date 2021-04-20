package wordtraitutil.iface;

/** A utility for TRAIT_PATTERN enums to match text.
 *  Implementation takes advantage of two things known about the data:
 *    1. text and patterns are always sorted in string by ascii value.
 *    2. text contains small, known set of characters which can be represented in pattern.
 *  If these are not both true, don't use this algorithm.
 *
 *  Pattern uses a simple language to specify allowed content in text:
 *    even char (e.g. pattern[0], pattern[2]...) defines presence of a trait in text:
 *      + means required
 *      - means excluded
 *      . means don't care
 *    odd char (e.g. pattern[1], pattern[3]...) gives the trait to be found; exact match
 *      against corresponding char in text (see tests)
 *  */
public interface ITraitPatternUtil {

    /** Matches text against pattern
     * @param text string to be checked
     * @param pattern specially formatted string
     * @return true if text matches pattern
     */
    boolean match(String text, String pattern);

    /** For dev, to ensure text strings are properly ordered
     * @param text standard string, no special format
     * @return all chars sorted by ascii value */
    String sortText(String text);

    /** For dev, to ensure pattern strings are properly ordered
     * @param pattern specially formatted string
     * @return pattern sorted by odd-numbered chars */
    String sortPattern(String pattern);
}
