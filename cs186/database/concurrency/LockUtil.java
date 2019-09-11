package edu.berkeley.cs186.database.concurrency;

import edu.berkeley.cs186.database.BaseTransaction;

import java.util.*;

/**
 * LockUtil is a declarative layer which simplifies multigranularity lock acquisition
 * for the user (you, in the second half of Part 2). Generally speaking, you should use LockUtil
 * for lock acquisition instead of calling LockContext methods directly.
 */
public class LockUtil {
    /**
     * Ensure that TRANSACTION can perform actions requiring LOCKTYPE on LOCKCONTEXT.
     *
     * This method should promote/escalate as needed, but should only grant the least
     * permissive set of locks needed.
     *
     * lockType must be one of LockType.S, LockType.X, and behavior is unspecified
     * if an intent lock is passed in to this method (you can do whatever you want in this case).
     *
     * If TRANSACTION is null, this method should do nothing.
     */
    public static void ensureSufficientLockHeld(BaseTransaction transaction, LockContext lockContext,
                                                LockType lockType) {
        if (transaction == null) {
            return;
        }
        Stack<LockContext> totalContext = new Stack<LockContext>();
        Stack<LockType> totalType = new Stack<LockType>();
        LockContext cContext = lockContext;
        LockType rType = lockType;
        LockType cType = get_LockType(transaction, cContext);
        totalContext.add(cContext);
        totalType.add(rType);
        while (cContext.parent != null) {
            cContext = cContext.parent;
            rType = LockType.parentLock(rType);
            cType = get_LockType(transaction, cContext);
            if (cType == LockType.NL) {
                totalContext.add(cContext);
                totalType.add(rType);
            } else if (LockType.substitutable(cType, rType)) {
                break;
            } else {
                totalContext.add(cContext);
                totalType.add(rType);
            }
        }
        while (!totalContext.isEmpty()) {
            cContext = totalContext.pop();
            rType = totalType.pop();
            cType = get_LockType(transaction, cContext);
            if (cType == rType) {
                continue;
            } else if (cType == LockType.NL) {
                cContext.acquire(transaction, rType);
            } else if (LockType.substitutable(rType, cType)) {
                cContext.promote(transaction, rType);
            } else {
                cContext.escalate(transaction);
                cType = get_LockType(transaction, cContext);
                if ((LockType.substitutable(rType, cType)) && (rType != cType)) {
                    cContext.promote(transaction, rType);
                }
            }
        }
    }

    public static LockType get_LockType(BaseTransaction t, LockContext LC) {
        long transNum = t.getTransNum();
        for (Lock l : LC.lockman.getLocks(LC.name)) {
            if (transNum == l.transactionNum) {
                return l.lockType;
            }
        }
        return LockType.NL;
    }

}
