/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._ast.builder.inheritedmethods.InheritedBuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.REAL_BUILDER;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE_VAR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.NAME_VAR;

public class SymbolLoaderBuilderDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolloader.";

  public SymbolLoaderBuilderDecorator(final GlobalExtensionManagement glex,
                                      final SymbolTableService symbolTableService,
                                      final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDType input) {
    String symbolLoaderName = symbolTableService.getSymbolLoaderSimpleName(input);
    String symbolLoaderBuilderName = symbolLoaderName + BUILDER_SUFFIX;
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    ASTModifier modifier = input.isPresentModifier() ?
        symbolTableService.createModifierPublicModifier(input.getModifier()):
        PUBLIC.build();

    BuilderMutatorMethodDecorator builderMutatorMethodDecorator = new BuilderMutatorMethodDecorator(glex,
        getMCTypeFacade().createQualifiedType(symbolLoaderBuilderName));

    List<ASTCDAttribute> builderAttributes = input.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .collect(Collectors.toList());

    builderAttributes = builderAttributes.stream()
        .filter(a -> !a.getName().equals("name"))
        .filter(a -> !a.getName().equals("enclosingScope"))
        .collect(Collectors.toList());

    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !getDecorationHelper().isListType(a.printType()))
        .filter(a -> !getDecorationHelper().isOptional(a.printType()))
        .filter(a -> !(a.getMCType() instanceof ASTMCPrimitiveType))
        .collect(Collectors.toList());

    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    BuilderMutatorMethodDecorator mutatorDecorator = new BuilderMutatorMethodDecorator(glex, getMCTypeFacade().createQualifiedType(symbolLoaderBuilderName));
    List<ASTCDMethod> mutatorMethods = builderAttributes.stream()
        .map(mutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMutatorMethods = mutatorDecorator.decorate(nameAttribute);
    List<ASTCDMethod> nameAccessorMethods = accessorDecorator.decorate(nameAttribute);

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceFullName);
    List<ASTCDMethod> enclosingScopeMutatorMethods = mutatorDecorator.decorate(enclosingScopeAttribute);
    List<ASTCDMethod> enclosingScopeAccessorMethods = accessorDecorator.decorate(enclosingScopeAttribute);

    builderAttributes.forEach(this::addAttributeDefaultValues);

    return CD4AnalysisMill.cDClassBuilder()
        .setName(symbolLoaderBuilderName)
        .setModifier(modifier)
        .addCDConstructor(createDefaultConstructor(symbolLoaderBuilderName))
        .addCDAttribute(createRealThisAttribute(symbolLoaderBuilderName))
        .addAllCDAttributes(builderAttributes)
        .addCDAttribute(nameAttribute)
        .addCDAttribute(enclosingScopeAttribute)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .addAllCDMethods(nameMutatorMethods)
        .addAllCDMethods(nameAccessorMethods)
        .addAllCDMethods(enclosingScopeAccessorMethods)
        .addAllCDMethods(enclosingScopeMutatorMethods)
        .addCDMethod(createBuildMethod(symbolLoaderName, builderAttributes))
        .build();
  }

  protected ASTCDConstructor createDefaultConstructor(String symbolLoaderBuilderName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC, symbolLoaderBuilderName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + symbolLoaderBuilderName + ") this;"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, NAME_VAR);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeInterface) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, ENCLOSING_SCOPE_VAR);
  }

  protected ASTCDAttribute createRealThisAttribute(String symbolLoaderBuilderName) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolLoaderBuilderName, REAL_BUILDER);
  }

  protected ASTCDMethod createBuildMethod(String symbolLoader, List<ASTCDAttribute> attributeList) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolLoader), "build");
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint(TEMPLATE_PATH + "BuildSymbolLoader", symbolLoader, attributeList));
    return buildMethod;
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (getDecorationHelper().isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (getDecorationHelper().isOptional(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }
}
