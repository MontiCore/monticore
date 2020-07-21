/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.Function;

public class MC2CDSymbolTranslation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
        CD4AnalysisNodeFactory.createASTCDCompilationUnit(), null);

    return new CDSymbolTranslation()
        .andThen(Link::target)
        .apply(rootLink);
  }
}
