/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.mcbasictypes._ast;


import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public  class ASTMCPrimitiveType extends ASTMCPrimitiveTypeTOP {

  protected ISymbol definingSymbol;

  @Override
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }

  public boolean isBoolean(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.BOOLEAN;
  }

  public boolean isByte(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.BYTE;
  }
  public boolean isChar(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.CHAR;
  }
  public boolean isShort(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.SHORT;
  }
  public boolean isInt(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.INT;
  }
  public boolean isFloat(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.FLOAT;
  }
  public boolean isLong(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.LONG;
  }
  public boolean isDouble(){
    return this.getPrimitive()==ASTConstantsMCBasicTypes.DOUBLE;
  }

  /**
   * toString delivers a short name like "int" for the primitive Types
   * @return
   */
  public String toString(){
    return printType();
  }
}
