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

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._cocos.MyDSLASTMyElementCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneMyField implements MyDSLASTMyElementCoCo {
  
  public static final String ERROR_CODE = "0xC0001";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " The element '%s' should have at least one field.";
  
  @Override
  public void check(ASTMyElement element) {    
    if (element.getMyFields().isEmpty()) {
      // Issue warning...
      Log.warn(String.format(ERROR_MSG_FORMAT, element.getName()),
          element.get_SourcePositionStart());
    }
  }
  
}
