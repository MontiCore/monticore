package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Map;

public class ReferencedSymbolExists implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4037";

  public static final String ERROR_MSG_FORMAT = " The production for the referenced symbol %s does not exist as a symbol or not at all.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol();
    Scope scope =grammarSymbol.getSpannedScope() ;
    for (ASTClassProd astClassProd : a.getClassProdList()) {
      for (ASTAlt astAlt : astClassProd.getAltList()) {
        isReferenceSymbol(astAlt, grammarSymbol);
      }
    }

    for (ASTInterfaceProd astInterfaceProd : a.getInterfaceProdList()) {
      for (ASTAlt astAlt : astInterfaceProd.getAltList()) {
        isReferenceSymbol(astAlt, grammarSymbol);
      }
    }

    for (ASTAbstractProd astAbstractProd : a.getAbstractProdList()) {
      for (ASTAlt astAlt : astAbstractProd.getAltList()) {
        isReferenceSymbol(astAlt, grammarSymbol);
      }
    }
  }

  private static void isReferenceSymbol(ASTAlt astAlt, MCGrammarSymbol grammarSymbol) {
    for (ASTRuleComponent astRuleComponent : astAlt.getComponentList()) {
      if (astRuleComponent instanceof ASTNonTerminal) {
        if (((ASTNonTerminal) astRuleComponent).isPresentReferencedSymbol()) {
          String symbol = ((ASTNonTerminal) astRuleComponent).getReferencedSymbol();
          if (grammarSymbol.getProdWithInherited(symbol).isPresent()) {
            if (grammarSymbol.getProdWithInherited(symbol).get().isSymbolDefinition()) {
              break;
            }
          }
            Log.error(String.format(ERROR_CODE + String.format(ERROR_MSG_FORMAT, symbol),
                astRuleComponent.get_SourcePositionStart()));
          }
        }
      }
    }
  }

