/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ast;

/**
 * interface that is implemented by generated ast classes that describe
 * replacements
 *
 */
public interface IReplacement extends ITFElement {

  ITFElement getLhs();

  ITFElement getRhs();

  boolean isPresentLhs();

  boolean isPresentRhs();

  default ITFElement getTFElement(){
    return isPresentLhs() ? getLhs() : getRhs();
  }

  IReplacementOperator getReplacementOp();

}
