package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ETypeSymbol extends ETypeSymbolTOP {

  public ETypeSymbol(String name) {
    super(name);
  }

  public ASTMCType getType() {
    throw new NotImplementedException();
  }
}
