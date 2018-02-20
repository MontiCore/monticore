/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

public class ASTGenericType extends ASTGenericTypeTOP {
  
  protected ASTGenericType() {
  }
  
  protected ASTGenericType(
      int dimension,
      java.util.List<String> names,
      java.util.List<de.monticore.grammar.grammar._ast.ASTGenericType> genericTypes)
  {
    setDimension(dimension);
    setNameList(names);
    setGenericTypeList(genericTypes);
  }
  
  public String toString() {
    return de.monticore.grammar.HelperGrammar.printGenericType(this);
  }
  
  public String getTypeName() {
    return de.monticore.grammar.HelperGrammar.printGenericType(this);
  }
  
  public boolean isExternal() {
    return true;
  };
  
}
