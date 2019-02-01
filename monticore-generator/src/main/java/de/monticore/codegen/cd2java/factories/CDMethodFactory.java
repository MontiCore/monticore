package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CDMethodFactory {
  
  private static CDMethodFactory cdMethodFactory;

  private final CD4AnalysisParser parser;
  
  private CDMethodFactory() {
    this.parser = new CD4AnalysisParser();
  }

  private static final ASTModifier PUBLIC = ModifierBuilder.builder().Public().build();
  
  private static final ASTModifier PUBLIC_STATIC = ModifierBuilder.builder().Public().Static().build();
  
  private static final ASTModifier PROTECTED = ModifierBuilder.builder().Protected().build();

  private static final ASTModifier PROTECTED_STATIC = ModifierBuilder.builder().Protected().Static().build();
  
  private static final ASTModifier PRIVATE = ModifierBuilder.builder().Private().build();

  private static final ASTModifier PRIVATE_STATIC = ModifierBuilder.builder().Private().Static().build();

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

  /*
  create Methods for all public Methods
   */
  public ASTCDMethod createPublicVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PUBLIC).build();
  }

  public ASTCDMethod createPublicVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PUBLIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPublicVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PUBLIC)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PUBLIC).build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PUBLIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPublicMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PUBLIC)
        .setCDParameterList(parameters)
        .build();
  }
  
  /*
  create Methods for all public static Methods
   */
  public ASTCDMethod createPublicStaticVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PUBLIC_STATIC).build();
  }

  public ASTCDMethod createPublicStaticVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PUBLIC_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPublicStaticVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PUBLIC_STATIC)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createPublicStaticMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PUBLIC_STATIC).build();
  }

  public ASTCDMethod createPublicStaticMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PUBLIC_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPublicStaticMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PUBLIC_STATIC)
        .setCDParameterList(parameters)
        .build();
  }

  /*
  create Methods for all protected Methods
   */
  public ASTCDMethod createProtectedVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PROTECTED).build();
  }

  public ASTCDMethod createProtectedVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PROTECTED)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createProtectedVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PROTECTED)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createProtectedMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PROTECTED).build();
  }

  public ASTCDMethod createProtectedMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PROTECTED)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createProtectedMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PROTECTED)
        .setCDParameterList(parameters)
        .build();
  }
  
    /*
  create Methods for all protected static Methods
   */
  public ASTCDMethod createProtectedStaticVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PROTECTED_STATIC).build();
  }

  public ASTCDMethod createProtectedStaticVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PROTECTED_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createProtectedStaticVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PROTECTED_STATIC)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createProtectedStaticMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PROTECTED_STATIC).build();
  }

  public ASTCDMethod createProtectedStaticMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PROTECTED_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createProtectedStaticMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PROTECTED_STATIC)
        .setCDParameterList(parameters)
        .build();
  }
  
    /*
  create Methods for all private Methods
   */
  public ASTCDMethod createPrivateVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PRIVATE).build();
  }

  public ASTCDMethod createPrivateVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PRIVATE)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPrivateVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PRIVATE)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createPrivateMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PRIVATE).build();
  }

  public ASTCDMethod createPrivateMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PRIVATE)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPrivateMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PRIVATE)
        .setCDParameterList(parameters)
        .build();
  }
  
    /*
  create Methods for all private static Methods
   */
  public ASTCDMethod createPrivateStaticVoidMethod(final String name) {
    return createVoidMethodBuilder(name, PRIVATE_STATIC).build();
  }

  public ASTCDMethod createPrivateStaticVoidMethod(final String name, final ASTCDParameter... parameters) {
    return createVoidMethodBuilder(name, PRIVATE_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPrivateStaticVoidMethod(final String name, final List<ASTCDParameter> parameters) {
    return createVoidMethodBuilder(name, PRIVATE_STATIC)
        .setCDParameterList(parameters)
        .build();
  }

  public ASTCDMethod createPrivateStaticMethod(final ASTType returnType, final String name) {
    return createMethodBuilder(returnType, name, PRIVATE_STATIC).build();
  }

  public ASTCDMethod createPrivateStaticMethod(final ASTType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethodBuilder(returnType, name, PRIVATE_STATIC)
        .setCDParameterList(Arrays.asList(parameters))
        .build();
  }

  public ASTCDMethod createPrivateStaticMethod(final ASTType returnType, final String name, final List<ASTCDParameter> parameters) {
    return createMethodBuilder(returnType, name, PRIVATE_STATIC)
        .setCDParameterList(parameters)
        .build();
  }

  
  /*
  builder for methods with the special modifier
   */
  private ASTCDMethodBuilder createMethodBuilder(final ASTType returnType,final String name, final ASTModifier modifier) {
    return createMethodBuilder(name, modifier)
        .setReturnType(returnType);
  }

  private ASTCDMethodBuilder createVoidMethodBuilder(final String name, final ASTModifier modifier) {
    return createMethodBuilder(name, modifier)
        .setReturnType(CDTypeFactory.getInstance().createVoidType());
  }

  private ASTCDMethodBuilder createMethodBuilder(final String name, final ASTModifier modifier) {
    return CD4AnalysisMill.cDMethodBuilder()
        .setModifier(modifier)
        .setName(name);
  }
}
