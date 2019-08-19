package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._visitor.visitor_interface.ASTVisitorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;

public class CDVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {
  protected final ASTVisitorDecorator astVisitorDecorator;

  protected final SymbolVisitorDecorator symbolVisitorDecorator;

  protected final ScopeVisitorDecorator scopeVisitorDecorator;

  protected final DelegatorVisitorDecorator delegatorVisitorDecorator;

  protected final InheritanceVisitorDecorator inheritanceVisitorDecorator;

  protected final ParentAwareVisitorDecorator parentAwareVisitorDecorator;

  public CDVisitorDecorator(final GlobalExtensionManagement glex,
                            final ASTVisitorDecorator astVisitorDecorator,
                            final SymbolVisitorDecorator symbolVisitorDecorator,
                            final ScopeVisitorDecorator scopeVisitorDecorator,
                            final DelegatorVisitorDecorator delegatorVisitorDecorator,
                            final InheritanceVisitorDecorator inheritanceVisitorDecorator,
                            final ParentAwareVisitorDecorator parentAwareVisitorDecorator) {
    super(glex);
    this.astVisitorDecorator = astVisitorDecorator;
    this.symbolVisitorDecorator = symbolVisitorDecorator;
    this.scopeVisitorDecorator = scopeVisitorDecorator;
    this.delegatorVisitorDecorator = delegatorVisitorDecorator;
    this.inheritanceVisitorDecorator = inheritanceVisitorDecorator;
    this.parentAwareVisitorDecorator = parentAwareVisitorDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> astPackage = new ArrayList<>(input.getPackageList());
    astPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), VISITOR_PACKAGE));

    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDInterface(astVisitorDecorator.decorate(input))
        .addCDInterface(symbolVisitorDecorator.decorate(input))
        .addCDInterface(scopeVisitorDecorator.decorate(input))
        .addCDClass(delegatorVisitorDecorator.decorate(input))
        .addCDInterface(inheritanceVisitorDecorator.decorate(input))
        .addCDClass(parentAwareVisitorDecorator.decorate(input))
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(astPackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(astPackage));
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(astPackage)
        .setCDDefinition(astCD)
        .build();
  }
}
