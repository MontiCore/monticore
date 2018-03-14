/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingTest {

  @Test
  public void testSameSymbolOccursOnlyOnce() {
    final EntitySymbol entity = new EntitySymbol("Entity");

    final MutableScope localScope = new CommonScope(false);
    localScope.addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));

    localScope.add(entity);
    localScope.add(entity);

    assertEquals(2, localScope.getLocalSymbols().get(entity.getName()).size());

    try {
      // Although the same symbol is stored twice in the scope, it is
      // only returned once, hence, no ambiguity exists.
      localScope.resolve("Entity", EntitySymbol.KIND).get();
    }
    catch(ResolvedSeveralEntriesException e) {
      fail();
    }

  }

  @Test
  public void testSpecificResolversForScopes() {
    final EntitySymbol entity = new EntitySymbol("Entity");
    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    entity.addProperty(property);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final MutableScope localScope = new CommonScope(false);
    ((MutableScope)action.getSpannedScope()).addSubScope(localScope);

    final ResolvingFilter<PropertySymbol> propertyResolvingFilter = CommonResolvingFilter.create(PropertySymbol.KIND);

    // Only localScope is initialized with a resolver for properties
    localScope.addResolver(propertyResolvingFilter);

    // If resolving starts from the local scope, prop is found, because local scope is
    // initialized with a property resolvers.
    assertTrue(localScope.resolve("prop", PropertySymbol.KIND).isPresent());
    assertSame(property, localScope.resolve("prop", PropertySymbol.KIND).get());

    // Although prop is defined in entity, it cannot be resolved starting from action or entity,
    // because these scopes are not initialized with a property resolvers.
    assertFalse(action.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
    assertFalse(entity.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());


    entity.getMutableSpannedScope().addResolver(propertyResolvingFilter);
    assertFalse(action.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
    assertTrue(entity.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testResolveInnerSymbol() {
    final EntitySymbol entity = new EntitySymbol("Entity");

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    action.addVariable(property);


    final ModelingLanguage modelingLanguage = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(modelingLanguage.getResolvingFilters());
    final ModelPath modelPath = new ModelPath(Paths.get(""));

    final GlobalScope globalScope = new GlobalScope(modelPath, modelingLanguage, resolvingConfiguration);
    globalScope.add(entity);

    assertTrue(globalScope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("Entity.action", ActionSymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("Entity.action.prop", PropertySymbol.KIND).isPresent());

    assertFalse(globalScope.resolve("Entity.action2.prop", PropertySymbol.KIND).isPresent());
  }


  @Test
  public void testResolveInnerSymbolWithArtifactScope() {
    final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());

    final EntitySymbol entity = new EntitySymbol("Entity");
    artifactScope.add(entity);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    action.addVariable(property);


    final ModelingLanguage modelingLanguage = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(modelingLanguage.getResolvingFilters());
    final ModelPath modelPath = new ModelPath(Paths.get(""));

    final GlobalScope globalScope = new GlobalScope(modelPath, modelingLanguage, resolvingConfiguration);
    globalScope.addSubScope(artifactScope);

    assertTrue(globalScope.resolve("p.q.Entity", EntitySymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("p.q.Entity.action", ActionSymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("p.q.Entity.action.prop", PropertySymbol.KIND).isPresent());

    // Cannot be resolved since names are not qualified
    assertFalse(globalScope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertFalse(globalScope.resolve("Entity.action", ActionSymbol.KIND).isPresent());
    assertFalse(globalScope.resolve("Entity.action.prop", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testCannotResolveInnerSymbolViaPartialName() {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(new EntityLanguage().getResolvingFilters());

    final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "p", new ArrayList<>());
    artifactScope.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final EntitySymbol entity = new EntitySymbol("Entity");
    entity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());
    artifactScope.add(entity);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    assertTrue(entity.getSpannedScope().resolve("action", ActionSymbol.KIND).isPresent());
    assertTrue(entity.getSpannedScope().resolve("p.Entity.action", ActionSymbol.KIND).isPresent());

    // Cannot resolve Entity.action, since it is not clear whether it is a qualified
    // or a partial name.
    assertFalse(entity.getSpannedScope().resolve("Entity.action", ActionSymbol.KIND).isPresent());

  }

}
