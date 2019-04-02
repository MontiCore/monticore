package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DataDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String DEEP_EQUALS_METHOD = "deepEquals";

  private static final String EQUALS_METHOD = "equals";

  private static final String WITH_COMMENTS_SUFFIX = "WithComments";

  private static final String EQUAL_ATTRIBUTES_METHOD = "equalAttributes";

  private static final String DEEP_CLONE_METHOD = "deepClone";

  private final MethodDecorator methodDecorator;

  private final AbstractService service;

  private final DataDecoratorUtil dataDecoratorUtil;

  public DataDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
                       final AbstractService service, final DataDecoratorUtil dataDecoratorUtil) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.service = service;
    this.dataDecoratorUtil = dataDecoratorUtil;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.addCDConstructor(createDefaultConstructor(clazz));
    if (!clazz.getCDAttributeList().isEmpty()) {
      clazz.addCDConstructor(createFullConstructor(clazz));
    }
    List<ASTCDMethod> dataMethods = dataDecoratorUtil.decorate(clazz);
    clazz.addAllCDMethods(dataMethods);
    clazz.addAllCDMethods(createCloneMethods(clazz));
    clazz.addAllCDMethods(clazz.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList()));

    return clazz;
  }

  protected ASTCDConstructor createDefaultConstructor(ASTCDClass clazz) {
    return this.getCDConstructorFactory().createDefaultConstructor(PROTECTED, clazz);
  }

  protected ASTCDConstructor createFullConstructor(ASTCDClass clazz) {
    List<ASTCDAttribute> inheritedAttributes = getInheritedAttributes(clazz);
    ASTCDConstructor fullConstructor = this.getCDConstructorFactory().createFullConstructor(PROTECTED, clazz, inheritedAttributes);
    this.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("data.ConstructorAttributesSetter", clazz, inheritedAttributes));
    return fullConstructor;
  }

  //todo move to constructor factory -> how use service in factory?
  //todo may do this recursive?
  protected List<ASTCDAttribute> getInheritedAttributes(ASTCDClass clazz) {
    List<ASTCDAttribute> inheritedAttributes = new ArrayList<>();
    //also consider super classes of super classes
    while (clazz.isPresentSuperclass()) {
      String superClassName = clazz.printSuperClass();
      String superGrammarName = superClassName.substring(0, superClassName.lastIndexOf("."));
      String superTypeName = superClassName.substring(superClassName.lastIndexOf(".") + 1);
      CDSymbol superGrammar = service.resolveCD(superGrammarName);
      if (superGrammar.getType(superTypeName).isPresent()) {
        CDTypeSymbol cdTypeSymbol = superGrammar.getType(superTypeName).get();
        for (CDFieldSymbol fieldSymbol : cdTypeSymbol.getFields()) {
          if (clazz.getCDAttributeList().stream().noneMatch(a -> a.getName().equals(fieldSymbol.getName()))) {
            inheritedAttributes.add((ASTCDAttribute) fieldSymbol.getAstNode().orElseThrow(
                () -> new DecorateException(DecoratorErrorCode.AST_FOR_CD_FIELD_SYMBOL_NOT_FOUND, fieldSymbol.getName())));
          }
        }
        //go on with next super grammar
        clazz = (ASTCDClass) cdTypeSymbol.getAstNode().orElseThrow(
            () -> new DecorateException(DecoratorErrorCode.AST_FOR_CD_TYPE_SYMBOL_NOT_FOUND, cdTypeSymbol.getName()));
      } else {
        throw new DecorateException(DecoratorErrorCode.CD_TYPE_NOT_FOUND, superTypeName);
      }
    }
    return inheritedAttributes;
  }

  protected ASTCDMethod createDeepEqualsMethod(ASTCDParameter objectParameter) {
    // public  boolean deepEquals(Object o)
    ASTCDMethod deepEquals = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEquals, new StringHookPoint("     return deepEquals(o, true);"));
    return deepEquals;
  }

  protected ASTCDMethod createDeepEqualsWithOrderMethod(ASTCDClass clazz, ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithOrder = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("data.DeepEqualsWithOrder", clazz));
    return deepEqualsWithOrder;
  }

  protected ASTCDMethod createDeepEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean deepEqualsWithComments(Object o)
    ASTCDMethod deepEqualsWithComments = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));
    return deepEqualsWithComments;
  }

  protected ASTCDMethod createDeepEqualsWithCommentsWithOrder(ASTCDClass clazz, ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithCommentsWithOrder = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("data.DeepEqualsWithComments", clazz));
    return deepEqualsWithCommentsWithOrder;
  }

  protected ASTCDMethod createEqualAttributesMethod(ASTCDClass clazz, ASTCDParameter objectParameter) {
    // public  boolean equalAttributes(Object o)
    ASTCDMethod equalAttributes = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter);
    this.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("data.EqualAttributes", clazz));
    return equalAttributes;
  }

  protected ASTCDMethod createEqualsWithComments(ASTCDClass clazz, ASTCDParameter objectParameter) {
    // public  boolean equalsWithComments(Object o)
    ASTCDMethod equals = this.getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.replaceTemplate(EMPTY_BODY, equals, new TemplateHookPoint("data.EqualsWithComments", clazz.getName()));
    return equals;
  }

  protected List<ASTCDMethod> createCloneMethods(ASTCDClass clazz) {
    ASTType classType = this.getCDTypeFactory().createSimpleReferenceType(clazz.getName());
    ASTCDParameter classParameter = getCDParameterFactory().createParameter(classType, "result");

    return new ArrayList<>(Arrays.asList(
        createDeepClone(classType),
        createDeepCloneWithParam(clazz, classParameter)));
  }

  protected ASTCDMethod createDeepClone(ASTType classType) {
    // deep clone without parameters
    ASTCDMethod deepClone = this.getCDMethodFactory().createMethod(PUBLIC, classType, DEEP_CLONE_METHOD);
    this.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
    return deepClone;
  }

  protected ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz, ASTCDParameter parameter) {
    // deep clone with result parameter
    ASTCDMethod deepCloneWithParam = this.getCDMethodFactory().createMethod(PUBLIC, parameter.getType(), DEEP_CLONE_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", clazz));
    return deepCloneWithParam;
  }
}
