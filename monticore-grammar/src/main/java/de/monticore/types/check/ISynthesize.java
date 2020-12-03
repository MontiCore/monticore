/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public interface ISynthesize {

  Optional<SymTypeExpression> getResult();

  void init();

  MCBasicTypesTraverser getTraverser();

}
