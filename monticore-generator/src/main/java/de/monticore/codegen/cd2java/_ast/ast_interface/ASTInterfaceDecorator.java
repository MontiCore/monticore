/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;

/**
 * transformation decorator which adds AST interface specific properties
 */
public class ASTInterfaceDecorator extends AbstractTransformer<ASTCDInterface> {

  protected final ASTService astService;

  protected final VisitorService visitorService;

  protected final ASTSymbolDecorator symbolDecorator;

  protected final ASTScopeDecorator scopeDecorator;

  protected final MethodDecorator methodDecorator;

  public ASTInterfaceDecorator(final GlobalExtensionManagement glex,
                               final ASTService astService,
                               final VisitorService visitorService,
                               final ASTSymbolDecorator symbolDecorator,
                               final ASTScopeDecorator scopeDecorator,
                               final MethodDecorator methodDecorator) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
    this.symbolDecorator = symbolDecorator;
    this.scopeDecorator = scopeDecorator;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDInterface originalInput, ASTCDInterface changedInput) {
    changedInput.addInterface(getMCTypeFacade().createQualifiedType(AST_INTERFACE));
    changedInput.addInterface(astService.getASTBaseInterface());
    changedInput.clearCDAttributes();

    methodDecorator.disableTemplates();

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalInput);
    changedInput.addAllCDMembers(addSymbolMethods(symbolAttributes));

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalInput);
    changedInput.addAllCDMembers(addScopeMethods(scopeAttributes));

    // if a ast has a symbol definition without a name, the getName has to be implemented manually
    // add getName method that is abstract
    if (astService.isSymbolWithoutName(originalInput)) {
      changedInput.addCDMember(astService.createGetNameMethod());
    }
    return changedInput;
  }

  protected List<ASTCDMethod> addScopeMethods(List<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> scopeMethods = new ArrayList<>();
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (!astService.hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED)) {
        List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
        methods.forEach(x -> x.getModifier().setAbstract(true));
        scopeMethods.addAll(methods);
      } else {
        List<ASTCDMethod> methods = methodDecorator.getMutatorDecorator().decorate(attribute);
        methods.forEach(x -> x.getModifier().setAbstract(true));
        scopeMethods.addAll(methods);
      }
    }
    return scopeMethods;
  }


  protected List<ASTCDMethod> addSymbolMethods(List<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> scopeMethods = new ArrayList<>();
    for (ASTCDAttribute attribute : astcdAttributes) {
      List<ASTCDMethod> methods = methodDecorator.getAccessorDecorator().decorate(attribute);
      methods.forEach(x -> x.getModifier().setAbstract(true));
      scopeMethods.addAll(methods);
    }
    return scopeMethods;

  }

}
