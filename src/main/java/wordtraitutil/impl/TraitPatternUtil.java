package wordtraitutil.impl;

import wordtraitutil.iface.ITraitPatternUtil;

import java.util.Arrays;

public class TraitPatternUtil implements ITraitPatternUtil {
    private static TraitPatternUtil instance;

    public static TraitPatternUtil initInstance(){
        return (instance == null)? (instance = new TraitPatternUtil()): instance;
    }

    private TraitPatternUtil(){}

    @Override
    public boolean match(String text, String pattern) {
        int t = 0, p = 0;
        char tCurr, presence, trait;
        boolean equal;
        while(t < text.length() && p < pattern.length()){
            tCurr = text.charAt(t);
            presence = pattern.charAt(p);
            trait = pattern.charAt(p + 1);
            equal = tCurr == trait;

            switch(presence){
                case '+':   // require
                    if(equal){
                        t += 1;
                        p += 2;
                        break;
                    }
                    else{
                        return false;
                    }
                case '-':   // exclude
                    if(equal){
                        return false;
                    }
                    else{
                        p += 2;
                        break;
                    }
                case '.':   // don't care
                    if(equal){
                        t += 1;
                    }
                    p += 2;
                    break;
                default:
                    throw new IllegalStateException("Bad control character: " + presence);
            }
        }
        if(p == pattern.length()){
            if(t != text.length()){
                throw new IllegalStateException("Every possible char in text must be designated in pattern");
            }
            else{
                return true;
            }
        }
        return t == text.length();
    }

    @Override
    public String sortText(String text) {
        char[] chars = text.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    @Override
    public String sortPattern(String pattern) {
        if(pattern.length() % 2 != 0){
            throw new IllegalStateException("Require even number length");
        }
        SortNode[] sortNodes = new SortNode[pattern.length()/2];
        for(int i = 0, k = 0; i < pattern.length(); i += 2, k++){
            sortNodes[k] = new SortNode(pattern.charAt(i), pattern.charAt(i + 1));
        }

        Arrays.sort(sortNodes);

        char[] chars = new char[pattern.length()];
        for(int i = 0, k = 0; k < pattern.length(); i++, k += 2){
            chars[k] = sortNodes[i].presence();
            chars[k + 1] = sortNodes[i].trait();
        }
        return new String(chars);
    }

    private class SortNode implements Comparable<SortNode>{
        private final char presence, trait;

        private SortNode(char presence, char trait) {
            this.presence = presence;
            this.trait = trait;
        }

        char presence(){
            return presence;
        }
        char trait(){
            return trait;
        }

        @Override
        public int compareTo(SortNode other) {
            return this.trait - other.trait();
        }
    }
}
