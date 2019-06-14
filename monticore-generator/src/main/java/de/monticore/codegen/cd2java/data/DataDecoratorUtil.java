package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractDecorator;

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

  public List<ASTCDMethod> createEqualsMethods() {
    ASTCDParameter objectParameter = getCDParameterFacade().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "forceSameOrder");

    return new ArrayList<>(Arrays.asList(
        createDeepEqualsMethod(objectParameter),
        createDeepEqualsWithOrderMethod(objectParameter, forceSameOrderParameter),
        createDeepEqualsWithComments(objectParameter),
        createDeepEqualsWithCommentsWithOrder(objectParameter, forceSameOrderParameter),
        createEqualAttributesMethod(objectParameter),
        createEqualsWithComments(objectParameter)));
  }

  public ASTCDMethod createDeepEqualsMethod(ASTCDParameter objectParameter) {
    // public  boolean deepEquals(Object o)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter);
  }

  public ASTCDMethod createDeepEqualsWithOrderMethod(ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEquals(Object o,boolean forceSameOrder)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), DEEP_EQUALS_METHOD, objectParameter, forceSameOrderParameter);
  }

  public ASTCDMethod createDeepEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean deepEqualsWithComments(Object o)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
  }

  public ASTCDMethod createDeepEqualsWithCommentsWithOrder(ASTCDParameter objectParameter, ASTCDParameter forceSameOrderParameter) {
    // public  boolean deepEqualsWithComments(Object o,boolean forceSameOrder)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), DEEP_EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter, forceSameOrderParameter);
  }

  public ASTCDMethod createEqualAttributesMethod(ASTCDParameter objectParameter) {
    // public  boolean equalAttributes(Object o)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), EQUAL_ATTRIBUTES_METHOD, objectParameter);
  }

  public ASTCDMethod createEqualsWithComments(ASTCDParameter objectParameter) {
    // public  boolean equalsWithComments(Object o)
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), EQUALS_METHOD + WITH_COMMENTS_SUFFIX, objectParameter);
  }

  public ASTCDMethod createDeepClone(ASTCDType cdType) {
    // deep clone without parameters
    ASTMCType type = getCDTypeFacade().createSimpleReferenceType(getSimpleName(cdType));
    return getCDMethodFacade().createMethod(PUBLIC, type, DEEP_CLONE_METHOD);
  }

  protected String getSimpleName(ASTCDType astcdType) {
    //remove TOP from Classname if hwc exists
    String simpleClassName = astcdType.getName();
    if (simpleClassName.endsWith("TOP")) {
      simpleClassName = simpleClassName.substring(0, simpleClassName.lastIndexOf("TOP"));
    }
    return simpleClassName;
  }
  
}
