package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DataDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  protected static final String DEEP_CLONE_METHOD = "deepClone";

  protected final MethodDecorator methodDecorator;

  protected final AbstractService<?> service;

  protected final DataDecoratorUtil dataDecoratorUtil;

  protected String clazzName;

  public DataDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
                       final AbstractService service, final DataDecoratorUtil dataDecoratorUtil) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.service = service;
    this.dataDecoratorUtil = dataDecoratorUtil;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    this.clazzName = clazz.deepClone().getName();
    clazz.addCDConstructor(createDefaultConstructor(clazz));
    if (!clazz.getCDAttributeList().isEmpty()) {
      clazz.addCDConstructor(createFullConstructor(clazz));
    }

    //remove symbol and scope attributes for deepEquals and deepClone methods
    List<ASTCDAttribute> noSymboAttributes = clazz.deepClone().getCDAttributeList().stream()
        .filter(a -> !service.isReferencedSymbolAttribute(a))
        .filter(ASTCDAttributeTOP::isPresentModifier)
        .filter(a -> !service.hasScopeStereotype(a.getModifier()))
        .filter(a -> !service.hasSymbolStereotype(a.getModifier()))
        .collect(Collectors.toList());

    clazz.addAllCDMethods(getAllDataMethods(clazz.deepClone(), noSymboAttributes));
    clazz.addCDMethod(createDeepCloneWithParam(clazz.deepClone(), noSymboAttributes));

    //remove inherited attributes, because this should not be generated again
    List<ASTCDAttribute> ownAttributes = clazz.getCDAttributeList()
        .stream()
        .filter(a -> !service.isInherited(a))
        .collect(Collectors.toList());

    clazz.setCDAttributeList(ownAttributes);
    clazz.getCDAttributeList().forEach(this::addAttributeDefaultValues);

    //remove methods that are already defined by ast rules
    clazz.addAllCDMethods(service.getMethodListWithoutDuplicates(clazz.getCDMethodList(), createGetter(ownAttributes)));
    clazz.addAllCDMethods(service.getMethodListWithoutDuplicates(clazz.getCDMethodList(), createSetter(ownAttributes)));

    return clazz;
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (GeneratorHelper.isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (GeneratorHelper.isOptional(attribute)) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  protected ASTCDConstructor createDefaultConstructor(ASTCDClass clazz) {
    return this.getCDConstructorFacade().createDefaultConstructor(PROTECTED, clazz);
  }

  protected ASTCDConstructor createFullConstructor(ASTCDClass clazz) {
    //remove referenced symbol attributes, because they are only calculated
    List<ASTCDAttribute> attributeList = clazz.deepClone().getCDAttributeList().stream()
        .filter(x -> !service.isReferencedSymbolAttribute(x))
        .collect(Collectors.toList());
    ASTCDConstructor fullConstructor = this.getCDConstructorFacade().createConstructor(PROTECTED, clazz.getName(), getCDParameterFacade().createParameters(attributeList));
    this.replaceTemplate(EMPTY_BODY, fullConstructor, new TemplateHookPoint("data.ConstructorAttributesSetter", attributeList));
    return fullConstructor;
  }

  protected List<ASTCDMethod> getAllDataMethods(ASTCDClass astcdClass, List<ASTCDAttribute> attributeList) {
    String simpleClassName = dataDecoratorUtil.getSimpleName(astcdClass);

    List<ASTCDMethod> methods = new ArrayList<>();
    ASTCDParameter objectParameter = getCDParameterFacade().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "forceSameOrder");

    ASTCDMethod deepEqualsMethod = dataDecoratorUtil.createDeepEqualsMethod(objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsMethod, new StringHookPoint("     return deepEquals(o, true);"));
    methods.add(deepEqualsMethod);

    ASTCDMethod deepEqualsWithOrder = dataDecoratorUtil.createDeepEqualsWithOrderMethod(objectParameter, forceSameOrderParameter);
    if (attributeList.isEmpty()) {
      this.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new StringHookPoint("return o instanceof " + simpleClassName + ";"));
    } else {
      this.replaceTemplate(EMPTY_BODY, deepEqualsWithOrder, new TemplateHookPoint("data.DeepEqualsWithOrder", attributeList, simpleClassName));
    }
    methods.add(deepEqualsWithOrder);

    ASTCDMethod deepEqualsWithComments = dataDecoratorUtil.createDeepEqualsWithComments(objectParameter);
    this.replaceTemplate(EMPTY_BODY, deepEqualsWithComments, new StringHookPoint("     return deepEqualsWithComments(o, true);"));
    methods.add(deepEqualsWithComments);

    ASTCDMethod deepEqualsWithCommentsWithOrder = dataDecoratorUtil.createDeepEqualsWithCommentsWithOrder(objectParameter, forceSameOrderParameter);
    if (attributeList.isEmpty()) {
      this.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new StringHookPoint("return o instanceof " + simpleClassName + ";"));
    } else {
      this.replaceTemplate(EMPTY_BODY, deepEqualsWithCommentsWithOrder, new TemplateHookPoint("data.DeepEqualsWithComments", attributeList, simpleClassName));
    }
    methods.add(deepEqualsWithCommentsWithOrder);

    ASTCDMethod equalAttributes = dataDecoratorUtil.createEqualAttributesMethod(objectParameter);
    this.replaceTemplate(EMPTY_BODY, equalAttributes, new TemplateHookPoint("data.EqualAttributes", astcdClass, simpleClassName));
    methods.add(equalAttributes);

    ASTCDMethod equalsWithComments = dataDecoratorUtil.createEqualsWithComments(objectParameter);
    this.replaceTemplate(EMPTY_BODY, equalsWithComments, new TemplateHookPoint("data.EqualsWithComments", simpleClassName));
    methods.add(equalsWithComments);

    ASTCDMethod deepClone = dataDecoratorUtil.createDeepClone(astcdClass);
    this.replaceTemplate(EMPTY_BODY, deepClone, new StringHookPoint("    return deepClone(_construct());"));
    methods.add(deepClone);
    return methods;
  }


  protected ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz, List<ASTCDAttribute> attributeList) {
    String simpleName = dataDecoratorUtil.getSimpleName(clazz);
    // deep clone with result parameter
    ASTMCType classType = this.getCDTypeFacade().createQualifiedType(simpleName);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(classType, "result");
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(classType).build();
    ASTCDMethod deepCloneWithParam = this.getCDMethodFacade().createMethod(PUBLIC, returnType, DEEP_CLONE_METHOD, parameter);
    List<ASTCDAttribute> noInheritedAttributes = attributeList.stream()
        .filter(a -> !service.isInherited(a))
        .collect(Collectors.toList());
    this.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", noInheritedAttributes));
    return deepCloneWithParam;
  }

  protected List<ASTCDMethod> createGetter(List<ASTCDAttribute> attributeList) {
    return attributeList.stream()
        .map(methodDecorator.getAccessorDecorator()::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  protected List<ASTCDMethod> createSetter(List<ASTCDAttribute> attributeList) {
    return attributeList.stream()
        .map(methodDecorator.getMutatorDecorator()::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}
