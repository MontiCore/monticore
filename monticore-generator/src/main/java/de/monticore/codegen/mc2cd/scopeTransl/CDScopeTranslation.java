/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.symbolTransl.CDDefinitionNameTranslation;
import de.monticore.codegen.mc2cd.transl.*;
import de.monticore.codegen.mc2cd.transl.creation.GrammarToCDDefinition;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class CDScopeTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new GrammarToCDDefinition()
        .andThen(new CDDefinitionNameTranslation())
        .andThen(new CreateScopeProd())
        .andThen(new ScopeRuleToCDScopeClass())
        .andThen(new AttributeInScopeRuleToCDAttribute())
        .andThen(new PackageTranslation())
        .andThen(new ScopeRuleMethodTranslation())
        .andThen(new ScopeRuleInheritanceTranslation())
        .andThen(new ReferenceTypeTranslation())
        .andThen(new MultiplicityTranslation())
        .andThen(new SymbolAndScopeTranslation())
        .andThen(new DerivedAttributeName())
        .apply(rootLink);
  }
}
