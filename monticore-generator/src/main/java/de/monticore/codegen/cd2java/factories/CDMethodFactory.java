package de.monticore.codegen.cd2java.factories;

import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CDMethodFactory {
  
  private static CDMethodFactory cdMethodFactory;

  private final CD4AnalysisParser parser;
  
  private CDMethodFactory() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDMethodFactory getInstance() {
    if (cdMethodFactory == null) {
      cdMethodFactory = new CDMethodFactory();
    }
    return cdMethodFactory;
  }

  public ASTCDMethod createMethodByDefinition(final String signature) {
    Optional<ASTCDMethod> method = Optional.empty();
    try {
      method = parser.parse_StringCDMethod(signature);
    } catch (IOException e) {
      Log.error("Could not create CDMethod with signature '" + signature + "'.", e);
    }
    if (!method.isPresent()) {
      Log.error("Could not create CDMethod with signature '" + signature + "'.");
    }
    return method.get();
  }

  public ASTCDMethod createPublicVoidMethod(final String name) {
    return createPublicVoidMethodBuilder(name).build();
  }

  public ASTCDMethod createPublicVoidMethod(final String name, final ASTCDAttribute... parameters) {
    List<ASTCDParameter> parameterList = CDParameterFactory.getInstance().createParameters(parameters);
    return createPublicVoidMethodBuilder(name)
        .setCDParameterList(parameterList)
        .build();
  }

  public ASTCDMethod createPublicVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createPublicVoidMethodBuilder(name)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name) {
    return createPublicMethodBuilder(returnType, name).build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name, final ASTCDAttribute... parameters) {
    List<ASTCDParameter> parameterList = CDParameterFactory.getInstance().createParameters(parameters);
    return createPublicMethodBuilder(returnType, name)
        .setCDParameterList(parameterList)
        .build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createPublicMethodBuilder(returnType, name)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }





  private ASTCDMethodBuilder createPublicMethodBuilder(final ASTType returnType, final String name) {
    return createPublicMethodBuilder(name)
        .setReturnType(returnType);
  }

  private ASTCDMethodBuilder createPublicVoidMethodBuilder(final String name) {
    return createPublicMethodBuilder(name)
        .setReturnType(CDTypeFactory.getInstance().createVoidType());
  }

  private ASTCDMethodBuilder createPublicMethodBuilder(final String name) {
    return CD4AnalysisMill.cDMethodBuilder()
        .setModifier(ModifierBuilder.builder().Public().build())
        .setName(name);
  }
}
