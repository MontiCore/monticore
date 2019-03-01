package de.monticore.codegen.cd2java.ast_new;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;


public class ASTDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String AST_PREFIX = "AST";

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

  private final CDConstructorFactory cdConstructorFactory;

  private final CDParameterFactory cdParameterFactory;

  private final CDMethodFactory cdMethodFactory;

  private final ASTCDCompilationUnit compilationUnit;

  public ASTDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass astcdClass) {
    ASTType classType = this.cdTypeFactory.createSimpleReferenceType(astcdClass.getName());
    ASTCDConstructor defaultConstructor = this.cdConstructorFactory.createDefaultConstructor(PROTECTED, astcdClass);
    ASTCDConstructor fullConstructor = this.cdConstructorFactory.createFullConstructor(PROTECTED, astcdClass);
    this.glex.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("ast_new.ConstructorAttributesSetter"));

    String simpleClassName = astcdClass.getName().replaceFirst(AST_PREFIX, "");

    List<ASTCDMethod> acceptMethods = getAcceptMethods(astcdClass, simpleClassName);

    List<ASTCDMethod> equalsMethods = getEqualsMethods(astcdClass);

    List<ASTCDMethod> deepCloneMethods = getCloneMethods(classType, astcdClass);

    ASTCDMethod constructMethod = getConstructMethod(astcdClass, simpleClassName, classType);

    List<ASTCDMethod> attributeMethods = getAllAttributeMethods(astcdClass.getCDAttributeList());

    ASTReferenceType superClass = cdTypeFactory.createSimpleReferenceType(ASTCNode.class);

    ASTReferenceType interfaceNode = cdTypeFactory.createReferenceTypeByDefinition(AST_PREFIX + compilationUnit.getCDDefinition().getName() + "Node");

    ASTCDClass astClassWithoutSymbols= CD4AnalysisMill.cDClassBuilder()
        .setModifier(astcdClass.getModifier())
        .setName(astcdClass.getName())
        .addAllCDAttributes(new ArrayList<>(astcdClass.getCDAttributeList()))
        .addCDConstructor(defaultConstructor)
        .addCDConstructor(fullConstructor)
        .addAllCDMethods(acceptMethods)
        .addAllCDMethods(equalsMethods)
        .addAllCDMethods(deepCloneMethods)
        .addCDMethod(constructMethod)
        .addAllCDMethods(attributeMethods)
        .setSuperclass(superClass)
        .addInterface(interfaceNode)
        .build();

    ASTWithSymbolDecorator symbolDecorator = new ASTWithSymbolDecorator(glex, compilationUnit);
    ASTCDClass astClassWithSymbolAndScope = symbolDecorator.decorate(astClassWithoutSymbols);
    ASTWithReferencedSymbolDecorator referencedSymbolDecorator = new ASTWithReferencedSymbolDecorator(glex, compilationUnit);
    return referencedSymbolDecorator.decorate(astClassWithSymbolAndScope);
  }

  private List<ASTCDMethod> getAcceptMethods(ASTCDClass astcdClass, String simpleClassName) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    ASTType visitorType = this.cdTypeFactory.createTypeByDefinition(getVisitorType(simpleClassName));
    ASTCDParameter visitorParameter = this.cdParameterFactory.createParameter(visitorType, VISITOR_PREFIX);

    ASTCDMethod accept = this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, visitorParameter);
    this.glex.replaceTemplate(EMPTY_BODY, accept, new TemplateHookPoint("ast_new.Accept", astcdClass));
    methodList.add(accept);

    //accept methods for super grammar visitors
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      ASTType superVisitorType = this.cdTypeFactory.createTypeByDefinition(superSymbol.getFullName().toLowerCase() + VISITOR_PACKAGE + superSymbol.getName() + VISITOR_SUFFIX);
      ASTCDParameter superVisitorParameter = this.cdParameterFactory.createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, superVisitorParameter);
      String errorcode = DecorationHelper.getGeneratedErrorCode(astcdClass);
      this.glex.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("ast_new.AcceptSuper", TypesPrinter.printType(visitorType), errorcode, astcdClass.getName(), TypesPrinter.printType(superVisitorType)));
      methodList.add(superAccept);
    }
    return methodList;
  }

  private List<ASTCDMethod> getEqualsMethods(ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter objectParameter = cdParameterFactory.createParameter(cdTypeFactory.createTypeByDefinition("Object"), "o");
    ASTCDParameter forceSameOrderParameter = cdParameterFactory.createParameter(cdTypeFactory.createBooleanType(), "forceSameOrder");

    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithOrder = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("ast_new.DeepEqualsWithOrder", astcdClass));
    methodList.add(deepEqualsWithOrder);

    // public  boolean deepEquals(Object o)
    ASTCDMethod deepEquals = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEquals, new StringHookPoint("     return deepEquals(o, true);"));
    methodList.add(deepEquals);

    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithCommentsWithOrder = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("ast_new.DeepEqualsWithComments", astcdClass));
    methodList.add(deepEqualsWithCommentsWithOrder);

    // public  boolean deepEqualsWithComments(Object o)
    ASTCDMethod deepEqualsWithComments = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));
    methodList.add(deepEqualsWithComments);

    // public  boolean equalAttributes(Object o)
    ASTCDMethod equalAttributes = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("ast_new.EqualAttributes", astcdClass));
    methodList.add(equalAttributes);

    // public  boolean equalsWithComments(Object o)
    ASTCDMethod equals = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, equals, new TemplateHookPoint("ast_new.EqualsWithComments", astcdClass.getName()));
    methodList.add(equals);
    return methodList;
  }

  private List<ASTCDMethod> getCloneMethods(ASTType classType, ASTCDClass astcdClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter classParameter = cdParameterFactory.createParameter(classType, "result");

    // deep clone with result parameter
    ASTCDMethod deepCloneWithParam = this.cdMethodFactory.createMethod(PUBLIC, classType, DEEP_CLONE_METHOD, classParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("ast_new.DeepCloneWithParameters", astcdClass));
    methodList.add(deepCloneWithParam);

    // deep clone without parameters
    ASTCDMethod deepClone = this.cdMethodFactory.createMethod(PUBLIC, classType, DEEP_CLONE_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
    methodList.add(deepClone);

    return methodList;

  }

  private List<ASTCDMethod> getAllAttributeMethods(List<ASTCDAttribute> attributeList) {
    List<ASTCDMethod> attributeMethods = new ArrayList<>();
    // method decorator does template handling
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    for (ASTCDAttribute attribute : attributeList) {
      attributeMethods.addAll(methodDecorator.decorate(attribute));
    }
    return attributeMethods;
  }

  private ASTCDMethod getConstructMethod(ASTCDClass astcdClass, String simpleClassName, ASTType classType) {
    ASTCDMethod constructMethod = this.cdMethodFactory.createMethod(PROTECTED, classType, CONSTRUCT_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(
        "return " + simpleClassName + "NodeFactory.create" + astcdClass.getName() + "();\n"));
    return constructMethod;
  }

  private String getPackage(){
    String qualifiedName = Names.getQualifiedName(compilationUnit.getPackageList(), compilationUnit.getCDDefinition().getName());
  return qualifiedName.toLowerCase();
  }

  private String getVisitorType(String className){
    return getPackage() + VISITOR_PACKAGE + className + VISITOR_SUFFIX;
  }
}
