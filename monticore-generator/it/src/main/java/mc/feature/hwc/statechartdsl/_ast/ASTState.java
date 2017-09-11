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

import java.util.List;

public class ASTState extends mc.feature.hwc.statechartdsl._ast.ASTStateTOP {
  
  protected ASTState() {
    super();
  }
  
  protected ASTState(
      ASTEntryAction entryAction,
      ASTExitAction exitAction,
      boolean initial,
      boolean r_final,
      String name,
      List<ASTState> states,
      List<ASTTransition> transitions
     ) {
    super(entryAction, exitAction, r_final, initial, name, states, transitions);
  };
  
  
  public static class StateBuilder extends ASTStateTOP.StateBuilder  {

    protected StateBuilder() {};

    public StateBuilder name(String name) {
      // For testing Purposes only: we adapt the name method
      this.name = name+"Suf1";
      return this;
    }
    
  }    

}
