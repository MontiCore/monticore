/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolReference;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingImportedScopesTest {

  @Test
  public void testOnlyConsiderExplicitlyImportedScopesWhenResolvingInImportedScope() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolvingConfiguration);
    final ArtifactScope as = new ArtifactScope(Optional.empty(), "p", new ArrayList<>());
    gs.addSubScope(as);
    as.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final PropertySymbol asProp = new PropertySymbol("asProp", new EntitySymbolReference("foo", as));
    asProp.setAccessModifier(BasicAccessModifier.PROTECTED);
    as.add(asProp);

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    as.add(supEntity);
    supEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    assertTrue(supEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());

    final PropertySymbol supProp = new PropertySymbol("supProp", new EntitySymbolReference("bar", supEntity.getSpannedScope()));
    supProp.setAccessModifier(BasicAccessModifier.PROTECTED);
    supEntity.addProperty(supProp);

    assertTrue(supEntity.getSpannedScope().resolve("supProp", PropertySymbol.KIND).isPresent());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    gs.add(subEntity);
    subEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    subEntity.setSuperClass(new EntitySymbolReference("p.SupEntity", gs));

    assertTrue(subEntity.getSuperClass().get().existsReferencedSymbol());
    // Resolving property that is defined in super entity (and not private) should work
    assertTrue(subEntity.getSpannedScope().resolve("supProp", PropertySymbol.KIND).isPresent());
    // Resolving property that is defined in the enclosing scope of the super entity should not work
    assertFalse(subEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testSymbolInImportedScopeHasHigherPriorityThanSymbolInEnclosingScope() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolvingConfiguration);
    final ArtifactScope as = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
    gs.addSubScope(as);
    as.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final PropertySymbol asProp = new PropertySymbol("prop", new EntitySymbolReference("foo", as));
    asProp.setAccessModifier(BasicAccessModifier.PROTECTED);
    as.add(asProp);

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    as.add(subEntity);
    subEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    gs.add(supEntity);
    supEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final PropertySymbol supProp = new PropertySymbol("prop", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supProp.setAccessModifier(BasicAccessModifier.PROTECTED);
    supEntity.addProperty(supProp);

    // Resolving "prop" in sub enity should resolve to (non-private) property symbol of super entity (instead of enclosing scope)
    final PropertySymbol resolvedProp = subEntity.getSpannedScope().<PropertySymbol>resolve("prop", PropertySymbol.KIND).get();
    assertSame(supProp, resolvedProp);
  }

  @Test
  public void testResolutionContinuesWIthPackageLocalModifierIfSuperTypeIsInSamePackage() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolvingConfiguration);
    final ArtifactScope asSub = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());
    gs.addSubScope(asSub);
    asSub.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    asSub.add(subEntity);
    subEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    final ArtifactScope asSup = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());
    gs.addSubScope(asSup);
    asSup.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    asSup.add(supEntity);
    supEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final PropertySymbol supProp = new PropertySymbol("prop", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supProp.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);
    supEntity.addProperty(supProp);

    // SubEntity and SupEntity are in same package, hence, package-local field "prop" can be resolved.
    PropertySymbol resolvedProp = subEntity.getSpannedScope().<PropertySymbol>resolve("prop", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get();
    assertSame(supProp, resolvedProp);
  }

  @Test
  public void testResolutionContinuesWithProtectedModifierIfSuperTypeIsInDifferentPackage() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolvingConfiguration);
    final ArtifactScope asSub = new ArtifactScope(Optional.empty(), "p.q",  singletonList(new ImportStatement("x.y", true)));
    gs.addSubScope(asSub);
    asSub.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    asSub.add(subEntity);
    subEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    // Super type is in another package
    final ArtifactScope asSup = new ArtifactScope(Optional.empty(), "x.y", new ArrayList<>());
    gs.addSubScope(asSup);
    asSup.setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    asSup.add(supEntity);
    supEntity.getMutableSpannedScope().setResolvingFilters(resolvingConfiguration.getDefaultFilters());

    final PropertySymbol supPropPackageLocal = new PropertySymbol("propPL", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supPropPackageLocal.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);
    supEntity.addProperty(supPropPackageLocal);

    final PropertySymbol supPropProtected = new PropertySymbol("propProtected", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supPropProtected.setAccessModifier(BasicAccessModifier.PROTECTED);
    supEntity.addProperty(supPropProtected);

    // SubEntity and SupEntity are not in same package, hence, package-local field "propPL" cannot be resolved...
    assertFalse(subEntity.getSpannedScope().<PropertySymbol>resolve("propPL", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).isPresent());
    // ...but the protected field "propProtected" can be resolved.
    assertTrue(subEntity.getSpannedScope().<PropertySymbol>resolve("propProtected", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).isPresent());

  }

}
