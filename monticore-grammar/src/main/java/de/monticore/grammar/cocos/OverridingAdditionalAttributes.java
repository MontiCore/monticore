/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks, if additional attributes are declared twice
 */
public class OverridingAdditionalAttributes implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4035";

  public static final String ERROR_MSG_FORMAT = " The additional attribute %s is defined twice for the " +
          "rule %s";

  @Override
  public void check(ASTMCGrammar a) {
    for (ProdSymbol prodSymbol: a.getSymbol().getSpannedScope().getLocalProdSymbols()) {
      List<AdditionalAttributeSymbol> attributes = prodSymbol.getSpannedScope().getLocalAdditionalAttributeSymbols();
      List<AdditionalAttributeSymbol> superAttributes = Lists.newArrayList(attributes);
      prodSymbol.getSuperProds().forEach(p -> superAttributes.addAll(p.lazyLoadDelegate().getSpannedScope().getLocalAdditionalAttributeSymbols()));
      for (AdditionalAttributeSymbol ad: attributes) {
        if (superAttributes.stream().filter(m -> ad.getName().equals(m.getName())
                && ad.isIsAstAttr()==m.isIsAstAttr()).count()>1) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ad.getName(), prodSymbol.getName()));
        }
      }
    }
  }

}
