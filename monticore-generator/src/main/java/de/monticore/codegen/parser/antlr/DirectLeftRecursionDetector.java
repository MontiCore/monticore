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

package de.monticore.codegen.parser.antlr;

import java.util.List;

import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.utils.ASTNodes;

/**
 * Checks if a MC production is a left directly left recursive: e.g. of the form A -> A.*
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class DirectLeftRecursionDetector {
  

  public boolean isAlternativeLeftRecursive(final ASTAlt productionAlternative, final ASTNonTerminal actualNonTerminal) {
    final String classProductionName = actualNonTerminal.getName();
    final List<ASTNonTerminal> nonterminals = ASTNodes.getSuccessors(productionAlternative, ASTNonTerminal.class);
    final List<ASTTerminal> terminals = ASTNodes.getSuccessors(productionAlternative, ASTTerminal.class);
    final List<ASTConstant> constants = ASTNodes.getSuccessors(productionAlternative, ASTConstant.class);
    // rules of the form Expr -> "a"
    if (nonterminals.isEmpty()) {
      return false;
    }

    if (!terminals.isEmpty() && !nonterminals.isEmpty()) {
      final ASTTerminal leftmostTerminal = terminals.get(0);
      final ASTNonTerminal leftmostNonterminal = nonterminals.get(0);
      // checks the case: Expr -> "a" Expr, e.g. the nonterminal occurs before the terminal
      if (leftmostNonterminal.get_SourcePositionStart().compareTo(leftmostTerminal.get_SourcePositionStart()) > 0) {
        return false;
      }
    }

    if (!constants.isEmpty() && !nonterminals.isEmpty()) {
      final ASTConstant leftmostConstant = constants.get(0);
      final ASTNonTerminal leftmostNonterminal = nonterminals.get(0);
      // checks the case: Expr -> ["a"] Expr, e.g. the nonterminal occurs before the terminal
      if (leftmostNonterminal.get_SourcePositionStart().compareTo(leftmostConstant.get_SourcePositionStart()) > 0) {
        return false;
      }
    }

    // e.g. the alternative begins with a nonterminal.
    if (!nonterminals.isEmpty()) {
      final ASTNonTerminal leftmostNonterminal = nonterminals.get(0);
      if ((leftmostNonterminal == actualNonTerminal) && leftmostNonterminal.getName().equals(classProductionName)) {
        return true;
      }

    }
    return false;
  }
}
