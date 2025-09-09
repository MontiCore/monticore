/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ast;

/**
 * interface that is implemented by generated ast classes that describe patterns
 *
 */
public interface IPattern extends ITFObject {

  default ITFElement getTFElement() {
    return this;
  }

}
