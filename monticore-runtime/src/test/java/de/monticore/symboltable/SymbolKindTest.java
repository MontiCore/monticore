/* (c) https://github.com/MontiCore/monticore */

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
