package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class VisitorService extends AbstractService {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }
}
