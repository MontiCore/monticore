/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

/**
 * A common interface that can be used to synthesize SymTypeExpressions from MCTypes
 */
public interface ISynthesize {

  /**
   * Collects the synthesized SymTypeExpressions after
   * using the traverser to traverse the MCType
   */
  Optional<SymTypeExpression> getResult();

  /**
   * Initializes the traverser with the correct visitors and handlers
   */
  void init();

  MCBasicTypesTraverser getTraverser();

}
