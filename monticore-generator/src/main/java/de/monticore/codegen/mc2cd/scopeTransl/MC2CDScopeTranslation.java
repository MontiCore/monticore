package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.Function;

public class MC2CDScopeTranslation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {

  private GlobalExtensionManagement glex;

  public MC2CDScopeTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
        CD4AnalysisNodeFactory.createASTCDCompilationUnit(), null);

    return new CDScopeTranslation(glex)
        .andThen(Link::target)
        .apply(rootLink);
  }

}
