/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

public class CDVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final ASTVisitorDecorator astVisitorDecorator;

  protected final DelegatorVisitorDecorator delegatorVisitorDecorator;

  protected final InheritanceVisitorDecorator inheritanceVisitorDecorator;

  protected final ParentAwareVisitorDecorator parentAwareVisitorDecorator;

  protected final IterablePath handCodedPath;

  protected final VisitorService visitorService;

  public CDVisitorDecorator(final GlobalExtensionManagement glex,
                            final IterablePath handCodedPath,
                            final VisitorService visitorService,
                            final ASTVisitorDecorator astVisitorDecorator,
                            final DelegatorVisitorDecorator delegatorVisitorDecorator,
                            final InheritanceVisitorDecorator inheritanceVisitorDecorator,
                            final ParentAwareVisitorDecorator parentAwareVisitorDecorator) {
    super(glex);
    this.handCodedPath = handCodedPath;
    this.visitorService = visitorService;
    this.astVisitorDecorator = astVisitorDecorator;
    this.delegatorVisitorDecorator = delegatorVisitorDecorator;
    this.inheritanceVisitorDecorator = inheritanceVisitorDecorator;
    this.parentAwareVisitorDecorator = parentAwareVisitorDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> visitorPackage = new ArrayList<>(input.getPackageList());
    visitorPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), VISITOR_PACKAGE));

    setIfExistsHandwrittenFile(visitorPackage);

    ASTCDDefinition astCD = CD4CodeMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDInterface(astVisitorDecorator.decorate(input))
        .addCDClass(delegatorVisitorDecorator.decorate(input))
        .addCDInterface(inheritanceVisitorDecorator.decorate(input))
        .addCDClass(parentAwareVisitorDecorator.decorate(input))
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(visitorPackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(visitorPackage));
    }

    return CD4CodeMill.cDCompilationUnitBuilder()
        .setPackageList(visitorPackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected void setIfExistsHandwrittenFile(List<String> visitorPackage) {
    boolean isVisitorHandCoded = existsHandwrittenClass(handCodedPath,
        constructQualifiedName(visitorPackage, visitorService.getVisitorSimpleName()));
    astVisitorDecorator.setTop(isVisitorHandCoded);
  }
}
