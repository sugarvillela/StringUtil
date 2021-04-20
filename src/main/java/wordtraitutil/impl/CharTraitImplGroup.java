package wordtraitutil.impl;

import wordtraitutil.iface.ICharTrait;

public abstract class CharTraitImplGroup {
    public static class CharTrait implements ICharTrait, Comparable<ICharTrait> {
        protected final char watchedTrait;
        protected boolean foundTrait;

        public CharTrait(char watchedTrait) {
            this.watchedTrait = watchedTrait;
            this.foundTrait = false;
        }

        @Override
        public void clear() {
            this.foundTrait = false;
        }

        @Override
        public char watchedTrait() {
            return watchedTrait;
        }

        @Override
        public boolean checkTrait(char curr) {
            if(curr == watchedTrait){
                return (foundTrait = true);
            }
            return false;
        }

        @Override
        public boolean foundTrait() {
            return foundTrait;
        }

        @Override
        public int compareTo(ICharTrait other) {
            return this.watchedTrait - other.watchedTrait();
        }

        protected boolean isVisibleAscii(char curr){
            return (32 < curr && curr < 127);
        }
        protected boolean isNumeric(char curr){
            return (47 < curr && curr < 58);
        }
        protected boolean isAlpha(char curr){
            return (96 < curr && curr < 123) || (64 < curr && curr < 91);
        }
        protected boolean isAlphaNumeric(char curr){
            return (96 < curr && curr < 123) || (47 < curr && curr < 58) || (64 < curr && curr < 91);
        }
        protected boolean isLower(char curr){
            return (96 < curr && curr < 123);
        }
        protected boolean isUpper(char curr){
            return (64 < curr && curr < 91);
        }

    }
    public static class CharTraitVisibleAscii extends CharTrait {
        public CharTraitVisibleAscii(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(this.isVisibleAscii(curr)){
                return (foundTrait = true);
            }
            return false;
        }
    }
    public static class CharTraitNumeric extends CharTrait {
        public CharTraitNumeric(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(this.isNumeric(curr)){
                return (foundTrait = true);
            }
            return false;
        }
    }
    public static class CharTraitUpper extends CharTrait {
        public CharTraitUpper(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(this.isUpper(curr)){
                return (foundTrait = true);
            }
            return false;
        }
    }
    public static class CharTraitLower extends CharTrait {
        public CharTraitLower(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(this.isLower(curr)){
                return (foundTrait = true);
            }
            return false;
        }
    }
    public static class CharTraitAlpha extends CharTrait {
        public CharTraitAlpha(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(this.isAlpha(curr)){
                return (foundTrait = true);
            }
            return false;
        }
    }
    public static class CharTraitIdentifier extends CharTrait {
        public CharTraitIdentifier(char watchedTrait) {
            super(watchedTrait);
        }

        @Override
        public boolean checkTrait(char curr) {
            if(
                (curr == '_' || this.isAlphaNumeric(curr))
            ){
                return (foundTrait = true);
            }
            return false;
        }
    }
}
