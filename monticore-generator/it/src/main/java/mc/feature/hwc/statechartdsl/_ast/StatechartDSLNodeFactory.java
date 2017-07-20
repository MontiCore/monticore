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

package mc.feature.hwc.statechartdsl._ast;

import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactoryTOP;

public class StatechartDSLNodeFactory extends StatechartDSLNodeFactoryTOP {
  
  protected StatechartDSLNodeFactory() {
  }
  
  public static ASTStatechart createASTStatechart() {
    if (factoryASTStatechart == null) {
      factoryASTStatechart = new StatechartDSLNodeFactory();
    }
    return factoryASTStatechart.doCreateASTStatechart();
  }
  
  protected ASTStatechart doCreateASTStatechart() {
    ASTStatechart s = new ASTStatechart();
    s.setName("default");
    return s;
  }
  
}
