package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.REAL_BUILDER;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE_VAR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.NAME_VAR;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolReferenceBuilderDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final AccessorDecorator accessorDecorator;

  public SymbolReferenceBuilderDecorator(final GlobalExtensionManagement glex,
                                         final SymbolTableService symbolTableService,
                                         final AccessorDecorator accessorDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDType input) {
    String symbolReferenceName = symbolTableService.getSymbolReferenceClassSimpleName(input);
    String symbolReferenceBuilderName = symbolReferenceName + BUILDER_SUFFIX;
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();

    BuilderMutatorMethodDecorator builderMutatorMethodDecorator = new BuilderMutatorMethodDecorator(glex,
        getCDTypeFacade().createQualifiedType(symbolReferenceBuilderName));
    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = accessorDecorator.decorate(nameAttribute);
    nameMethods.addAll(builderMutatorMethodDecorator.decorate(nameAttribute));

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceFullName);
    List<ASTCDMethod> enclosingScopeMethods = accessorDecorator.decorate(enclosingScopeAttribute);
    enclosingScopeMethods.addAll(builderMutatorMethodDecorator.decorate(enclosingScopeAttribute));

    return CD4AnalysisMill.cDClassBuilder()
        .setName(symbolReferenceBuilderName)
        .setModifier(PUBLIC.build())
        .addCDConstructor(createDefaultConstructor(symbolReferenceBuilderName))
        .addCDAttribute(createRealThisAttribute(symbolReferenceBuilderName))
        .addCDAttribute(nameAttribute)
        .addCDAttribute(enclosingScopeAttribute)
        .addAllCDMethods(nameMethods)
        .addAllCDMethods(enclosingScopeMethods)
        .addCDMethod(createBuildMethod(symbolReferenceName))
        .build();
  }

  protected ASTCDConstructor createDefaultConstructor(String symbolReferenceBuilderName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PROTECTED, symbolReferenceBuilderName);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + REAL_BUILDER + " = (" + symbolReferenceBuilderName + ") this;"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, NAME_VAR);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeInterface) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, ENCLOSING_SCOPE_VAR);
  }

  protected ASTCDAttribute createRealThisAttribute(String symbolReferenceBuilderName){
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolReferenceBuilderName, REAL_BUILDER);
  }

  protected ASTCDMethod createBuildMethod(String symbolReference) {
    ASTCDMethod buildMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(symbolReference), "build");
    this.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("_symboltable.symbolreferece.Build", symbolReference));
    return buildMethod;
  }
}
