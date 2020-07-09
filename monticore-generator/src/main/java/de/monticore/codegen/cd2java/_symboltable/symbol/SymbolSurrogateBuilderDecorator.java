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

public class SymbolSurrogateBuilderDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolsurrogate.";

  public SymbolSurrogateBuilderDecorator(final GlobalExtensionManagement glex,
                                         final SymbolTableService symbolTableService,
                                         final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDType input) {
    String symbolSurrogateName = symbolTableService.getSymbolSurrogateSimpleName(input);
    String symbolSurrogateBuilderName = symbolSurrogateName + BUILDER_SUFFIX;
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    ASTModifier modifier = input.isPresentModifier() ?
        symbolTableService.createModifierPublicModifier(input.getModifier()):
        PUBLIC.build();

    List<ASTCDAttribute> builderAttributes = input.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .filter(a -> !a.getModifier().isFinal())
        .collect(Collectors.toList());

    builderAttributes = builderAttributes.stream()
        .filter(a -> !a.getName().equals("name"))
        .filter(a -> !a.getName().equals("enclosingScope"))
        .collect(Collectors.toList());

    List<ASTCDMethod> accessorMethods = builderAttributes.stream()
        .map(accessorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    BuilderMutatorMethodDecorator mutatorDecorator = new BuilderMutatorMethodDecorator(glex, getMCTypeFacade().createQualifiedType(symbolSurrogateBuilderName));
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
        .setName(symbolSurrogateBuilderName)
        .setModifier(modifier)
        .addCDConstructor(createDefaultConstructor(symbolSurrogateBuilderName))
        .addCDAttribute(createRealThisAttribute(symbolSurrogateBuilderName))
        .addAllCDAttributes(builderAttributes)
        .addCDAttribute(nameAttribute)
        .addCDAttribute(enclosingScopeAttribute)
        .addAllCDMethods(accessorMethods)
        .addAllCDMethods(mutatorMethods)
        .addAllCDMethods(nameMutatorMethods)
        .addAllCDMethods(nameAccessorMethods)
        .addAllCDMethods(enclosingScopeAccessorMethods)
        .addAllCDMethods(enclosingScopeMutatorMethods)
        .addCDMethod(createBuildMethod(symbolSurrogateName, builderAttributes))
        .build();
  }

  protected ASTCDConstructor createDefaultConstructor(String symbolSurrogateBuilderName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC, symbolSurrogateBuilderName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + symbolSurrogateBuilderName + ") this;"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, NAME_VAR);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeInterface) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, ENCLOSING_SCOPE_VAR);
  }

  protected ASTCDAttribute createRealThisAttribute(String symbolSurrogateBuilderName) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolSurrogateBuilderName, REAL_BUILDER);
  }

  protected ASTCDMethod createBuildMethod(String symbolSurrogate, List<ASTCDAttribute> attributeList) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolSurrogate), "build");
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint(TEMPLATE_PATH + "BuildSymbolSurrogate", symbolSurrogate, attributeList));
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
