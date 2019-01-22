package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;

public class CDConstructorFactory {

  private static CDConstructorFactory cdConstructorFactory;

  private CDConstructorFactory() {
  }

  public static CDConstructorFactory getInstance() {
    if (cdConstructorFactory == null) {
      cdConstructorFactory = new CDConstructorFactory();
    }
    return cdConstructorFactory;
  }

  public ASTCDConstructor createProtectedFullConstructor(final ASTCDClass ast) {
    List<ASTCDParameter> parameterList = CDParameterFactory.getInstance().createParameters(ast.getCDAttributeList());
    return createProtectedConstructorBuilder(ast.getName())
        .setCDParameterList(parameterList)
        .build();
  }

  public ASTCDConstructor createProtectedDefaultConstructor(final ASTCDClass ast) {
    return createProtectedDefaultConstructor(ast.getName());
  }


  public ASTCDConstructor createProtectedDefaultConstructor(final String name) {
    return createProtectedConstructorBuilder(name).build();
  }

  private ASTCDConstructorBuilder createProtectedConstructorBuilder(final String name) {
    return CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(ModifierBuilder.builder().Protected().build())
        .setName(name);
  }
}
