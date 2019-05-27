package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class ASTInterfaceDecorator extends AbstractDecorator<ASTCDInterface, ASTCDInterface> {

  private final ASTService astService;

  private final VisitorService visitorService;

  private final ASTSymbolDecorator symbolDecorator;

  private final ASTScopeDecorator scopeDecorator;

  private final MethodDecorator methodDecorator;

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
  public ASTCDInterface decorate(ASTCDInterface input) {
    input.addCDMethod(getAcceptMethod(visitorService.getVisitorType()));
    input.addInterface(getCDTypeFacade().createReferenceTypeByDefinition(AST_INTERFACE));
    input.addInterface(astService.getASTBaseInterface());
    input.clearCDAttributes();

    methodDecorator.disableTemplates();

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(input);
    List<ASTCDMethod> symbolMethods = astService.getMethodsFromAttributeList(symbolAttributes, methodDecorator);
    symbolAttributes.forEach(x-> x.getModifier().setAbstract(true));
    input.addAllCDMethods(symbolMethods);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(input);
    List<ASTCDMethod> scopeMethods = scopeAttributes.stream()
            .map(methodDecorator.getAccessorDecorator()::decorate)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    scopeMethods.forEach(x-> x.getModifier().setAbstract(true));
    input.addAllCDMethods(scopeMethods);
    return input;
  }

  protected void addSymboltableMethods(List<ASTCDAttribute> astcdAttributes, ASTCDInterface input) {
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (!astService.hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED)) {
        List<ASTCDMethod> methods = methodDecorator.decorate(attribute);
        methods.forEach(x -> x.getModifier().setAbstract(true));
        input.addAllCDMethods(methods);
      } else {
        List<ASTCDMethod> methods = methodDecorator.getMutatorDecorator().decorate(attribute);
        methods.forEach(x -> x.getModifier().setAbstract(true));
        input.addAllCDMethods(methods);
      }
    }
  }

  protected ASTCDMethod getAcceptMethod(ASTType visitorType) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(visitorType, "visitor");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, getCDTypeFacade().createVoidType(), ACCEPT_METHOD, parameter);
  }
}
