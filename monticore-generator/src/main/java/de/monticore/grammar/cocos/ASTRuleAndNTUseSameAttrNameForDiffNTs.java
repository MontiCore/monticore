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

import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;

/**
 *
 *
 * @author KH
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTs implements GrammarASTASTRuleCoCo {

  public static final String ERROR_CODE = "0xA4028";

  public static final String ERROR_MSG_FORMAT = " The AST rule for the nonterminal %s must not use the "
      + "same attribute name %s as the corresponding production "
      + "with the type %s as %s is not "
      + "identical to or a super type of %s.";

  @Override
  public void check(ASTASTRule a) {
    MCProdSymbol symbol = (MCProdSymbol) a.getEnclosingScope().get().resolve(a.getType(),
        MCProdSymbol.KIND).get();
    for(ASTAttributeInAST attr : a.getAttributeInASTs()) {
      Optional<MCProdComponentSymbol> rc = symbol.getProdComponent(attr.getName().orElse(""));
      if (rc.isPresent()) {
        if (!attr.getGenericType().getTypeName().endsWith(rc.get().getReferencedSymbolName().get())) {
          // TODO GV:
//          MCTypeSymbol attrType = (MCTypeSymbol) a.getEnclosingScope().get()
//              .resolve(attr.getGenericType().getTypeName(), MCTypeSymbol.KIND).orElse(null);
//          MCTypeSymbol compType = (MCTypeSymbol) a.getEnclosingScope().get()
//              .resolve(rc.get().getReferencedRuleName(), MCTypeSymbol.KIND).orElse(null);
//          if (attrType != null && compType != null && !compType.isSubtypeOf(attrType)) {
//            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType(), attr.getName().get(), attr.getGenericType().getTypeName(), attr.getGenericType().getTypeName(), rc.get().getReferencedRuleName()),
//                a.get_SourcePositionStart());
//          }
        }
      }
    }
  }
}
