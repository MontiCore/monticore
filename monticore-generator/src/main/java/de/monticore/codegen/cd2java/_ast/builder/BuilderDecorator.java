/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.*;

/**
 * simple and abstract BuilderDecorator, can be used for special builder generations
 * for a special generation use this class as basis and add additional features
 */
public class BuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final AccessorDecorator accessorDecorator;

  protected final AbstractService service;

  /**
   * additional flag that can be disabled to not insert a template for the build method
   * was inserted, because special implementations often only want to change the build method implementation
   */
  private boolean printBuildMethodTemplate = true;

  public BuilderDecorator(final GlobalExtensionManagement glex,
                          final AccessorDecorator accessorDecorator,
                          final AbstractService service) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.service = service;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) throws DecorateException {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTMCType domainType = this.getMCTypeFacade().createQualifiedType(domainClass.getName());
    ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(builderClassName);

    // make the builder abstract for a abstract AST class
    CDModifier modifier = PUBLIC;
    if (domainClass.isPresentModifier() && domainClass.getModifier().isAbstract()) {
      modifier = PUBLIC_ABSTRACT;
    }

    ASTCDAttribute realThisAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED, builderType, REAL_BUILDER);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .filter(a -> !service.isInheritedAttribute(a))
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !getDecorationHelper().isListType(a.printType()))
        .filter(a -> !getDecorationHelper().isOptional(a.printType()))
        .filter(a -> !(a.getMCType() instanceof ASTMCPrimitiveType))
        .filter(a -> !service.isInheritedAttribute(a))
        .collect(Collectors.toList());

    List<ASTCDAttribute> inheritedAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .filter(service::isInherited)
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, builderClassName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + builderClassName + ") this;"));

    ASTCDMethod buildMethod = this.getCDMethodFacade().createMethod(modifier, domainType, BUILD_METHOD);
    if (isPrintBuildMethodTemplate()) {
      this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("_ast.builder.BuildMethod", domainClass, mandatoryAttributes));
    }

    ASTCDMethod isValidMethod = this.getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), IS_VALID);
    this.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("_ast.builder.IsValidMethod", mandatoryAttributes));

    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    BuilderMutatorMethodDecorator mutatorDecorator = new BuilderMutatorMethodDecorator(glex, builderType);
    List<ASTCDMethod> mutatorMethods = builderAttributes.stream()
        .map(mutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    List<ASTCDMethod> inheritedMutatorMethods = inheritedAttributes.stream()
        .map(mutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    builderAttributes.forEach(this::addAttributeDefaultValues);

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier.build())
        .setName(builderClassName)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .addAllCDMethods(inheritedMutatorMethods)
        .build();
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (getDecorationHelper().isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (getDecorationHelper().isOptional(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  public boolean isPrintBuildMethodTemplate() {
    return printBuildMethodTemplate;
  }

  public void setPrintBuildMethodTemplate(boolean printBuildMethodTemplate) {
    this.printBuildMethodTemplate = printBuildMethodTemplate;
  }
}
