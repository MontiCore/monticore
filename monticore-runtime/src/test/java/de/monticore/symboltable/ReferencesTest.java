/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.JTypeSymbolMock;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.references.PropertySymbolReference;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertSame;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ReferencesTest {

  @Test
  public void testVariableReference() {
    /**
     * class C {
     * ... D.fieldOfD // reference to fieldOfD
     * }
     *
     * class D {
     *   int fieldOfD;
     * }
     *
     */

    EntitySymbol c = new EntitySymbol("C");
    PropertySymbolReference ref = new PropertySymbolReference("fieldOfD", Optional.of("D"), c.getSpannedScope());

    EntitySymbol d = new EntitySymbol("D");

    PropertySymbol fieldOfD = new PropertySymbol("fieldOfD", new CommonJTypeReference<>("int", JTypeSymbolMock.KIND, c.getSpannedScope()));
    d.addProperty(fieldOfD);

    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilter(CommonResolvingFilter.create(CommonJTypeSymbol.KIND));
    resolvingConfiguration.addDefaultFilter(CommonResolvingFilter.create(PropertySymbol.KIND));

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new ArrayList<>(), resolvingConfiguration);

    globalScope.add(c);
    globalScope.add(d);

    c.getMutableSpannedScope().setResolvingFilters(globalScope.getResolvingFilters());
    d.getMutableSpannedScope().setResolvingFilters(globalScope.getResolvingFilters());

    assertSame(c, globalScope.resolve("C", CommonJTypeSymbol.KIND).orElse(null));
    assertSame(d, globalScope.resolve("D", CommonJTypeSymbol.KIND).orElse(null));

    assertSame(fieldOfD, d.getProperty("fieldOfD").orElse(null));

    assertSame(fieldOfD, ref.getReferencedSymbol());
  }

}
