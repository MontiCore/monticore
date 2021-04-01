/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;


public class SymTypeVariable extends SymTypeExpression {

  /**
   * Constructor:
   */
  public SymTypeVariable(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  public String getVarName() {
    return typeSymbol.getFullName();
  }

  public void setVarName(String name) {
    typeSymbol.setName(name);
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    return typeSymbol.getName();
  }

  @Override
  public String printFullName() {
    return getVarName();
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeVariable");
    jp.member("varName", getVarName());
    jp.endObject();
    return jp.getContent();
  }

  /**
   * Am I primitive? (such as "int")
   */
  public boolean isTypeConstant() {
    return false;
    /**
     *     Please note that the var itself is not a primitive type, but it might
     *     be instantiated into a primitive type
     *     unless we always assume boxed implementations then return false would be correct
     *     according to the W algorithm of Hindley-Milner, we regard a variable
     *     a monomorphic type on its own and do hence not regard it as primitive type
     */
  }

  public boolean isTypeVariable() {
    return true;
  }

  @Override
  public SymTypeVariable deepClone() {
    return new SymTypeVariable(this.typeSymbol);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypeVariable)){
      return false;
    }
    SymTypeVariable symVar = (SymTypeVariable) sym;
    if(this.typeSymbol == null ||symVar.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symVar.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symVar.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symVar.print());
  }

  // --------------------------------------------------------------------------
}
