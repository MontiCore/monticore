package de.monticore.codegen.cd2java.language_interface;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class LanguageInteraceDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String VISITOR_PACKAGE = "._visitor.";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String AST_PREFIX = "AST";

  private static final String NODE_SUFFIX = "Node";

  private static final String ASTNODE = "de.monticore.ast.ASTNode";

  private static final String ACCEPT_METHOD = "accept";

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit compilationUnit) {
    String interfaceName = AST_PREFIX + compilationUnit.getCDDefinition().getName() + NODE_SUFFIX;
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(interfaceName)
        .addInterface(0, this.getCDTypeFactory().createReferenceTypeByDefinition(ASTNODE))
        .addCDMethod(getAcceptMethod(compilationUnit))
        .build();
  }

  protected ASTCDMethod getAcceptMethod(ASTCDCompilationUnit compilationUnit){
    String visitorPackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + VISITOR_PACKAGE).toLowerCase();
    ASTType visitorType = this.getCDTypeFactory().createSimpleReferenceType(visitorPackage + compilationUnit.getCDDefinition().getName() + VISITOR_SUFFIX);
    ASTCDParameter visitorParameter = this.getCDParameterFactory().createParameter(visitorType, "visitor");
    //need to add stereotype for abstract method that has no implementation
    ASTModifier modifier = CD4AnalysisMill.modifierBuilder().setAbstract(true).setPublic(true).build();
    return getCDMethodFactory().createMethod(modifier, ACCEPT_METHOD, visitorParameter);
  }
}
