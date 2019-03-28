package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.builder.BuilderDecorator.BUILD_METHOD;
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.TRAVERSE;

public class SymbolVisitorDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String SYMBOL_PACKAGE = "._symboltable.";

  private final VisitorDecorator visitorDecorator;

  public SymbolVisitorDecorator(final GlobalExtensionManagement glex, final VisitorDecorator visitorDecorator) {
    super(glex);
    this.visitorDecorator = visitorDecorator;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = input.deepClone();

    //set classname to correct Name with path
    String astPath = compilationUnit.getCDDefinition().getName().toLowerCase() + SYMBOL_PACKAGE;
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassList()) {
      astcdClass.setName(astPath + astcdClass.getName());
    }

    for (ASTCDInterface astcdInterface : compilationUnit.getCDDefinition().getCDInterfaceList()) {
      astcdInterface.setName(astPath + astcdInterface.getName());
    }

    for (ASTCDEnum astcdEnum : compilationUnit.getCDDefinition().getCDEnumList()) {
      astcdEnum.setName(astPath + astcdEnum.getName());
    }

    ASTCDInterface symbolBuilder = visitorDecorator.decorate(compilationUnit);

    Optional<ASTCDMethod> traverseMethod = symbolBuilder.getCDMethodList().stream().filter(m -> BUILD_METHOD.equals(m.getName())).findFirst();
    traverseMethod.ifPresent(m ->
        this.replaceTemplate(TRAVERSE, m, new TemplateHookPoint(EMPTY_BODY, symbolBuilder)));

    return symbolBuilder;
  }

}
