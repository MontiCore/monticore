package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDConstructorFactory;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DataDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String DEEP_EQUALS_METHOD = "deepEquals";

  private static final String EQUALS_METHOD = "equals";

  private static final String WITH_COMMENTS_SUFFIX = "WithComments";

  private static final String EQUAL_ATTRIBUTES_METHOD = "equalAttributes";

  private static final String DEEP_CLONE_METHOD = "deepClone";

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDParameterFactory cdParameterFactory;

  private final CDMethodFactory cdMethodFactory;

  public DataDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    ASTCDClass dataClass = clazz.deepClone();
    MethodDecorator methodDecorator = new MethodDecorator(this.glex);

    dataClass.addCDConstructor(createDefaultConstructor(dataClass));
    dataClass.addCDConstructor(createFullConstructor(dataClass));
    dataClass.addAllCDMethods(createEqualsMethods(dataClass));
    dataClass.addAllCDMethods(createCloneMethods(dataClass));
    dataClass.addAllCDMethods(dataClass.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList()));
    
    return dataClass;
  }

  private ASTCDConstructor createDefaultConstructor(ASTCDClass clazz) {
    return this.cdConstructorFactory.createDefaultConstructor(PROTECTED, clazz);
  }

  private ASTCDConstructor createFullConstructor(ASTCDClass clazz) {
    ASTCDConstructor fullConstructor = this.cdConstructorFactory.createFullConstructor(PROTECTED, clazz);
    this.glex.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("data.ConstructorAttributesSetter", clazz));
    return fullConstructor;
  }
  
  private List<ASTCDMethod> createEqualsMethods(ASTCDClass clazz) {
    ASTCDParameter objectParameter = cdParameterFactory.createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = cdParameterFactory.createParameter(cdTypeFactory.createBooleanType(), "forceSameOrder");
    
    return new ArrayList<>(Arrays.asList(
        createDeepEqualsMethod(objectParameter),
        createDeepEqualsWithOrderMethod(clazz, objectParameter, forceSameOrderParameter),
        createDeepEqualsWithComments(objectParameter),
        createDeepEqualsWithCommentsWithOrder(clazz, objectParameter, forceSameOrderParameter),
        createEqualAttributesMethod(clazz, objectParameter),
        createEqualsWithComments(clazz, objectParameter)));
  }

  private ASTCDMethod createDeepEqualsMethod(ASTCDParameter objectParameter) {
    // public  boolean deepEquals(Object o)
    ASTCDMethod deepEquals = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEquals, new StringHookPoint("     return deepEquals(o, true);"));
    return deepEquals;
  }

  private ASTCDMethod createDeepEqualsWithOrderMethod(ASTCDClass clazz, ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithOrder = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("data.DeepEqualsWithOrder", clazz));
    return deepEqualsWithOrder;
  }

  private ASTCDMethod createDeepEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean deepEqualsWithComments(Object o)
    ASTCDMethod deepEqualsWithComments = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));
    return deepEqualsWithComments;
  }
  
  private ASTCDMethod createDeepEqualsWithCommentsWithOrder(ASTCDClass clazz, ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    ASTCDMethod deepEqualsWithCommentsWithOrder = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("data.DeepEqualsWithComments", clazz));
    return deepEqualsWithCommentsWithOrder;
  }
  
  private ASTCDMethod createEqualAttributesMethod(ASTCDClass clazz, ASTCDParameter objectParameter) {
    // public  boolean equalAttributes(Object o)
    ASTCDMethod equalAttributes = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("data.EqualAttributes", clazz));
    return equalAttributes;
  }
  
  private ASTCDMethod createEqualsWithComments(ASTCDClass clazz, ASTCDParameter objectParameter) {
    // public  boolean equalsWithComments(Object o)
    ASTCDMethod equals = this.cdMethodFactory.createMethod(PUBLIC, cdTypeFactory.createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
    this.glex.replaceTemplate(EMPTY_BODY, equals, new TemplateHookPoint("data.EqualsWithComments", clazz.getName()));
    return equals;
  }

  private List<ASTCDMethod> createCloneMethods(ASTCDClass clazz) {
    ASTType classType = this.cdTypeFactory.createSimpleReferenceType(clazz.getName());
    ASTCDParameter classParameter = cdParameterFactory.createParameter(classType, "result");
    
    return new ArrayList<>(Arrays.asList(
       createDeepClone(classType),
       createDeepCloneWithParam(clazz, classParameter)));
  }

  private ASTCDMethod createDeepClone(ASTType classType) {
    // deep clone without parameters
    ASTCDMethod deepClone = this.cdMethodFactory.createMethod(PUBLIC, classType, DEEP_CLONE_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
    return deepClone;
  }
  
  private ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz, ASTCDParameter parameter) {
    // deep clone with result parameter
    ASTCDMethod deepCloneWithParam = this.cdMethodFactory.createMethod(PUBLIC, parameter.getType(), DEEP_CLONE_METHOD, parameter);
    this.glex.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", clazz));
    return deepCloneWithParam;
  }
}
