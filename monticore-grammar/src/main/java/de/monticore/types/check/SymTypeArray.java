package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * Arrays of a certain dimension (>= 1)
 */
public class SymTypeArray extends SymTypeExpression {
  
  /**
   * An arrayType has a dimension
   */
  protected int dim;
  
  /**
   * An Array has an argument Type
   */
  protected SymTypeExpression argument;
  
  public SymTypeArray(int dim, SymTypeExpression argument) {
    this.dim = dim;
    this.argument = argument;
    this.setTypeInfo(DefsTypeBasic._array);
  }
  

  // ------------------------------------------------------------------ Functions
  
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
  public String print() {
    StringBuffer r = new StringBuffer(getArgument().print());
    for(int i = 1; i<=dim;i++){
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
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeArray");
    jp.memberJson("argument", argument.printAsJson());
    jp.member("dim", dim);
    jp.endObject();
    return jp.getContent();
  }
  
  
  // --------------------------------------------------------------------------
  
}
