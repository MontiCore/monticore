/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.grammar.cocos;

import java.util.List;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals or only overridden by normal nonterminals.
 *
 * @author KH
 */
public class OverridingNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4009";
  
  public static final String ERROR_MSG_FORMAT = " The production for the nonterminal %s must not be overridden\n" +
          "by a production for an %s nonterminal.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    List<MCGrammarSymbol> grammarSymbols =  grammarSymbol.getSuperGrammars();

    for(MCGrammarSymbol s: grammarSymbols) {
      for (ASTEnumProd p : a.getEnumProds()) {
          doCheck(s.getType(p.getName()), "enum");
      }
      for (ASTExternalProd p : a.getExternalProds()) {
          doCheck(s.getType(p.getName()), "external");
      }
      for (ASTInterfaceProd p : a.getInterfaceProds()) {
          doCheck(s.getType(p.getName()), "interface");
      }
      for (ASTLexProd p : a.getLexProds()) {
          doCheck(s.getType(p.getName()), "lexical");
      }
      for (ASTAbstractProd p : a.getAbstractProds()) {
        doCheck(s.getType(p.getName()), "abstract");
      }
    }
  }

  private void doCheck(MCTypeSymbol typeSymbol, String type) {
    if (typeSymbol != null && typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.CLASS) && !typeSymbol.isAbstract()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, typeSymbol.getName(), type));
    }
  }

}
