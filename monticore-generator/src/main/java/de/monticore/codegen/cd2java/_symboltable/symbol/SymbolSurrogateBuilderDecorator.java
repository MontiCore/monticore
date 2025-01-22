/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.REAL_BUILDER;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE_VAR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.NAME_VAR;

@Deprecated
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
    ASTModifier modifier = symbolTableService.createModifierPublicModifier(input.getModifier());

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
        .addCDMember(createDefaultConstructor(symbolSurrogateBuilderName))
        .addCDMember(createRealThisAttribute(symbolSurrogateBuilderName))
        .addAllCDMembers(builderAttributes)
        .addCDMember(nameAttribute)
        .addCDMember(enclosingScopeAttribute)
        .addAllCDMembers(accessorMethods)
        .addAllCDMembers(mutatorMethods)
        .addAllCDMembers(nameMutatorMethods)
        .addAllCDMembers(nameAccessorMethods)
        .addAllCDMembers(enclosingScopeAccessorMethods)
        .addAllCDMembers(enclosingScopeMutatorMethods)
        .addCDMember(createBuildMethod(symbolSurrogateName, builderAttributes))
        .build();
  }

  protected ASTCDConstructor createDefaultConstructor(String symbolSurrogateBuilderName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolSurrogateBuilderName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + symbolSurrogateBuilderName + ") this;"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED.build(), String.class, NAME_VAR);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeInterface) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeInterface, ENCLOSING_SCOPE_VAR);
  }

  protected ASTCDAttribute createRealThisAttribute(String symbolSurrogateBuilderName) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED.build(), symbolSurrogateBuilderName, REAL_BUILDER);
  }

  protected ASTCDMethod createBuildMethod(String symbolSurrogate, List<ASTCDAttribute> attributeList) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(symbolSurrogate), "build");
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint(TEMPLATE_PATH + "BuildSymbolSurrogate", symbolSurrogate, attributeList));
    return buildMethod;
  }

  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (getDecorationHelper().isListType(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (getDecorationHelper().isOptional(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }
}
