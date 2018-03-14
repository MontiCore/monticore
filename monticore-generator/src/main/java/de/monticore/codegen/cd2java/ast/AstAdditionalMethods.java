/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

public enum AstAdditionalMethods {
  
  deepEquals("public boolean deepEquals(Object o);"),
  
  deepEqualsWithOrder("public boolean deepEquals(Object o, boolean forceSameOrder);"),
  
  deepEqualsWithComments("public boolean deepEqualsWithComments(Object o);"),
  
  deepEqualsWithCommentsWithOrder("public boolean deepEqualsWithComments(Object o, boolean forceSameOrder);"),
  
  equalAttributes("public boolean equalAttributes(Object o);"),
  
  equalsWithComments("public boolean equalsWithComments(Object o);"),
  
  // %s the ast-class name as return type
  deepClone("public %s deepClone();"),
  
  // %s the ast-class name as return and input parameter type
  deepCloneWithOrder("public %s deepClone(%s result);"),
  
  // %s the ast-class name as return type
  _construct("protected %s _construct();"),
  
  get_Children("public java.util.Collection<de.monticore.ast.ASTNode> get_Children();"),
  
  // %s the language specific visitor-type as full-qualified-name
  accept("public void accept(%s visitor);");
  
  private String methodDeclaration;
  
  private AstAdditionalMethods(String header) {
    this.methodDeclaration = header;
  }
  
  public String getDeclaration() {
    return methodDeclaration;
  }
  
}
