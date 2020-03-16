package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.transl.*;
import de.monticore.codegen.mc2cd.transl.creation.GrammarToCDDefinition;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class CDSymbolTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new GrammarToCDDefinition()
        .andThen(new CDDefinitionNameTranslation())
        .andThen(new CreateSymbolProds())
        .andThen(new SymbolRulesToCDClassAndCDInterface())
        .andThen(new AttributeInSymbolRuleToCDAttribute())
        .andThen(new PackageTranslation())
        .andThen(new SymbolRuleInheritanceTranslation())
        .andThen(new SymbolRuleMethodTranslation())
        .andThen(new ReferenceTypeTranslation())
        .andThen(new MultiplicityTranslation())
        .andThen(new SymbolAndScopeTranslationForSymbolCD())
        .andThen(new ComponentTranslation())
        .andThen(new StartProdTranslation())
        .andThen(new DeprecatedTranslation())
        .apply(rootLink);
  }
}
