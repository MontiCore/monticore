/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

public class CDTraverserDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final TraverserInterfaceDecorator iTraverserDecorator;
  protected final TraverserClassDecorator traverserDecorator;
  protected final Visitor2Decorator visitor2Decorator;
  protected final HandlerDecorator handlerDecorator;
  protected final IterablePath handCodedPath;
  protected final VisitorService visitorService;

  public CDTraverserDecorator(final GlobalExtensionManagement glex,
                            final IterablePath handCodedPath,
                            final VisitorService visitorService,
                            final TraverserInterfaceDecorator iTraverserDecorator,
                            final TraverserClassDecorator traverserDecorator,
                            final Visitor2Decorator visitor2Decorator,
                            final HandlerDecorator handlerDecorator) {
    super(glex);
    this.handCodedPath = handCodedPath;
    this.visitorService = visitorService;
    this.iTraverserDecorator = iTraverserDecorator;
    this.traverserDecorator = traverserDecorator;
    this.visitor2Decorator = visitor2Decorator;
    this.handlerDecorator = handlerDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> visitorPackage = Lists.newArrayList();
    input.getPackageList().forEach(p -> visitorPackage.add(p.toLowerCase()));
    visitorPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), VISITOR_PACKAGE));

    // check for TOP classes
    setIfExistsHandwrittenFile(visitorPackage);

    // decorate cd
    ASTCDInterface traverserInterface = iTraverserDecorator.decorate(input);
    ASTCDClass traverserClass = traverserDecorator.decorate(input);
    ASTCDInterface visitor2Interface = visitor2Decorator.decorate(input);
    ASTCDInterface handlerInterface = handlerDecorator.decorate(input);
    
    // build cd
    ASTCDDefinition astCD = CD4CodeMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDInterface(traverserInterface)
        .addCDClass(traverserClass)
        .addCDInterface(visitor2Interface)
        .addCDInterface(handlerInterface)
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

  /**
   * Informs the traverser interface generator of potential TOP classes.
   * 
   * @param visitorPackage The package under observation
   */
  protected void setIfExistsHandwrittenFile(List<String> visitorPackage) {
    boolean isVisitorHandCoded = existsHandwrittenClass(handCodedPath,
        constructQualifiedName(visitorPackage, visitorService.getTraverserInterfaceSimpleName()));
    iTraverserDecorator.setTop(isVisitorHandCoded);
  }
}
