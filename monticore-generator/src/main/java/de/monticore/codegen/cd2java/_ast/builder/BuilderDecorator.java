/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._ast.builder.inheritedmethods.InheritedBuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
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
  protected boolean printBuildMethodTemplate = true;

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
    ASTModifier modifier = service.createModifierPublicModifier(domainClass.getModifier()) ;
    if (domainClass.getModifier().isAbstract()) {
      modifier.setAbstract(true);
    }

    ASTCDAttribute realThisAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED.build(), builderType, REAL_BUILDER);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .filter(a -> !service.isInheritedAttribute(a))
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !getDecorationHelper().isListType(CD4CodeMill.prettyPrint(a.getMCType(), false)))
        .filter(a -> !getDecorationHelper().isOptional(CD4CodeMill.prettyPrint(a.getMCType(), false)))
        .filter(a -> !(a.getMCType() instanceof ASTMCPrimitiveType))
        .filter(a -> !service.isInheritedAttribute(a))
        .collect(Collectors.toList());

    // additionally add only setter methods with correct builder return type for inherited attributes
    List<ASTCDAttribute> inheritedAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .filter(service::isInheritedAttribute)
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PUBLIC.build(), builderClassName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + builderClassName + ") this;"));

    ASTCDMethod buildMethod = this.getCDMethodFacade().createMethod(modifier.deepClone(), domainType, BUILD_METHOD);
    if (isPrintBuildMethodTemplate()) {
      this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("_ast.builder.BuildMethod", domainClass, mandatoryAttributes, true));
    }

    ASTCDMethod isValidMethod = this.getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), IS_VALID);
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

    InheritedBuilderMutatorMethodDecorator inheritedMutatorDecorator = new InheritedBuilderMutatorMethodDecorator(glex, builderType);
    List<ASTCDMethod> inheritedMutatorMethods = inheritedAttributes.stream()
        .map(inheritedMutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    builderAttributes.forEach(this::addAttributeDefaultValues);

    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier)
        .setName(builderClassName)
        .addCDMember(realThisAttribute)
        .addAllCDMembers(builderAttributes)
        .addCDMember(constructor)
        .addCDMember(buildMethod)
        .addCDMember(isValidMethod)
        .addAllCDMembers(accessorMethods)
        .addAllCDMembers(mutatorMethods)
        .addAllCDMembers(inheritedMutatorMethods)
        .build();
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (getDecorationHelper().isListType(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (getDecorationHelper().isOptional(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
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
