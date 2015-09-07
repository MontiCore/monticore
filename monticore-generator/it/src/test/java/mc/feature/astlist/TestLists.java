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

package mc.feature.astlist;

import static org.junit.Assert.assertEquals;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTB;
import mc.feature.featuredsl._ast.ASTBList;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

import org.junit.Test;

public class TestLists extends GeneratorIntegrationsTest {
  
  /** Tests if remove function works correctly with the equals method */
  @Test
  public void testLists() {
    
    ASTBList aList = FeatureDSLNodeFactory.createASTBList();
    ASTB a = FeatureDSLNodeFactory.createASTB();
    ASTB b = FeatureDSLNodeFactory.createASTB();
    ASTB c = FeatureDSLNodeFactory.createASTB();
    ASTB d = FeatureDSLNodeFactory.createASTB();
    ASTB e = FeatureDSLNodeFactory.createASTB();
    ASTB f = FeatureDSLNodeFactory.createASTB();
    ASTB g = FeatureDSLNodeFactory.createASTB();
    
    aList.add(a);
    aList.add(b);
    aList.add(c);
    aList.add(d);
    aList.add(e);
    aList.add(f);
    aList.add(g);
    
    assertEquals(6, aList.indexOf(g));
    aList.remove(g);
    assertEquals(-1, aList.indexOf(g));
    
  }
}
