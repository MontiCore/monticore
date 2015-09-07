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

package de.monticore.languages.grammar;

import java.util.Optional;

import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.se_rwth.commons.SourcePosition;

/**
 * This class represents a symbol for a class rule (resp. production). E.g., in <code>A = B "t"</code>
 * <code>A</code> is the class rule.
 *
 * @author  Pedram Mir Seyed Nazari
 * @version $Revision$,
 *          $Date$
 *
 */
public class MCClassRuleSymbol extends MCRuleSymbol {

  private Optional<ASTClassProd> ruleNode;
  
  public MCClassRuleSymbol(ASTClassProd rule) {
    super(HelperGrammar.getRuleName(rule));
    this.ruleNode = Optional.of(rule);
  }

  protected MCClassRuleSymbol(String name) {
   super(name);
  }
  
  /**
   * @see MCRuleSymbol#getKindSymbolRule()
   */
  @Override
  public KindSymbolRule getKindSymbolRule() {
    return KindSymbolRule.PARSERRULE;
  }
  
  /**
   * @see MCRuleSymbol#getType()
   */
  @Override
  public MCTypeSymbol getType() {
    if (super.getType() != null) {
      return super.getType();
    }
    
    return getDefinedType();
  }
  
  @Override
  public SourcePosition getSourcePosition() {
    if (ruleNode.isPresent()) {
      return ruleNode.get().get_SourcePositionStart();
    }
    return super.getSourcePosition();
  }
  
  
  /**
   * Returns the AST associated with this STRule
   * 
   * @return AST for this ruleNode
   */
  public Optional<ASTClassProd> getRuleNode() {
    return this.ruleNode;
  }
  
}
