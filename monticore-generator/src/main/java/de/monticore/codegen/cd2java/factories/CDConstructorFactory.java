package de.monticore.codegen.cd2java.factories;

import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

  public ASTCDConstructor createFullConstructor(final ASTModifier modifier, final ASTCDClass cdClass) {
    List<ASTCDParameter> parameterList = CDParameterFactory.getInstance().createParameters(cdClass.getCDAttributeList());
    return createConstructor(modifier, cdClass.getName(), parameterList);
  }

  public ASTCDConstructor createDefaultConstructor(final ASTModifier modifier, final ASTCDClass cdClass) {
    return createConstructor(modifier, cdClass.getName(), Collections.emptyList());
  }

  public ASTCDConstructor createConstructor(final ASTModifier modifier, final String name) {
    return createConstructor(modifier, name, Collections.emptyList());
  }

  public ASTCDConstructor createConstructor(final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(modifier.deepClone())
        .setName(name)
        .setCDParameterList(parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
        .build();
  }
}
