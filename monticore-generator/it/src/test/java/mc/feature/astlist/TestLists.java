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

import java.util.ArrayList;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTB;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

public class TestLists extends GeneratorIntegrationsTest {
  
  /** Tests if remove function works correctly with the equals method */
  @Test
  public void testLists() {
    
    ASTB a = FeatureDSLNodeFactory.createASTB();
    ASTB b = FeatureDSLNodeFactory.createASTB();
    ASTB c = FeatureDSLNodeFactory.createASTB();
    ASTB d = FeatureDSLNodeFactory.createASTB();
    ASTB e = FeatureDSLNodeFactory.createASTB();
    ASTB f = FeatureDSLNodeFactory.createASTB();
    ASTB g = FeatureDSLNodeFactory.createASTB();
    
    ArrayList<ASTB> list = new ArrayList<>();
    list.add(a);
    list.add(b);
    list.add(c);
    list.add(d);
    list.add(e);
    list.add(f);
    list.add(g);
    
    assertEquals(6, list.indexOf(g));
    list.remove(g);
    assertEquals(-1, list.indexOf(g));
    
  }
}
