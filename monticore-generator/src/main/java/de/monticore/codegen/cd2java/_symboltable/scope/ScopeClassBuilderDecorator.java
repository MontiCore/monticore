/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;

public class ScopeClassBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.scope.";

  public ScopeClassBuilderDecorator(final GlobalExtensionManagement glex,
                                    final SymbolTableService symbolTableService,
                                    final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass scopeClass) {
    ASTCDClass decoratedScopeClass = scopeClass.deepClone();
    String scopeBuilderName = scopeClass.getName() + BUILDER_SUFFIX;

    decoratedScopeClass.getCDMethodList().clear();

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass scopeBuilder = builderDecorator.decorate(decoratedScopeClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    scopeBuilder.setName(scopeBuilderName);

    // new build method template
    Optional<ASTCDMethod> buildMethod = scopeBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    if (buildMethod.isPresent()) {
      this.replaceTemplate(EMPTY_BODY, buildMethod.get(),
          new TemplateHookPoint(TEMPLATE_PATH + "BuildScope", scopeClass.getName()));
      buildMethod.get().setMCReturnType(MCBasicTypesMill.mCReturnTypeBuilder()
          .setMCType(getMCTypeFacade().createQualifiedType("I"+ buildMethod.get().printReturnType()))
          .build());
    }

    // add '= true' template to exportingSymbols attribute
    Optional<ASTCDAttribute> exportingSymbolsAttribute = scopeBuilder.getCDAttributeList()
        .stream()
        .filter(a -> "exportingSymbols".equals(a.getName()))
        .findFirst();
    exportingSymbolsAttribute.ifPresent(b -> this.replaceTemplate(VALUE, b,
        new StringHookPoint("= true")));

    return scopeBuilder;
  }
}
