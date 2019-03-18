package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class BuilderDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  public static final String BUILD_INIT_TEMPLATE = "builder.BuildInit";

  public static final String BUILDER_SUFFIX = "Builder";

  public static final String REAL_BUILDER = "realBuilder";

  public static final String BUILD_METHOD = "build";

  public static final String IS_VALID = "isValid";

  private final MethodDecorator methodDecorator;

  public BuilderDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) throws DecorateException {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTType domainType = this.getCDTypeFactory().createSimpleReferenceType(domainClass.getName());
    ASTType builderType = this.getCDTypeFactory().createSimpleReferenceType(builderClassName);


    CDModifier modifier = PUBLIC;
    if (domainClass.isPresentModifier() && domainClass.getModifier().isAbstract()) {
      modifier = PUBLIC_ABSTRACT;
    }

    ASTCDAttribute realThisAttribute = this.getCDAttributeFactory().createAttribute(PROTECTED, builderType, REAL_BUILDER);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !GeneratorHelper.isListType(a.printType()))
        .filter(a -> !GeneratorHelper.isOptional(a))
        .filter(a -> !GeneratorHelper.isPrimitive(a.getType()))
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.getCDConstructorFactory().createConstructor(PROTECTED, builderClassName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this."  + REAL_BUILDER + " = (" + builderClassName + ") this;"));

    ASTCDMethod buildMethod = this.getCDMethodFactory().createMethod(modifier, domainType, BUILD_METHOD);
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("builder.BuildMethod", domainClass, mandatoryAttributes));

    ASTCDMethod isValidMethod = this.getCDMethodFactory().createMethod(PUBLIC, this.getCDTypeFactory().createBooleanType(), IS_VALID);
    this.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("builder.IsValidMethod", mandatoryAttributes));


    AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator = this.methodDecorator.getAccessorDecorator();
    AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator = this.methodDecorator.getMutatorDecorator();
    mutatorDecorator.disableTemplates();

    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    List<ASTCDMethod> mutatorMethods = new ArrayList<>();
    for (ASTCDAttribute attribute : builderAttributes) {
      List<ASTCDMethod> methods = mutatorDecorator.decorate(attribute);
      for (ASTCDMethod m : methods) {
        m.setReturnType(builderType);
        String methodName = m.getName().substring(0, m.getName().length() - attribute.getName().length());
        String parameterCall = m.getCDParameterList().stream()
            .map(ASTCDParameter::getName)
            .collect(Collectors.joining(", "));
        this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("builder.MethodDelegate", attribute.getName(), methodName, parameterCall));
      }
      mutatorMethods.addAll(methods);
    }

    return  CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier.build())
        .setName(builderClassName)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .build();
  }
}
