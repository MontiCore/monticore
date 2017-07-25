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

public class MyElementNameStartsWithCapitalLetter implements MyDSLASTMyElementCoCo {
  
  public static final String ERROR_CODE = "0xC0003";
  
  public static final String ERROR_MSG_FORMAT =
      ERROR_CODE + " Element name '%s' should start with a capital letter.";
  
  @Override
  public void check(ASTMyElement element) {
    String elementName = element.getName();
    boolean startsWithUpperCase = Character.isUpperCase(elementName.charAt(0));
    
    if (!startsWithUpperCase) {
      // Issue warning...
      Log.warn(String.format(ERROR_MSG_FORMAT, elementName), element.get_SourcePositionStart());
    }
  }
  
}
