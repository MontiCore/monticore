package de.monticore.codegen.cd2java.ast;

public enum AstOptionalGetMethods {

  get("public %s get%s();"),

  getOpt("public Optional<%s> get%sOpt();"),

  isPresent("public boolean isPresent%s();");

  private String methodDeclaration;

  private AstOptionalGetMethods(String header) {
    this.methodDeclaration = header;
  }

  public String getDeclaration() {
    return methodDeclaration;
  }
}
