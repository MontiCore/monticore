// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import de.monticore.visitor.ITraverser;

/**
 * Contains the traverser create
 * {@link de.monticore.interpreter.calculations.MICalculation}
 * and the corresponding data object used in said traverser.
 *
 * @param <D>       the type of the exchanged data object.
 * @param data      the data shared corresponding to the traverser.
 * @param traverser the traverser corresponding to the data.
 */
public record TraverserAndIData<D extends InterpreterData>(
    ITraverser traverser,
    D data
) {
}
