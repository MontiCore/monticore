/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class NodeFactoryService extends AbstractService<NodeFactoryService> {

  public NodeFactoryService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public NodeFactoryService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected NodeFactoryService createService(CDDefinitionSymbol cdSymbol) {
    return createNodeFactoryService(cdSymbol);
  }

  public static NodeFactoryService createNodeFactoryService(CDDefinitionSymbol cdSymbol) {
    return new NodeFactoryService(cdSymbol);
  }

  public String getNodeFactorySimpleTypeName() {
    return getCDName() + NodeFactoryConstants.NODE_FACTORY_SUFFIX;
  }

  public String getNodeFactoryFullTypeName() {
    return String.join(".", getPackage(), getNodeFactorySimpleTypeName());
  }

  public ASTMCType getNodeFactoryType() {
    return getCDTypeFactory().createQualifiedType(getNodeFactoryFullTypeName());
  }

  public String getCreateInvocation(ASTCDClass clazz) {
    return "return " + getPackage() + "." + getNodeFactorySimpleTypeName() + "." + NodeFactoryConstants.CREATE_METHOD + clazz.getName() + "();\n";
  }
}
