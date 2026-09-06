/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tests.expressionsandstatements._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.ArrayList;
import java.util.List;

public class ExpressionsAndStatementsScopesGenitor
    extends ExpressionsAndStatementsScopesGenitorTOP {

  @Override
  public IExpressionsAndStatementsArtifactScope createFromAST(
      ASTBehaviorInput node
  ) {
    IExpressionsAndStatementsArtifactScope result = super.createFromAST(node);
    result.setName("TestA");
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStmt : node.getMCImportStatementList()) {
      imports.add(
          new ImportStatement(importStmt.getQName(), importStmt.isStar())
      );
    }
    result.setImportsList(imports);
    return result;
  }

}
