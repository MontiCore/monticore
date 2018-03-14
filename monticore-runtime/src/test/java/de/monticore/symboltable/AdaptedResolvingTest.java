/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.scandentity.Action2StateAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.Action2StateTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.scandentity.Entity2ScAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.Entity2ScTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.scandentity.Entity2StateAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.Entity2StateTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.scandentity.Sc2EntityAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.Sc2EntityTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.scandentity.State2EntityTransitiveResolvingFilter;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class AdaptedResolvingTest {

  @Test
  public void testAdaptedResolvingInSameScope() {
    final MutableScope scope = new CommonScope(true);

    final EntitySymbol entity = new EntitySymbol("Entity");
    final StateChartSymbol sc = new StateChartSymbol("Sc");

    scope.add(entity);
    scope.add(sc);

    scope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));

    assertTrue(scope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertTrue(scope.resolve("Sc", StateChartSymbol.KIND).isPresent());

    // adapted resolving
    assertFalse(scope.resolve("Sc", EntitySymbol.KIND).isPresent());

    // Statechart -> Entity
    scope.addResolver(new Sc2EntityTransitiveResolvingFilter());
    final EntitySymbol sc2entity = scope.<EntitySymbol>resolve("Sc", EntitySymbol.KIND).orElse(null);
    assertNotNull(sc2entity);
    assertEquals("Sc", sc2entity.getName());
    assertTrue(sc2entity instanceof Sc2EntityAdapter);
    assertSame(sc, ((Sc2EntityAdapter) sc2entity).getAdaptee());

    // Entity -> State
    assertFalse(scope.resolve("Entity", StateSymbol.KIND).isPresent());
    scope.addResolver(new Entity2StateTransitiveResolvingFilter());
    final StateSymbol entity2state = scope.<StateSymbol>resolve("Entity", StateSymbol.KIND)
        .orElse(null);
    assertNotNull(entity2state);
    assertEquals("Entity", entity2state.getName());
    assertTrue(entity2state instanceof Entity2StateAdapter);
    assertSame(entity, ((Entity2StateAdapter) entity2state).getAdaptee());
  }
  
  @Test
  public void testAdaptedResolvingInDifferentScope() {
    final MutableScope parentScope = new CommonScope(true);
    final MutableScope subScope = new CommonScope(true);
    parentScope.addSubScope(subScope);

    final EntitySymbol entity = new EntitySymbol("Entity");
    final StateChartSymbol sc = new StateChartSymbol("Sc");

    // Symbols are defined in parent scope...
    parentScope.add(entity);
    parentScope.add(sc);

    // ...Resolving filters are only registered in the sub scope.
    subScope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    subScope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));
    subScope.addResolver(new Sc2EntityTransitiveResolvingFilter());
    subScope.addResolver(new Entity2StateTransitiveResolvingFilter());


    // Symbols cannot be resolved starting in the parent scope, since no resolving filters are
    // registered there.
    assertFalse(parentScope.<EntitySymbol>resolve("Sc", EntitySymbol.KIND).isPresent());
    assertFalse(parentScope.<StateSymbol>resolve("Entity", StateSymbol.KIND).isPresent());


    // Start resolving in sub scope.

    // Statechart -> Entity
    final EntitySymbol sc2entity = subScope.<EntitySymbol>resolve("Sc", EntitySymbol.KIND).orElse(null);
    assertNotNull(sc2entity);
    assertEquals("Sc", sc2entity.getName());
    assertTrue(sc2entity instanceof Sc2EntityAdapter);
    assertSame(sc, ((Sc2EntityAdapter) sc2entity).getAdaptee());

    // Entity -> State
    final StateSymbol entity2state = subScope.<StateSymbol>resolve("Entity", StateSymbol.KIND).orElse(null);
    assertNotNull(entity2state);
    assertEquals("Entity", entity2state.getName());
    assertTrue(entity2state instanceof Entity2StateAdapter);
    assertSame(entity, ((Entity2StateAdapter) entity2state).getAdaptee());
  }

  @Test
  public void testTransitiveAdaptedResolvingInSameScope() {
    final MutableScope scope = new CommonScope(true);

    final EntitySymbol entity = new EntitySymbol("Entity");
    final StateChartSymbol sc = new StateChartSymbol("Sc");
    final StateSymbol state = new StateSymbol("state");

    scope.add(entity);
    scope.add(sc);
    scope.add(state);

    scope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateSymbol.KIND));

    // Adapted resolving filters
    scope.addResolver(new Entity2StateTransitiveResolvingFilter());
    scope.addResolver(new Sc2EntityTransitiveResolvingFilter());

    // Statechart -> Entity -> State => (((Statechart) -> Entity) -> State)
    final StateSymbol sc2entity2state = scope.<StateSymbol>resolve("Sc", StateSymbol.KIND).orElse(null);
    assertNotNull(sc2entity2state);
    assertEquals("Sc", sc2entity2state.getName());
    // (((Statechart) -> Entity) -> State)
    assertTrue(sc2entity2state instanceof  Entity2StateAdapter);
    final Entity2StateAdapter adap1 = (Entity2StateAdapter) sc2entity2state;
    // ((Statechart) -> Entity)
    assertTrue(adap1.getAdaptee() instanceof Sc2EntityAdapter);
    final Sc2EntityAdapter adap2 = (Sc2EntityAdapter) adap1.getAdaptee();
    // (Statechart)
    assertSame(sc, adap2.getAdaptee());
  }
  @Test
  public void testCircularAdaptedResolvingDependencies() {
    final MutableScope scope = new CommonScope(true);

    final EntitySymbol entity = new EntitySymbol("Entity");
    final StateChartSymbol sc = new StateChartSymbol("Sc");

    scope.add(entity);
    scope.add(sc);

    scope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));

    assertTrue(scope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertTrue(scope.resolve("Sc", StateChartSymbol.KIND).isPresent());

    // StateChart -> Entity
    scope.addResolver(new Sc2EntityTransitiveResolvingFilter());
    final EntitySymbol sc2entity = scope.<EntitySymbol>resolve("Sc", EntitySymbol.KIND).orElse(null);
    assertNotNull(sc2entity);
    assertEquals("Sc", sc2entity.getName());
    assertTrue(sc2entity instanceof Sc2EntityAdapter);
    assertSame(sc, ((Sc2EntityAdapter) sc2entity).getAdaptee());

    // Now a Entity -> Statechart adapter is added, hence (theoretically) a circular dependency
    // exists:  Entity -> StateChart -> Entity
    // The resolving mechanismus should recognize this case and stop after Entity -> StateChart
    scope.addResolver(new Entity2ScTransitiveResolvingFilter());
    final StateChartSymbol entity2sc = scope.<StateChartSymbol>resolve("Entity", StateChartSymbol.KIND).orElse(null);
    assertNotNull(entity2sc);
    assertEquals("Entity", entity2sc.getName());
    assertTrue(entity2sc instanceof Entity2ScAdapter);
    assertSame(entity, ((Entity2ScAdapter) entity2sc).getAdaptee());
  }
 
  @Test
  public void testTransitiveCircularAdaptedResolvingDependencies() {
    final MutableScope scope = new CommonScope(true);

    final EntitySymbol entity = new EntitySymbol("Entity");

    // Add all kinds of symbols to ensure that really the correct one is resolved
    scope.add(entity);
    scope.add(new ActionSymbol("action"));
    scope.add(new StateChartSymbol("Sc"));
    scope.add(new StateSymbol("state"));

    scope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(ActionSymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));
    scope.addResolver(CommonResolvingFilter.create(StateSymbol.KIND));

    // Adapted resolving filters
    scope.addResolver(new Entity2ScTransitiveResolvingFilter());
    scope.addResolver(new Action2StateTransitiveResolvingFilter());
    scope.addResolver(new Sc2ActionTransitiveResolvingFilter());
    scope.addResolver(new State2EntityTransitiveResolvingFilter());

    // Circular dependency: Entity -> StateChart -> Action -> State -> Entity
    // The resolving mechanism should recognize this case and stop after Entity -> StateChart
    final StateSymbol entity2state = scope.<StateSymbol>resolve("Entity", StateSymbol.KIND).orElse(null);
    assertNotNull(entity2state);
    assertEquals("Entity", entity2state.getName());
    // NOTE: the last adapter (i.e., Action -> State) is returned.
    assertTrue(entity2state instanceof Action2StateAdapter);
    assertTrue(((Action2StateAdapter) entity2state).getAdaptee() instanceof Sc2ActionAdapter);
    assertTrue(((Sc2ActionAdapter)((Action2StateAdapter) entity2state).getAdaptee()).getAdaptee()
        instanceof Entity2ScAdapter);

    // Finally, the state is just an entity.
    assertSame(entity, ((Entity2ScAdapter)((Sc2ActionAdapter) ((Action2StateAdapter) entity2state)
        .getAdaptee()).getAdaptee()).getAdaptee());
  }


}
