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
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;


public class ExpressionValid implements ExpressionsBasisASTExpressionCoCo {

  public static final String ERROR_CODE = "0xA0513";

  public static final String ERROR_MSG_FORMAT = " No Type for Expression %s";

  protected Optional<ASTExpression> checkingNode = Optional.empty();

  protected IDerive iDerive;

  public ExpressionValid(IDerive iDerive) {
    this.iDerive = iDerive;
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
      iDerive.init();
      expr.accept(iDerive.getTraverser());
      Optional<SymTypeExpression> result = iDerive.getResult();
      if (!result.isPresent()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, expr));
      }
      checkingNode = Optional.of(expr);
    }
  }

}
