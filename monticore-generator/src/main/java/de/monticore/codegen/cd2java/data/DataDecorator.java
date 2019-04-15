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
    clazz.addAllCDMethods(getAllDataMethods(clazz));
    clazz.addCDMethod(createDeepCloneWithParam(clazz));
    clazz.addAllCDMethods(clazz.getCDAttributeList().stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList()));

    return clazz;
  }

  protected ASTCDConstructor createDefaultConstructor(ASTCDClass clazz) {
    return this.getCDConstructorFacade().createDefaultConstructor(PROTECTED, clazz);
  }

  protected ASTCDConstructor createFullConstructor(ASTCDClass clazz) {
    List<ASTCDAttribute> allAttributesInHierarchy = getAllAttributesInHierarchy(clazz);
    ASTCDConstructor fullConstructor = this.getCDConstructorFacade().createConstructor(PROTECTED, clazz.getName(), getCDParameterFacade().createParameters(allAttributesInHierarchy));
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

  protected List<ASTCDMethod> getAllDataMethods(ASTCDClass clazz) {
    List<ASTCDMethod> methods = new ArrayList<>();
    ASTCDParameter objectParameter = getCDParameterFacade().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "forceSameOrder");

    ASTCDMethod deepEqualsMethod = dataDecoratorUtil.createDeepEqualsMethod(objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsMethod, new StringHookPoint("     return deepEquals(o, true);"));
    methods.add(deepEqualsMethod);

    ASTCDMethod deepEqualsWithOrder = dataDecoratorUtil.createDeepEqualsWithOrderMethod(objectParameter, forceSameOrderParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("data.DeepEqualsWithOrder", clazz));
    methods.add(deepEqualsWithOrder);

    ASTCDMethod deepEqualsWithComments = dataDecoratorUtil.createDeepEqualsWithComments(objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));
    methods.add(deepEqualsWithComments);

    ASTCDMethod deepEqualsWithCommentsWithOrder = dataDecoratorUtil.createDeepEqualsWithCommentsWithOrder(objectParameter, forceSameOrderParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("data.DeepEqualsWithComments", clazz));
    methods.add(deepEqualsWithCommentsWithOrder);

    ASTCDMethod equalAttributes = dataDecoratorUtil.createEqualAttributesMethod(objectParameter);
    this.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("data.EqualAttributes", clazz));
    methods.add(equalAttributes);

    ASTCDMethod equalsWithComments = dataDecoratorUtil.createEqualsWithComments(objectParameter);
    this.replaceTemplate(EMPTY_BODY, equalsWithComments, new TemplateHookPoint("data.EqualsWithComments", clazz.getName()));
    methods.add(equalsWithComments);

    ASTCDMethod deepClone = dataDecoratorUtil.createDeepClone(clazz);
    this.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
    methods.add(deepClone);
    return methods;
  }


  protected ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz) {
    // deep clone with result parameter
    ASTType classType = this.getCDTypeFacade().createSimpleReferenceType(clazz.getName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(classType, "result");
    ASTCDMethod deepCloneWithParam = this.getCDMethodFacade().createMethod(PUBLIC, parameter.getType(), DEEP_CLONE_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", clazz));
    return deepCloneWithParam;
  }
}
