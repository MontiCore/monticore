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

import java.util.Collection;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the productions, which implement an interface, use the
 * non-terminals of that interface.
 * 
 * @author BS
 */
public class SubrulesUseInterfaceNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4047";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must use the non-terminal %s from interface %s.";
  
  public void check(ASTMCGrammar a) {
    for (ASTClassProd classProd : a.getClassProds()) {
      MCRuleSymbol classSymbol = (MCRuleSymbol) classProd.getSymbol().get();
      if (!classProd.getSuperInterfaceRule().isEmpty()) {
        for (ASTRuleReference sr : classProd.getSuperInterfaceRule()) {
          MCRuleSymbol ruleSymbol = (MCRuleSymbol) classProd.getEnclosingScope().get()
              .resolve(sr.getName(), MCRuleSymbol.KIND).orElse(null);
          if (!ruleSymbol.getRuleComponents().isEmpty()) {
            Collection<MCRuleComponentSymbol> interfaceComponents = ruleSymbol.getRuleComponents();
            compareComponents(interfaceComponents, classSymbol);
          }
          checkSuperInterfaces(ruleSymbol, classSymbol);
        }
      }
    }
    
    for (ASTAbstractProd abstractProd : a.getAbstractProds()) {
      MCRuleSymbol abstractSymbol = (MCRuleSymbol) abstractProd.getSymbol().get();
      if (!abstractProd.getSuperInterfaceRule().isEmpty()) {
        for (ASTRuleReference sr : abstractProd.getSuperInterfaceRule()) {
          MCRuleSymbol ruleSymbol = (MCRuleSymbol) abstractProd.getEnclosingScope().get()
              .resolve(sr.getName(), MCRuleSymbol.KIND).orElse(null);
          if (!ruleSymbol.getRuleComponents().isEmpty()) {
            Collection<MCRuleComponentSymbol> interfaceComponents = ruleSymbol.getRuleComponents();
            compareComponents(interfaceComponents, abstractSymbol);
          }
          checkSuperInterfaces(ruleSymbol, abstractSymbol);
        }
      }
    }
  }
  
  private void compareComponents(Collection<MCRuleComponentSymbol> interfaceComponents,
      MCRuleSymbol prodSymbol) {
    for (MCRuleComponentSymbol interfaceComponent : interfaceComponents) {
      if (prodSymbol.getRuleComponent(interfaceComponent.getName()).isPresent()) {
        if (prodSymbol.getRuleComponent(interfaceComponent.getName()).get()
            .getReferencedRuleName().equals(interfaceComponent.getReferencedRuleName())) {
          continue;
        }
      }
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(),
          interfaceComponent.getName(), interfaceComponent.getEnclosingRule().getName()),
          prodSymbol.getSourcePosition());
      
    }
  }
  
  private void checkSuperInterfaces(MCRuleSymbol ruleSymbol, MCRuleSymbol prodSymbol) {
    for (MCTypeSymbol superInterface : ruleSymbol.getType().getAllSTSuperInterfaces()) {
      MCRuleSymbol superRuleSymbol = (MCRuleSymbol) ruleSymbol.getEnclosingScope()
          .resolve(superInterface.getName(), MCRuleSymbol.KIND).orElse(null);
      if (!superRuleSymbol.getRuleComponents().isEmpty()) {
        Collection<MCRuleComponentSymbol> superInterfaceComponents = superRuleSymbol
            .getRuleComponents();
        compareComponents(superInterfaceComponents, prodSymbol);
      }
    }
  }
  
}
