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

import java.util.Map;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class ProdWithExtensionMustNotBeOverridden implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4010";

  public static final String ERROR_MSG_FORMAT = " The production %s must not be overridden because there"
      + " already exist productions extending it.";

  @Override
  public void check(ASTProd a) {

    MCRuleSymbol ruleSymbol = (MCRuleSymbol) a.getSymbol().get();
    MCGrammarSymbol grammarSymbol = ruleSymbol.getGrammarSymbol();
    boolean isOverriding  = false;
    for(MCGrammarSymbol sup : grammarSymbol.getAllSuperGrammars()){
      if(sup.getRule(a.getName()) != null){
        isOverriding = true;
        break;
      }
    }
    if(!isOverriding) return;

    boolean extensionFound = false;
    entryLoop: for(Map.Entry entry : grammarSymbol.getRulesWithInherited().entrySet()){
      MCRuleSymbol rs = (MCRuleSymbol) entry.getValue();
      for(MCTypeSymbol typeSymbol : rs.getType().getAllSuperclasses()){
        if (a.getName().equals(typeSymbol.getName())) {
          extensionFound = true;
          break entryLoop;
        }
      }
    }
    if (extensionFound) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
