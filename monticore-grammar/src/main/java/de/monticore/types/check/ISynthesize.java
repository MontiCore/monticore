/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

import java.util.Optional;

public interface ISynthesize extends MCBasicTypesVisitor {

  Optional<SymTypeExpression> getResult();

  void init();

}
