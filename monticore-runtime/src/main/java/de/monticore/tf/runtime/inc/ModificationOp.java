/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

/**
 * Enumeration of types of attribute modification operations.
 */
public enum ModificationOp {
  /**
   * A new value has been set to an attribute.
   */
  SET,
  /**
   * An attribute value has been cleared or unset.
   */
  UNSET,
  /**
   * An attribute value has been replaced with a new value.
   */
  REPLACE
}
