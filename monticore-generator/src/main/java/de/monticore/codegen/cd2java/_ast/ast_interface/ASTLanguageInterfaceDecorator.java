/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

/**
 * creates for a grammar the corresponding ASTXNode interface
 */
public class ASTLanguageInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final ASTService astService;

  protected final VisitorService visitorService;

  public ASTLanguageInterfaceDecorator(ASTService astService, VisitorService visitorService) {
    this.astService = astService;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDCompilationUnit compilationUnit) {
    // creates Interfaces like for example ASTAutomataNode
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(astService.getASTBaseInterfaceSimpleName())
        .addInterface(getCDTypeFacade().createQualifiedType(ASTNode.class))
        .addCDMethod(getAcceptMethod())
        .build();
  }

  protected ASTCDMethod getAcceptMethod() {
    ASTMCType visitorType = visitorService.getVisitorType();
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(visitorType, "visitor");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, ACCEPT_METHOD, visitorParameter);
  }
}
