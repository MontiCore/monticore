package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
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
import static de.monticore.codegen.cd2java.builder.BuilderDecoratorUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class BuilderDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDMethodFactory cdMethodFactory;

  public BuilderDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) throws DecorateException {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTType domainType = this.cdTypeFactory.createSimpleReferenceType(domainClass.getName());
    ASTType builderType = this.cdTypeFactory.createSimpleReferenceType(builderClassName);


    CDModifier modifier = PUBLIC;
    if (domainClass.isPresentModifier() && domainClass.getModifier().isAbstract()) {
      modifier = PUBLIC_ABSTRACT;
    }

    ASTCDAttribute realThisAttribute = this.cdAttributeFactory.createAttribute(PROTECTED, builderType, REAL_THIS);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !GeneratorHelper.isListType(a.printType()))
        .filter(a -> !GeneratorHelper.isOptional(a))
        .filter(a -> !GeneratorHelper.isPrimitive(a.getType()))
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.cdConstructorFactory.createConstructor(PROTECTED, builderClassName);
    this.glex.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this."  + REAL_THIS + " = (" + builderClassName + ") this;"));

    ASTCDMethod buildMethod = this.cdMethodFactory.createMethod(modifier, domainType, BUILD_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("builder.BuildMethod", domainClass, mandatoryAttributes));

    ASTCDMethod isValidMethod = this.cdMethodFactory.createMethod(PUBLIC, this.cdTypeFactory.createBooleanType(), IS_VALID);
    this.glex.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("builder.IsValidMethod", mandatoryAttributes));


    AccessorDecorator accessorDecorator = new AccessorDecorator(this.glex);
    MutatorDecorator mutatorDecorator = new MutatorDecorator(this.glex);

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
        this.glex.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("builder.MethodDelegate", attribute.getName(), methodName, parameterCall));
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
