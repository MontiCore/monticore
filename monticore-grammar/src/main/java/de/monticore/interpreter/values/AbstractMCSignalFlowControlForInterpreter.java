package de.monticore.interpreter.values;

import de.monticore.values.MCSignalFlowControl;

/**
 * These values control the flow of the interpreter thread.
 * As we are using native Java {@link Thread},
 * this is done using unchecked {@link Throwable}.
 * Unfortunately, there is no good candidate;
 * The Throwable should be unchecked
 * and Error seemed even less applicable (s.a.JLS 21 11.1.1.).
 */
public abstract class AbstractMCSignalFlowControlForInterpreter
    extends RuntimeException
    implements MCSignalFlowControl {

}
