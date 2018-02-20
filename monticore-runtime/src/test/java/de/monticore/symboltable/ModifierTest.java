/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Set;

import com.google.common.collect.Sets;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ModifierTest {

  @Test
  public void test() {
    EntitySymbol entity = new EntitySymbol("Entity");

    final JTypeReference<JTypeSymbol> intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope());
    
    PropertySymbol publicI = new PropertySymbol("i", intReference);
    publicI.setAccessModifier(BasicAccessModifier.PUBLIC);
    
    PropertySymbol protectedJ = new PropertySymbol("j", intReference);
    protectedJ.setAccessModifier(BasicAccessModifier.PROTECTED);
    
    PropertySymbol defaultK = new PropertySymbol("k", intReference);
    defaultK.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);
    
    PropertySymbol privateL = new PropertySymbol("l", intReference);
    privateL.setAccessModifier(BasicAccessModifier.PRIVATE);
    
    entity.addProperty(publicI);
    entity.addProperty(protectedJ);
    entity.addProperty(defaultK);
    entity.addProperty(privateL);
   
    MutableScope scope = entity.getMutableSpannedScope();
    
    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = Sets.newLinkedHashSet();
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol.KIND));
    scope.setResolvingFilters(resolvingFilters);
    
    // public (is always found)
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PUBLIC).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PROTECTED).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PACKAGE_LOCAL).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, AccessModifier.ALL_INCLUSION).get());
    
    // protected
    assertFalse(scope.resolve("j", PropertySymbol.KIND, BasicAccessModifier.PUBLIC).isPresent());
    assertSame(protectedJ, scope.resolve("j", PropertySymbol.KIND, BasicAccessModifier.PROTECTED).get());
    assertSame(protectedJ, scope.resolve("j", PropertySymbol.KIND, BasicAccessModifier.PACKAGE_LOCAL).get());
    assertSame(protectedJ, scope.resolve("j", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get());
    assertSame(protectedJ, scope.resolve("j", PropertySymbol.KIND, AccessModifier.ALL_INCLUSION).get());
    
    // default (package access)
    assertFalse(scope.resolve("k", PropertySymbol.KIND, BasicAccessModifier.PUBLIC).isPresent());
    assertFalse(scope.resolve("k", PropertySymbol.KIND, BasicAccessModifier.PROTECTED).isPresent());
    assertSame(defaultK, scope.resolve("k", PropertySymbol.KIND, BasicAccessModifier.PACKAGE_LOCAL).get());
    assertSame(defaultK, scope.resolve("k", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get());
    assertSame(defaultK, scope.resolve("k", PropertySymbol.KIND, AccessModifier.ALL_INCLUSION).get());
    
    // private
    assertFalse(scope.resolve("l", PropertySymbol.KIND, BasicAccessModifier.PUBLIC).isPresent());
    assertFalse(scope.resolve("l", PropertySymbol.KIND, BasicAccessModifier.PROTECTED).isPresent());
    assertFalse(scope.resolve("l", PropertySymbol.KIND, BasicAccessModifier.PACKAGE_LOCAL).isPresent());
    assertSame(privateL, scope.resolve("l", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get());
    assertSame(privateL, scope.resolve("l", PropertySymbol.KIND, AccessModifier.ALL_INCLUSION).get());
  }


  public void test2() {
    EntitySymbol entity = new EntitySymbol("Entity");

    final JTypeReference<JTypeSymbol> intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope());

    PropertySymbol publicI = new PropertySymbol("i", intReference);
    publicI.setAccessModifier(BasicAccessModifier.PUBLIC);

    PropertySymbol protectedJ = new PropertySymbol("j", intReference);
    protectedJ.setAccessModifier(BasicAccessModifier.PROTECTED);

    PropertySymbol defaultK = new PropertySymbol("k", intReference);
    defaultK.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);

    PropertySymbol privateL = new PropertySymbol("l", intReference);
    privateL.setAccessModifier(BasicAccessModifier.PRIVATE);

    entity.addProperty(publicI);
    entity.addProperty(protectedJ);
    entity.addProperty(defaultK);
    entity.addProperty(privateL);

    MutableScope scope = entity.getMutableSpannedScope();

    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = Sets.newLinkedHashSet();
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol
        .KIND));
    scope.setResolvingFilters(resolvingFilters);

    // public (is always found)
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PUBLIC).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PROTECTED).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PACKAGE_LOCAL).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get());
    assertSame(publicI, scope.resolve("i", PropertySymbol.KIND, AccessModifier.ALL_INCLUSION).get());
  }
}
