package edu.berkeley.cs186.database.concurrency;

public enum LockType {
    S,   // shared
    X,   // exclusive
    IS,  // intention shared
    IX,  // intention exclusive
    SIX, // shared intention exclusive
    NL;  // no lock held

    /**
     * This method checks whether lock types A and B are compatible with
     * each other. If a transaction can hold lock type A on a resource
     * at the same time another transaction holds lock type B on the same
     * resource, the lock types are compatible.
     */
    public static boolean compatible(LockType a, LockType b) {
        if (a == null || b == null) {
            throw new NullPointerException("null lock type");
        } else if(a == NL || b == NL) {
            return true;
        } else if (a == X || b == X){
            return false;
        } else if (a == IS || b == IS){
            return true;
        } else if (a == SIX || b == SIX) {
            return false;
        } else if ((a == IX && b == IX) || (a == S && b == S)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method returns the least permissive lock on the parent resource
     * that must be held for a lock of type A to be granted.
     */
    public static LockType parentLock(LockType a) {
        if (a == null) {
            throw new NullPointerException("null lock type");
        }
        if (a == NL){
            return NL;
        } else if (a == S || a == IS){
            return IS;
        } else {// if (a == X || a == IX || a == SIX){
            return IX;
        }
    }

    /**
     * This method returns whether a lock can be used for a situation
     * requiring another lock (e.g. an S lock can be substituted with
     * an X lock, because an X lock allows the transaction to do everything
     * the S lock allowed it to do).
     */
    public static boolean substitutable(LockType substitute, LockType given) {
        if (given == null || substitute == null) {
            throw new NullPointerException("null lock type");
        } else if (given == substitute) {
            return true;
        } else if (given == NL) {
            return true;
        } else if (given == S) {
            return ((substitute == X) || (substitute == SIX));
        } else if (given == X) {
            return false;
        } else if (given == IS) {
            return ((substitute == X) || (substitute == IX) || (substitute == SIX));
        } else if (given == IX) {
            return ((substitute == X) || (substitute == SIX));
        } else if (given == SIX) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    public String toString() {
        switch (this) {
        case S: return "S";
        case X: return "X";
        case IS: return "IS";
        case IX: return "IX";
        case SIX: return "SIX";
        case NL: return "NL";
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }
}

