/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagtest.cdbasis4tags._ast;

public class ASTCDPackage extends ASTCDPackageTOP {
  @Override
  public String getName() {
    return getMCQualifiedName().getQName();
  }
}
