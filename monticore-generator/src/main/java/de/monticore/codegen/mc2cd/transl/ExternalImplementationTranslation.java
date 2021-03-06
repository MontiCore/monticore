/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ExternalImplementationTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      String name = link.source().getName();
      Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRuleInSupersOnly(
          link.source(), name);
      if (ruleSymbol.isPresent() && ruleSymbol.get().isIsExternal()) {
        if (!link.target().isPresentCDInterfaceUsage()) {
          link.target().setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
        }
        link.target().getCDInterfaceUsage().addInterface(
            TransformationHelper.createObjectType(TransformationHelper.getGrammarNameAsPackage(
                ruleSymbol.get()) + "AST" + name + "Ext"));
      }
    }

    return rootLink;
  }

}
