package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractDecorator;
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

public class DataDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String DEEP_EQUALS_METHOD = "deepEquals";

  private static final String EQUALS_METHOD = "equals";

  private static final String WITH_COMMENTS_SUFFIX = "WithComments";

  private static final String EQUAL_ATTRIBUTES_METHOD = "equalAttributes";

  private static final String DEEP_CLONE_METHOD = "deepClone";

  private final MethodDecorator methodDecorator;

  public DataDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.addCDConstructor(createDefaultConstructor(clazz));
    if (!clazz.getCDAttributeList().isEmpty()) {
      clazz.addCDConstructor(createFullConstructor(clazz));
    }
    clazz.addAllCDMethods(createEqualsMethods(clazz));
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
    ASTCDConstructor fullConstructor = this.getCDConstructorFactory().createFullConstructor(PROTECTED, clazz);
    this.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("data.ConstructorAttributesSetter", clazz));
    return fullConstructor;
  }

  protected List<ASTCDMethod> createEqualsMethods(ASTCDClass clazz) {
    ASTCDParameter objectParameter = getCDParameterFactory().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFactory().createParameter(getCDTypeFactory().createBooleanType(), "forceSameOrder");
    
    return new ArrayList<>(Arrays.asList(
        createDeepEqualsMethod(objectParameter),
        createDeepEqualsWithOrderMethod(clazz, objectParameter, forceSameOrderParameter),
        createDeepEqualsWithComments(objectParameter),
        createDeepEqualsWithCommentsWithOrder(clazz, objectParameter, forceSameOrderParameter),
        createEqualAttributesMethod(clazz, objectParameter),
        createEqualsWithComments(clazz, objectParameter)));
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
