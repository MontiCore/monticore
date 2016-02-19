/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.monticore.symboltable.mocks.languages.extendedstatechart.XStateChartSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import org.junit.Test;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolKindTest {

  @Test
  public void testSymbolKind() {
    assertEquals(SymbolKind.class.getName(), SymbolKind.KIND.getName());
    assertTrue(SymbolKind.KIND.isKindOf(SymbolKind.KIND));
    assertTrue(SymbolKind.KIND.isSame(SymbolKind.KIND));
  }

  @Test
  public void testKindHierarchy() {
    // XSc is kind of SC and SymbolKind...
    assertTrue(XStateChartSymbol.KIND.isKindOf(StateChartSymbol.KIND));
    assertTrue(XStateChartSymbol.KIND.isKindOf(SymbolKind.KIND));

    // ...but not the same.
    assertFalse(XStateChartSymbol.KIND.isSame(StateChartSymbol.KIND));
    assertFalse(XStateChartSymbol.KIND.isSame(SymbolKind.KIND));

    // Neither SC nor SymbolKind is kind of XSc
    assertFalse(StateChartSymbol.KIND.isKindOf(XStateChartSymbol.KIND));
    assertFalse(SymbolKind.KIND.isKindOf(XStateChartSymbol.KIND));
  }

}
