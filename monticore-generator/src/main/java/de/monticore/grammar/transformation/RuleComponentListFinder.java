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

package de.monticore.grammar.transformation;

import java.util.Map;

import de.monticore.annotations.Visit;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._ast.ASTRuleComponentList;
import de.monticore.utils.ASTTraverser;


/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$, $Date: 2014-12-22 11:03:30 +0100 (Mo, 22 Dez 2014) $
 * @since   TODO: add version number
 *
 */
public class RuleComponentListFinder implements ASTTraverser.Visitor{
  
  private Map<ASTNonTerminalSeparator, ASTRuleComponentList> map;
  
  /**
   * Constructor for de.monticore.grammar.transformation.RuleComponentListFinder.
   * @param grammar
   * @param map
   */
  public RuleComponentListFinder(Map<ASTNonTerminalSeparator, ASTRuleComponentList> map) {
    super();
    this.map = map;
  }
  
  @Visit
  private void find(ASTRuleComponentList componentList) {
    for (ASTRuleComponent component: componentList) {
      if (component instanceof ASTNonTerminalSeparator) {
        map.put((ASTNonTerminalSeparator) component, componentList);
      }
    }
  }

  
}
