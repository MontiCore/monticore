/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ast;

/**
 * interface that is implemented by generated ast classes that describe
 * attribute patterns
 *
 */
public interface IAttributePattern extends ITFAttribute {

  default ITFElement getTFElement() {
    return this;
  }

}
