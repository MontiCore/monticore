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

package mc.feature.constantsshortform;

import static org.junit.Assert.assertEquals;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import mc.feature.constantsshortform.constantsshortform._ast.ConstantsShortFormNodeFactory;

import org.junit.Test;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() {
    ASTA a = ConstantsShortFormNodeFactory.createASTA();
    assertEquals(a.isMyConst(), false);
    a.setMyConst(true);
    assertEquals(a.isMyConst(), true);
    
    ASTB b = ConstantsShortFormNodeFactory.createASTB();
    assertEquals(b.isConst(), false);
    b.setConst(true);
    assertEquals(b.isConst(), true);
  }
  
}
