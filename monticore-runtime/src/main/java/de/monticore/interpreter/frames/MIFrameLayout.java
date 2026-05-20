// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.frames;

/**
 * describes the layout of an {@link MIFrame},
 * each layout can be used to create multiple frames,
 * e.g., calls of a recursive function.
 */
public interface MIFrameLayout {

  int sizeBooleans();

  int sizeIntegers();

  int sizeDoubles();

  int sizeObjects();

}
