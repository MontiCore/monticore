/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts;


public abstract class ASTCompilationUnit extends ASTNodeMock {

  private String packageName = "";

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
}
