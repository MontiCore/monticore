package de.monticore.codegen.cd2java.factory;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public class NodeFactoryService extends AbstractService<NodeFactoryService> {

  public NodeFactoryService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public NodeFactoryService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  protected String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected NodeFactoryService createService(CDSymbol cdSymbol) {
    return createNodeFactoryService(cdSymbol);
  }

  public static NodeFactoryService createNodeFactoryService(CDSymbol cdSymbol) {
    return new NodeFactoryService(cdSymbol);
  }

  public String getNodeFactorySimpleTypeName() {
    return getCDName() + NodeFactoryConstants.NODE_FACTORY_SUFFIX;
  }

  public String getNodeFactoryFullTypeName() {
    return String.join(".", getPackage(), getNodeFactorySimpleTypeName());
  }

  public ASTType getNodeFactoryType() {
    return getCDTypeFactory().createSimpleReferenceType(getNodeFactoryFullTypeName());
  }

  public String getCreateInvocation(ASTCDClass clazz) {
    return "return " + getNodeFactorySimpleTypeName() + "." + NodeFactoryConstants.CREATE_METHOD + clazz.getName() + "();\n";
  }
}
