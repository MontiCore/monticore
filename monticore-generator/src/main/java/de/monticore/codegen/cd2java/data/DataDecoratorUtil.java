package de.monticore.codegen.cd2java.data;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DataDecoratorUtil extends AbstractDecorator<ASTCDType, List<ASTCDMethod>> {

  private static final String DEEP_EQUALS_METHOD = "deepEquals";

  private static final String EQUALS_METHOD = "equals";

  private static final String WITH_COMMENTS_SUFFIX = "WithComments";

  private static final String EQUAL_ATTRIBUTES_METHOD = "equalAttributes";

  private static final String DEEP_CLONE_METHOD = "deepClone";
  
  @Override
  public List<ASTCDMethod> decorate(ASTCDType cdType) {
    List<ASTCDMethod> result = createEqualsMethods();
    result.add(createDeepClone(cdType));
    return result;
  }

  protected List<ASTCDMethod> createEqualsMethods() {
    ASTCDParameter objectParameter = getCDParameterFactory().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFactory().createParameter(getCDTypeFactory().createBooleanType(), "forceSameOrder");

    return new ArrayList<>(Arrays.asList(
        createDeepEqualsMethod(objectParameter),
        createDeepEqualsWithOrderMethod(objectParameter, forceSameOrderParameter),
        createDeepEqualsWithComments(objectParameter),
        createDeepEqualsWithCommentsWithOrder(objectParameter, forceSameOrderParameter),
        createEqualAttributesMethod(objectParameter),
        createEqualsWithComments(objectParameter)));
  }

  protected ASTCDMethod createDeepEqualsMethod(ASTCDParameter objectParameter) {
    // public  boolean deepEquals(Object o)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter);
  }

  protected ASTCDMethod createDeepEqualsWithOrderMethod(ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter);
  }

  protected ASTCDMethod createDeepEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean deepEqualsWithComments(Object o)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
  }

  protected ASTCDMethod createDeepEqualsWithCommentsWithOrder(ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter);
  }

  protected ASTCDMethod createEqualAttributesMethod(ASTCDParameter objectParameter) {
    // public  boolean equalAttributes(Object o)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter);
  }

  protected ASTCDMethod createEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean equalsWithComments(Object o)
    return getCDMethodFactory().createMethod(PUBLIC, getCDTypeFactory().createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
  }

  protected ASTCDMethod createDeepClone(ASTCDType cdType) {
    // deep clone without parameters
    ASTType type = getCDTypeFactory().createSimpleReferenceType(cdType.getName());
    return getCDMethodFactory().createMethod(PUBLIC, type, DEEP_CLONE_METHOD);
  }


  
}
