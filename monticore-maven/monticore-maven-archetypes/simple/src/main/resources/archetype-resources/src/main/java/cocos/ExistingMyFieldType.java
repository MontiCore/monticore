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

package ${package}.cocos;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;

import ${package}.mydsl._ast.ASTMyField;
import ${package}.mydsl._cocos.MyDSLASTMyFieldCoCo;
import ${package}.symboltable.MyElementSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class ExistingMyFieldType implements MyDSLASTMyFieldCoCo {
  
  public static final String ERROR_CODE = "0xC0002";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " The referenced element '%s' of the field '%s' does not exist.";
  
  @Override
  public void check(ASTMyField field) {
    checkArgument(field.getEnclosingScope().isPresent());
    
    Scope enclosingScope = field.getEnclosingScope().get();
    Optional<MyElementSymbol> typeElement = enclosingScope.resolve(field.getType(), MyElementSymbol.KIND);
    
    if (!typeElement.isPresent()) {
      // Issue error...
      Log.error(String.format(ERROR_MSG_FORMAT, field.getType(), field.getName()),
          field.get_SourcePositionStart());
    }
  }
}
