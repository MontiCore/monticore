/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DataDecorator extends AbstractTransformer<ASTCDClass> {

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
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    this.clazzName = originalClass.getName();
    changedClass.addCDMember(createDefaultConstructor(originalClass));
    if (originalClass.isPresentCDExtendUsage()) {
      changedClass.setCDExtendUsage(CD4AnalysisMill.cDExtendUsageBuilder().build());
      changedClass.getCDExtendUsage().setSuperclassList(originalClass.getSuperclassList());
    }
    if (originalClass.isPresentCDInterfaceUsage()) {
      changedClass.setCDInterfaceUsage(CD4AnalysisMill.cDInterfaceUsageBuilder().build());
      changedClass.getCDInterfaceUsage().setInterfaceList(originalClass.getInterfaceList());
    }
    for (ASTCDMethod meth: originalClass.getCDMethodList()) {
      ASTCDMethod newMethod = meth.deepClone();
      changedClass.addCDMember(newMethod);
      if (service.isMethodBodyPresent(newMethod)) {
        glex.replaceTemplate(EMPTY_BODY, newMethod, new StringHookPoint(service.getMethodBody(newMethod)));
      }
    }

    //remove inherited attributes, because these are already defined in superclass
    List<ASTCDAttribute> ownAttributes = originalClass.getCDAttributeList()
            .stream()
            .filter(a -> !service.isInheritedAttribute(a))
            .map(a -> a.deepClone())
            .collect(Collectors.toList());

    changedClass.addAllCDMembers(getAllDataMethods(originalClass, originalClass.getCDAttributeList()));
    // no Inherited attributes only, because inherited once are cloned through super.deepClone()
    changedClass.addCDMember(createDeepCloneWithParam(originalClass, ownAttributes));

    changedClass.setCDAttributeList(ownAttributes);
    changedClass.getCDAttributeList().forEach(this::addAttributeDefaultValues);

    //remove methods that are already defined by ast rules
    changedClass.addAllCDMembers(service.getMethodListWithoutDuplicates(originalClass.getCDMethodList(), createGetter(ownAttributes)));
    changedClass.addAllCDMembers(service.getMethodListWithoutDuplicates(originalClass.getCDMethodList(), createSetter(ownAttributes)));

    return changedClass;
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (getDecorationHelper().isListType(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (getDecorationHelper().isOptional(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  protected ASTCDConstructor createDefaultConstructor(ASTCDClass clazz) {
    return this.getCDConstructorFacade().createDefaultConstructor(PROTECTED.build(), clazz);
  }

  protected List<ASTCDMethod> getAllDataMethods(ASTCDClass astcdClass, List<ASTCDAttribute> attributeList) {
    String simpleClassName = dataDecoratorUtil.getSimpleName(astcdClass);

    List<ASTCDMethod> methods = new ArrayList<>();
    ASTCDParameter objectParameter = getCDParameterFacade().createParameter(Object.class, "o");
    ASTCDParameter forceSameOrderParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), "forceSameOrder");

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


  protected ASTCDMethod createDeepCloneWithParam(ASTCDClass clazz, List<ASTCDAttribute> noInheritedAttributes) {
    String simpleName = dataDecoratorUtil.getSimpleName(clazz);
    // deep clone with result parameter
    ASTMCType classType = this.getMCTypeFacade().createQualifiedType(simpleName);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(classType, "result");
    ASTCDMethod deepCloneWithParam = this.getCDMethodFacade().createMethod(PUBLIC.build(), classType, DEEP_CLONE_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, deepCloneWithParam, new TemplateHookPoint("data.DeepCloneWithParameters", noInheritedAttributes, clazz.isPresentCDExtendUsage()));
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
