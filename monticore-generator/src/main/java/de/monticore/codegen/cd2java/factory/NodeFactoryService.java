package de.monticore.codegen.cd2java.factory;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class NodeFactoryService extends AbstractService {

  public NodeFactoryService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  public String getNodeFactoryTypeName() {
    return getCDName() + NodeFactoryConstants.NODE_FACTORY_SUFFIX;
  }

  public ASTType getNodeFactoryType() {
    return getCDTypeFactory().createSimpleReferenceType(getNodeFactoryTypeName());
  }

  public String getCreateInvocation(ASTCDClass clazz) {
    return "return " + getNodeFactoryTypeName() + "." + NodeFactoryConstants.CREATE + clazz.getName() + "();\n";
  }
}
