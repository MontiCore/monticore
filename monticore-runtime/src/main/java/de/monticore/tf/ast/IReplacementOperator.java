/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ast;


/**
 * interface that is implemented by replacementOperator
 * replacements
 *
 */
public interface IReplacementOperator {

  boolean isFirst();

  boolean isLast();

  boolean isDefault();

  boolean isRelative();

  boolean isInplace();

}
