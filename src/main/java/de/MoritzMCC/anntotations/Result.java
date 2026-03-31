package de.MoritzMCC.anntotations;

public enum Result {
    CONTINUE,
    SKIP,
    CANCEL,
    CANCEL_AND_EXECUTE;

    static Result get(boolean bool){
        return bool ? CONTINUE : SKIP;
    }

    public boolean shouldContinue(){
        return this == CONTINUE || this == CANCEL_AND_EXECUTE;
    }

    public boolean shouldCancel(){
        return this == CANCEL || this == CANCEL_AND_EXECUTE;
    }

}
