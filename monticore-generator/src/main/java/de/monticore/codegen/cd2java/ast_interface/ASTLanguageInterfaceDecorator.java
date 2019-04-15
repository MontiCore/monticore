package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class ASTLanguageInterfaceDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private final ASTService astService;

  private final VisitorService visitorService;

  public ASTLanguageInterfaceDecorator(ASTService astService, VisitorService visitorService) {
    this.astService = astService;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit compilationUnit) {
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(astService.getASTBaseInterfaceSimpleName())
        .addInterface(astService.getASTNodeInterfaceType())
        .addCDMethod(getAcceptMethod())
        .build();
  }

  protected ASTCDMethod getAcceptMethod() {
    ASTType visitorType = visitorService.getVisitorType();
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(visitorType, "visitor");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, ACCEPT_METHOD, visitorParameter);
  }
}
