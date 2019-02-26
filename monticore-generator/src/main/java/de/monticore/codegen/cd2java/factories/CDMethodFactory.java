package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDTypeFactory.VOID_TYPE;

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
    Optional<ASTCDMethod> method;
    try {
      method = parser.parseCDMethod(new StringReader(signature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature, e);
    }

    if (!method.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature);
    }

    return method.get();
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name) {
    return createMethod(modifier, VOID_TYPE, name);
  }
  
  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, VOID_TYPE, name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return createMethod(modifier, VOID_TYPE, name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTReturnType returnType, final String name) {
    return createMethod(modifier, returnType, name, Collections.emptyList());
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTReturnType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTReturnType returnType, final String name, final List<ASTCDParameter> parameters) {
    return CD4AnalysisMill.cDMethodBuilder()
        .setModifier(modifier.deepClone())
        .setReturnType(returnType.deepClone())
        .setName(name)
        .setCDParameterList(parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
        .build();
  }


  public ASTCDMethod createMethod(final CDModifier modifier, final String name) {
    return createMethod(modifier.build(), name);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier.build(), name, parameters);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return createMethod(modifier.build(), name, parameters);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTReturnType returnType, final String name) {
    return createMethod(modifier.build(), returnType, name);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTReturnType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier.build(), returnType, name, parameters);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTReturnType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethod(modifier.build(), returnType, name, parameters);
  }
}
