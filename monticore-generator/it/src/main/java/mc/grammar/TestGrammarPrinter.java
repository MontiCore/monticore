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

package mc.grammar;

import de.se_rwth.commons.Names;
import mc.grammar.ittestgrammar._ast.ASTGenericType;

/**
 * Some helper methods for GrammarDSL
 * 
 * @author krahn
 */
public class TestGrammarPrinter extends de.monticore.grammar.HelperGrammar {
  
  public static String printGenericType(ASTGenericType genericType) {
    
    StringBuilder b = new StringBuilder();
    
    b.append(Names.getQualifiedName(genericType.getNames()));
    
    boolean first = true;
    for (ASTGenericType t : genericType.getGenericTypes()) {
      if (first) {
        b.append("<");
        first = false;
      }
      else {
        b.append(",");
        
      }
      
      b.append(printGenericType(t));
    }
    
    if (!first) {
      b.append(">");
    }
    
    int dimension = genericType.getDimension();
    for (int i = dimension; i > 0; i--) {
      b.append("[]");
    }
    
    return b.toString();
  }
  
 
  
}
