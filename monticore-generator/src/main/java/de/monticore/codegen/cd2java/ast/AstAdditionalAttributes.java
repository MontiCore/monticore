/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast;

public enum AstAdditionalAttributes {
  
  symbol("public Optional<? extends Symbol> symbol;"),
  
  enclosingScope("public Optional<? extends Scope> enclosingScope;");
  
  private String attributeDeclaration;
  
  private AstAdditionalAttributes(String def) {
    this.attributeDeclaration = def;
  }
  
  public String getDeclaration() {
    return attributeDeclaration;
  }
  
}
