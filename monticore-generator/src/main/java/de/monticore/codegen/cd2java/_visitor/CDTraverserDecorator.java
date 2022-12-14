/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Joiners;

import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;
import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

public class CDTraverserDecorator extends AbstractDecorator {

  protected final TraverserInterfaceDecorator iTraverserDecorator;
  protected final TraverserClassDecorator traverserDecorator;
  protected final Visitor2Decorator visitor2Decorator;
  protected final HandlerDecorator handlerDecorator;
  protected final InheritanceHandlerDecorator inheritanceHandlerDecorator;
  protected final MCPath handCodedPath;
  protected final VisitorService visitorService;

  public CDTraverserDecorator(final GlobalExtensionManagement glex,
                            final MCPath handCodedPath,
                            final VisitorService visitorService,
                            final TraverserInterfaceDecorator iTraverserDecorator,
                            final TraverserClassDecorator traverserDecorator,
                            final Visitor2Decorator visitor2Decorator,
                            final HandlerDecorator handlerDecorator,
                            final InheritanceHandlerDecorator inheritanceHandlerDecorator) {
    super(glex);
    this.handCodedPath = handCodedPath;
    this.visitorService = visitorService;
    this.iTraverserDecorator = iTraverserDecorator;
    this.traverserDecorator = traverserDecorator;
    this.visitor2Decorator = visitor2Decorator;
    this.handlerDecorator = handlerDecorator;
    this.inheritanceHandlerDecorator = inheritanceHandlerDecorator;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VISITOR_PACKAGE);
    // check for TOP classes
    setIfExistsHandwrittenFile(visitorPackage.getName());

    // decorate cd
    ASTCDInterface traverserInterface = iTraverserDecorator.decorate(input);
    ASTCDClass traverserClass = traverserDecorator.decorate(input);
    ASTCDInterface visitor2Interface = visitor2Decorator.decorate(input);
    ASTCDInterface handlerInterface = handlerDecorator.decorate(input);
    ASTCDClass inheritanceClass  = inheritanceHandlerDecorator.decorate(input);
    
    visitorPackage.addCDElement(traverserInterface);
    visitorPackage.addCDElement(traverserClass);
    visitorPackage.addCDElement(visitor2Interface);
    visitorPackage.addCDElement(handlerInterface);
    visitorPackage.addCDElement(inheritanceClass);
  }

  /**
   * Informs the traverser interface generator of potential TOP classes.
   * 
   * @param visitorPackage The package under observation
   */
  protected void setIfExistsHandwrittenFile(String visitorPackage) {
    boolean isVisitorHandCoded = existsHandwrittenClass(handCodedPath,
        Joiners.DOT.join(visitorPackage, visitorService.getTraverserInterfaceSimpleName()));
    iTraverserDecorator.setTop(isVisitorHandCoded);
  }
}
