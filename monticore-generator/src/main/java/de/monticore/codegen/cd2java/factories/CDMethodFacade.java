package de.monticore.codegen.cd2java.factories;

import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CDMethodFacade {
  
  private static CDMethodFacade cdMethodFacade;

  private final CDTypeFacade cdTypeFacade;

  private final CD4AnalysisParser parser;
  
  private CDMethodFacade() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.parser = new CD4AnalysisParser();
  }

  public static CDMethodFacade getInstance() {
    if (cdMethodFacade == null) {
      cdMethodFacade = new CDMethodFacade();
    }
    return cdMethodFacade;
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
    return createMethod(modifier, this.cdTypeFacade.createVoidType(), name);
  }
  
  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, this.cdTypeFacade.createVoidType(), name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return createMethod(modifier, this.cdTypeFacade.createVoidType(), name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name) {
    return createMethod(modifier, returnType, name, Collections.emptyList());
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name, final List<ASTCDParameter> parameters) {
    return CD4AnalysisMill.cDMethodBuilder()
        .setModifier(modifier)
        .setMCReturnType(returnType)
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

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTMCReturnType returnType, final String name) {
    return createMethod(modifier.build(), returnType, name);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTMCReturnType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier.build(), returnType, name, parameters);
  }

  public ASTCDMethod createMethod(final CDModifier modifier, final ASTMCReturnType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethod(modifier.build(), returnType, name, parameters);
  }
}
