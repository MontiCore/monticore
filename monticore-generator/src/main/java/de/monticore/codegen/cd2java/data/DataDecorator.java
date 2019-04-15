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
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DataDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

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
    addImplementation(clazz);
    clazz.addCDMethod(createDeepCloneWithParam(clazz));
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
    List<ASTCDAttribute> allAttributesInHierarchy = getAllAttributesInHierarchy(clazz);
    ASTCDConstructor fullConstructor = this.getCDConstructorFactory().createConstructor(PROTECTED, clazz.getName(), getCDParameterFactory().createParameters(allAttributesInHierarchy));
    this.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("data.ConstructorAttributesSetter", allAttributesInHierarchy));
    return fullConstructor;
  }

  //todo move to constructor factory -> how use service in factory?
  //todo may do this recursive?
  protected List<ASTCDAttribute> getAllAttributesInHierarchy(ASTCDClass clazz) {
    List<ASTCDAttribute> result = new ArrayList<>(clazz.getCDAttributeList());
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
            result.add((ASTCDAttribute) fieldSymbol.getAstNode().orElseThrow(
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
    return result;
  }

  protected void addImplementation(ASTCDClass clazz) {
    ASTCDMethod deepEquals = getMethodBy("deepEquals", 1, clazz);
    this.replaceTemplate(EMPTY_BODY, deepEquals, new StringHookPoint("     return deepEquals(o, true);"));

    ASTCDMethod deepEqualsWithOrder = getMethodBy("deepEquals", 2, clazz);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("data.DeepEqualsWithOrder", clazz));

    ASTCDMethod deepEqualsWithComments = getMethodBy("deepEqualsWithComments", 1, clazz);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));

    ASTCDMethod deepEqualsWithCommentsWithOrder = getMethodBy("deepEqualsWithComments", 2, clazz);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("data.DeepEqualsWithComments", clazz));

    ASTCDMethod equalAttributes = getMethodBy("equalAttributes", 1, clazz);
    this.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("data.EqualAttributes", clazz));

    ASTCDMethod equalsWithComments = getMethodBy("equalsWithComments", 1, clazz);
    this.replaceTemplate(EMPTY_BODY, equalsWithComments, new TemplateHookPoint("data.EqualsWithComments", clazz.getName()));

    ASTCDMethod deepClone = getMethodBy("deepClone", 0, clazz);
    this.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
  }

  //todo make util class for these methods
  public ASTCDMethod getMethodBy(String name, int parameterSize, ASTCDClass astcdClass) {
    return astcdClass.getCDMethodList().stream().filter(m -> name.equals(m.getName()))
        .filter(m -> parameterSize == m.sizeCDParameters()).findFirst().get();
  }

  protected ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz) {
    // deep clone with result parameter
    ASTType classType = this.getCDTypeFactory().createSimpleReferenceType(clazz.getName());
    ASTCDParameter parameter = getCDParameterFactory().createParameter(classType, "result");
    ASTCDMethod deepCloneWithParam = this.getCDMethodFactory().createMethod(PUBLIC, parameter.getType(), DEEP_CLONE_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", clazz));
    return deepCloneWithParam;
  }
}
