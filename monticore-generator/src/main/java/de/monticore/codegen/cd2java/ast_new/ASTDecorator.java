package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;


public class ASTDecorator implements Decorator<ASTCDClass, ASTCDClass> {


  private static final String ACCEPT_METHOD = "accept";

  private static final String DEEP_EQUALS_METHOD = "deepEquals";

  private static final String EQUALS_METHOD = "equals";

  private static final String WITH_COMMENTS_SUFFIX = "WithComments";

  private static final String EQUAL_ATTRIBUTES_METHOD = "equalAttributes";

  private static final String DEEP_CLONE_METHOD = "deepClone";

  private static final String CONSTRUCT_METHOD = "_construct";

  private static final String VISITOR_PACKAGE = "._visitor.";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String VISITOR_PREFIX = "visitor";


  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDParameterFactory cdParameterFactory;

  private final CDMethodFactory cdMethodFactory;

  private final ASTCDCompilationUnit compilationUnit;

  public ASTDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass astcdClass) {
    ASTType classType = this.cdTypeFactory.createSimpleReferenceType(astcdClass.getName());
    ASTCDConstructor defaultConstructor = this.cdConstructorFactory.createDefaultConstructor(PROTECTED, astcdClass);
    ASTCDConstructor paramConstructor = this.cdConstructorFactory.createFullConstructor(PROTECTED, astcdClass);

    List<ASTCDMethod> acceptMethods = getAcceptMethods(astcdClass);

    List<ASTCDMethod> equalsMethods = getEqualsMethods();

    List<ASTCDMethod> deepCloneMethods = getCloneMethods(classType);

    ASTCDMethod constructMethod = this.cdMethodFactory.createMethod(PROTECTED, classType, CONSTRUCT_METHOD);

    List<ASTCDMethod> attributeMethods = getAllAttributeMethods(astcdClass.getCDAttributeList());


    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC)
        .setName(astcdClass.getName())
        .addAllCDAttributes(astcdClass.getCDAttributeList())
        .addCDConstructor(defaultConstructor)
        .addCDConstructor(paramConstructor)
        .addAllCDMethods(acceptMethods)
        .addAllCDMethods(equalsMethods)
        .addAllCDMethods(deepCloneMethods)
        .addCDMethod(constructMethod)
        .addAllCDMethods(attributeMethods)
        .build();
  }

  private List<ASTCDMethod> getAcceptMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    String simpleClassName = (astcdClass.getName().contains("AST")) ? astcdClass.getName().replaceFirst("AST", "") : astcdClass.getName();
    ASTType visitorType = this.cdTypeFactory.createTypeByDefinition(simpleClassName.toLowerCase() + VISITOR_PACKAGE + simpleClassName + VISITOR_SUFFIX);
    ASTCDParameter visitorParameter = this.cdParameterFactory.createParameter(visitorType, VISITOR_PREFIX);
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, visitorParameter));

    //accept methods for super grammar visitors
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      ASTType superVisitorType = this.cdTypeFactory.createTypeByDefinition(superSymbol.getFullName().toLowerCase() + VISITOR_PACKAGE + superSymbol.getName() + VISITOR_SUFFIX);
      ASTCDParameter superVisitorParameter = this.cdParameterFactory.createParameter(superVisitorType, VISITOR_PREFIX);
      methodList.add(this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, superVisitorParameter));
    }
    return methodList;
  }

  private List<ASTCDMethod> getEqualsMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter objectParameter = cdParameterFactory.createParameter(cdTypeFactory.createTypeByDefinition("Object"), "o");
    ASTCDParameter forceSameOrderParameter = cdParameterFactory.createParameter(cdTypeFactory.createBooleanType(), "forceSameOrder");
    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter));

    // public  boolean deepEquals(Object o)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter));

    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter));

    // public  boolean deepEqualsWithComments(Object o)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter));

    // public  boolean equalAttributes(Object o)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter));

    // public  boolean equalsWithComments(Object o)
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter));

    return methodList;
  }

  private List<ASTCDMethod> getCloneMethods(ASTType classType) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    ASTCDParameter classParameter = cdParameterFactory.createParameter(classType, "result");
    //deep clone with result parameter
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, classType, DEEP_CLONE_METHOD, classParameter));

    //deep clone without parameters
    methodList.add(this.cdMethodFactory.createMethod(PUBLIC, classType, DEEP_CLONE_METHOD));

    return methodList;

  }

  private List<ASTCDMethod> getAllAttributeMethods(List<ASTCDAttribute> attributeList){
    List<ASTCDMethod> attributeMethods = new ArrayList<>();
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    for(ASTCDAttribute attribute : attributeList){
      attributeMethods.addAll(methodDecorator.decorate(attribute));
    }
    return attributeMethods;
  }
}
