package com.sq.ipc;

//i mport sun.misc.Signal;

/**
 * @reference Unix/Linux based platform the signals are SEGV, ILL, FPE, BUS, SYS, CPU, FSZ, ABRT, INT, TERM, HUP, USR1, QUIT,
 * BREAK, TRAP, PIPE. <br/>
 * For Windows based platform, the signals are SEGV, ILL, FPE, ABRT, INT, TERM, BREAK.
 */

/**
 * Utility to allow the registration of a SIGINT handler that hides the unsupported {@link Signal} class.
 */
public class SigInt {
    /**
     * Register a task to be run when a SIGINT is received.
     *
     * @param task to run on reception of the signal.
     */
    public static void register(final Runnable task) {
        Signal.handle(new Signal("INT"), (signal) -> task.run());
    }
}
