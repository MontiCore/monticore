package de.monticore.rte.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;

import java.io.File;

public abstract class InterfaceRteGenerator extends RteGenerator {

  public InterfaceRteGenerator(File outputDirectory) {
    super(outputDirectory);
  }

  @Override
  protected ASTCDConstructor constructConstructor(ASTCDConstructor constructor, int n) {
    return null;
  }

  @Override
  public ASTCDElement constructClass(int n) {
    return CD4CodeMill.cDInterfaceBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setTypeParameters(constructTypeParameters(n))
        .setName(getClassName(n))
        .setCDExtendUsage(constructExtendUsage(n))
        .addAllCDMembers(constructMembers(n)).build();
  }

  public ASTCDExtendUsage constructExtendUsage(int n) {
    return null;
  }
}
