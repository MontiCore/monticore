/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import de.monticore.ast.ASTNode;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.JavaDoc;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.CD2JavaTemplatesFix.JAVADOC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;

/**
 * creates for a grammar the corresponding ASTXNode interface
 */
public class ASTLanguageInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final ASTService astService;

  protected final VisitorService visitorService;

  public ASTLanguageInterfaceDecorator(GlobalExtensionManagement glex, ASTService astService, VisitorService visitorService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDCompilationUnit compilationUnit) {
    // creates Interfaces like for example ASTAutomataNode
    ASTCDInterface nodeInterface = CD4AnalysisMill.cDInterfaceBuilder()
            .setModifier(PUBLIC.build())
            .setName(astService.getASTBaseInterfaceSimpleName())
            .setCDExtendUsage(CD4AnalysisMill.cDExtendUsageBuilder().addSuperclass(getMCTypeFacade().createQualifiedType(ASTNode.class)).build())
            .addCDMember(getAcceptTraverserMethod())
            .build();

    CD4C.getInstance().addImport(nodeInterface, "de.monticore.ast.ASTNode");
    return nodeInterface;
  }

  protected ASTCDMethod getAcceptTraverserMethod() {
    ASTMCType visitorType = visitorService.getTraverserInterfaceType();
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(visitorType, "visitor");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(JAVADOC, method,
            JavaDoc.of("Entry point for the Visitor pattern.",
                    "Cf. MontiCore handbook chapter 8.").asHP());
    return method;
  }
}
