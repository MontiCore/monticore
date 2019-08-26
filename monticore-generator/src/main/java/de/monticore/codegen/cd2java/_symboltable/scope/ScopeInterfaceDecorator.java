package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {
  public ScopeInterfaceDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    return null;
  }
}
