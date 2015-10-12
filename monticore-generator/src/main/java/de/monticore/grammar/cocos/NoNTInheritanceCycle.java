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

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTInterfaceProdCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Checks that nonterminals do not have inheritance cycles.
 *
 * @author KH
 */
public class NoNTInheritanceCycle implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4022";

  public static final String ERROR_MSG_FORMAT = " The production %s introduces an inheritance"
      + " cycle. Inheritance may not be cyclic.";

  @Override
  public void check(ASTProd a) {
    if (a.getSymbol().get() instanceof MCRuleSymbol){
      MCTypeSymbol typeSymbol = ((MCRuleSymbol) a.getSymbol().get()).getType();
      for(MCTypeSymbol sr : typeSymbol.getAllSuperTypes()){
        if(sr.getName().equals(a.getName())){
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
}
