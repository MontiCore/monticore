/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolReference;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import org.junit.Test;

import java.util.Collection;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class ResolvingViaPredicateTest {

  final EntitySymbolReference DUMMY_TYPE_REF = new EntitySymbolReference("DUMMY", new CommonScope(true));
  final EntitySymbolReference DUMMY_TYPE_REF2 = new EntitySymbolReference("DUMMY2", new CommonScope(true));

  @Test
  public void testResolveOnlyPropertiesSymbolWithMatchingType() {
    final EntitySymbol entitySymbol = new EntitySymbol("E");
    entitySymbol.getMutableSpannedScope().addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));

    entitySymbol.addProperty(new PropertySymbol("p", DUMMY_TYPE_REF));

    final Scope spannedScope = entitySymbol.getSpannedScope();

    assertTrue(spannedScope.resolve("p", PropertySymbol.KIND).isPresent());

    assertEquals(1, spannedScope.resolveMany("p", PropertySymbol.KIND, new PropertyTypePredicate(DUMMY_TYPE_REF.getName())).size());
    assertEquals(0, spannedScope.resolveMany("p", PropertySymbol.KIND, new PropertyTypePredicate("OTHERTTYPE")).size());

    entitySymbol.addProperty(new PropertySymbol("p", DUMMY_TYPE_REF2));
    entitySymbol.addProperty(new PropertySymbol("p", DUMMY_TYPE_REF));

    assertEquals(2, spannedScope.resolveMany("p", PropertySymbol.KIND, new PropertyTypePredicate(DUMMY_TYPE_REF.getName())).size());
  }

  @Test
  public void testResolveOnlyActionWithMatchingParameters() {
    final EntitySymbol entitySymbol = new EntitySymbol("E");
    entitySymbol.getMutableSpannedScope().addResolver(CommonResolvingFilter.create(ActionSymbol.KIND));
    entitySymbol.getMutableSpannedScope().addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));

    PropertySymbol param1 = new PropertySymbol("param1", DUMMY_TYPE_REF);
    param1.setParameter(true);
    PropertySymbol param2 = new PropertySymbol("param2", DUMMY_TYPE_REF2);
    param2.setParameter(true);
    PropertySymbol param3 = new PropertySymbol("param3", DUMMY_TYPE_REF);
    param3.setParameter(true);

    final ActionSymbol a1 = new ActionSymbol("action");
    a1.getMutableSpannedScope().addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));

    a1.addParameter(param1);
    a1.addParameter(param2);
    a1.addParameter(param3);

    assertEquals(1, a1.getSpannedScope().resolveMany("param1", PropertySymbol.KIND).size());


    param1 = new PropertySymbol("param1", DUMMY_TYPE_REF);
    param1.setParameter(true);
    param2 = new PropertySymbol("param2", DUMMY_TYPE_REF2);
    param2.setParameter(true);
    param3 = new PropertySymbol("param3", DUMMY_TYPE_REF);
    param3.setParameter(true);

    final ActionSymbol a2 = new ActionSymbol("action");
    a2.getMutableSpannedScope().addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));

    // parameters are in different order than action a1
    a2.addParameter(param1);
    a2.addParameter(param3);
    a2.addParameter(param2);



    entitySymbol.addAction(a1);
    entitySymbol.addAction(a2);

    final Scope spannedScope = entitySymbol.getSpannedScope();

    // make sure that both actions are resolved by name
    assertEquals(2, spannedScope.resolveMany("action", ActionSymbol.KIND).size());

    // resolve only action with paramet types: DUMMY, DUMMY2, DUMMY
    final Collection<Symbol> actionsWithMatchingParameters = spannedScope.resolveMany("action", ActionSymbol.KIND,
        new CorrectActionParametersPredicate(DUMMY_TYPE_REF.getName(), DUMMY_TYPE_REF2.getName(), DUMMY_TYPE_REF.getName()));

    assertEquals(1, actionsWithMatchingParameters.size());
    assertSame(a1, actionsWithMatchingParameters.iterator().next());

  }


  private class PropertyTypePredicate implements Predicate<Symbol> {
    private String typeName;

    public PropertyTypePredicate(String typeName) {
      this.typeName = typeName;
    }

    @Override
    public boolean test(Symbol symbol) {
      if (symbol instanceof PropertySymbol) {
        PropertySymbol p = (PropertySymbol) symbol;
        return p.getType().getName().equals(typeName);
      }

      return false;
    }
  }

  private class CorrectActionParametersPredicate implements Predicate<Symbol> {
    private String[] parameterTypeNames;

    public CorrectActionParametersPredicate(String... parameterTypeNames) {
      this.parameterTypeNames = parameterTypeNames;
    }

    @Override
    public boolean test(Symbol symbol) {
      if (symbol instanceof ActionSymbol) {
        ActionSymbol actionSymbol = (ActionSymbol) symbol;

        if (actionSymbol.getParameters().size() == parameterTypeNames.length) {
          for (int i = 0; i < actionSymbol.getParameters().size(); i++) {
            if (!actionSymbol.getParameters().get(i).getType().getName().equals(parameterTypeNames[i])) {
              return false;
            }
          }
          return true;
        }
      }

      return false;
    }

  }

}
