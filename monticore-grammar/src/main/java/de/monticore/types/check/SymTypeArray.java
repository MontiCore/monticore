/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * Arrays of a certain dimension (>= 1)
 */
public class SymTypeArray extends SymTypeExpression {

  /**
   * An arrayType has a dimension (>= 1)
   */
  protected int dim;

  /**
   * An Array has an argument Type
   */
  //todo NP what is the argument for?
  protected SymTypeExpression argument;

  /**
   * Constructor
   *
   * @param dim      dimension
   * @param argument Argument Type
   * @param typeSymbol loader for the Type-Symbol that defines this type
   */
  public SymTypeArray(TypeSymbol typeSymbol, int dim, SymTypeExpression argument) {
    this.typeSymbol = typeSymbol;
    this.dim = dim;
    this.argument = argument;
  }

  // ------------------------------------------------------------------ Functions


  @Override
  public boolean isArrayType() {
    return true;
  }

  public int getDim() {
    return dim;
  }

  public void setDim(int dim) {
    this.dim = dim;
  }

  public SymTypeExpression getArgument() {
    return argument;
  }

  public void setArgument(SymTypeExpression argument) {
    this.argument = argument;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    StringBuffer r = new StringBuffer(getArgument().print());
    for (int i = 1; i <= dim; i++) {
      r.append("[]");
    }
    return r.toString();
  }

  /**
   * printToJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeArray");
    jp.memberJson("argument", argument.printAsJson());
    jp.member("dim", dim);
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeArray deepClone() {
    TypeSymbolSurrogate loader = new TypeSymbolSurrogate(typeSymbol.getName());
    loader.setEnclosingScope(typeSymbol.getEnclosingScope());
    return new SymTypeArray(loader,
        this.dim, this.argument.deepClone());
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypeArray)){
      return false;
    }
    SymTypeArray symArr = (SymTypeArray) sym;
    if(this.dim!=symArr.dim){
      return false;
    }
    if(this.typeSymbol == null ||symArr.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symArr.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symArr.typeSymbol.getName())){
      return false;
    }
    if(!this.getArgument().deepEquals(symArr.getArgument())){
      return false;
    }
    return this.print().equals(symArr.print());
  }

  // --------------------------------------------------------------------------
}
