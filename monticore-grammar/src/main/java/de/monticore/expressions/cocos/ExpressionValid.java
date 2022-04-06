/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
package de.monticore.expressions.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.types.check.TypeCalculator;

import java.util.Optional;


public class ExpressionValid implements ExpressionsBasisASTExpressionCoCo {

  // The error message is thrown in typeCheck

  protected Optional<ASTExpression> checkingNode = Optional.empty();

  protected TypeCalculator typeCheck;

  public ExpressionValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  @Override
  public void endVisit(ASTExpression expr) {
    if (checkingNode.isPresent() && checkingNode.get() == expr) {
      checkingNode = Optional.empty();
    }
  }

  @Override
  public void check(ASTExpression expr) {
    if (!checkingNode.isPresent()) {
      // TypeCheck
      typeCheck.typeOf(expr);
      checkingNode = Optional.of(expr);
    }
  }

}
