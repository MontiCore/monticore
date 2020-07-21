/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;

/**
 * helper class that helps with the decoration of nodeFactories
 */
public class NodeFactoryService extends AbstractService<NodeFactoryService> {

  public NodeFactoryService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public NodeFactoryService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_ast' package for NodeFactory generation
   */

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

  /**
   node factory class names e.g. AutomataNodeFactory
   */
  public String getNodeFactorySimpleTypeName() {
    return getNodeFactorySimpleTypeName(getCDSymbol());
  }

  public String getNodeFactorySimpleTypeName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + NodeFactoryConstants.NODE_FACTORY_SUFFIX;
  }

  public String getNodeFactoryFullTypeName() {
    return getNodeFactoryFullTypeName(getCDSymbol());
  }

  public String getNodeFactoryFullTypeName(CDDefinitionSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getNodeFactorySimpleTypeName(cdSymbol));
  }
}
