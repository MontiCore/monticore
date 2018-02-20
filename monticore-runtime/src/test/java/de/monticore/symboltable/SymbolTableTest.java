/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import de.monticore.symboltable.mocks.languages.JTypeSymbolMock;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertyPredicate;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolTableTest {

  private MutableScope topScope;

  private JTypeReference<JTypeSymbol> intReference;
  private JTypeReference<JTypeSymbol> stringReference;

  @Before
  public void setUp() {
    topScope = new CommonScope(true);

    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();
    resolvingFilters.add(CommonResolvingFilter.create(JTypeSymbolMock.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol.KIND));
    
    topScope.setResolvingFilters(resolvingFilters);
    
    topScope.add(new JTypeSymbolMock("int"));
    topScope.add(new JTypeSymbolMock("String"));

    JTypeSymbolMock intSymbol = topScope.<JTypeSymbolMock>resolve("int", JTypeSymbolMock.KIND).get();
    JTypeSymbolMock stringSymbol = topScope.<JTypeSymbolMock>resolve("String", JTypeSymbolMock.KIND).get();

    intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, topScope);
    stringReference = new CommonJTypeReference<>("String", JTypeSymbol.KIND, topScope);

    assertSame(intSymbol, intReference.getReferencedSymbol());
    assertSame(stringSymbol, stringReference.getReferencedSymbol());
  }
  
  @Test
  public void test() {
    /**
     * String var1;
     * int var2;
     * 
     *  void m() {
     *    int var1;
     *  
     *  }
     * 
     * 
     */
    
    
    ActionSymbol method = new ActionSymbol("m");
    topScope.add(method);
    
    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol.KIND));
    ((MutableScope)method.getSpannedScope()).setResolvingFilters(resolvingFilters);
    
    
    assertSame(topScope, method.getEnclosingScope());
    assertSame(method.getEnclosingScope(), method.getSpannedScope().getEnclosingScope().get());
    
    PropertySymbol variable = new PropertySymbol("var1", intReference);
    method.addVariable(variable);
    
    assertSame(method.getSpannedScope(), variable.getEnclosingScope());
    
    assertSame(variable, method.getVariable("var1").get());
    assertFalse(method.getVariable("NotExisting").isPresent());
    
    PropertySymbol globalVariable1 = new PropertySymbol("var1", stringReference);
    PropertySymbol globalVariable2 = new PropertySymbol("var2", intReference);
    topScope.add(globalVariable1);
    topScope.add(globalVariable2);
    
    assertSame(globalVariable1, topScope.resolve("var1", PropertySymbol.KIND).get());
    // returns only the local variable  
    assertSame(variable, method.getVariable("var1").get());
    // returns nothing, because var2 is not defined locally
    assertFalse(method.getVariable("var2").isPresent());
    // normal resolving finds the global variable
    assertSame(globalVariable2, method.getSpannedScope().resolve("var2", PropertySymbol.KIND).get());
  }
  
  @Test
  public void testResolveWithFilter() {
    /**
     * String var1;
     * int var2;
     * 
     *  void m() {
     *    int var1;
     *  
     *  }
     */
    ActionSymbol method = new ActionSymbol("m");
    topScope.add(method);
    
    PropertySymbol variable = new PropertySymbol("var1", intReference);
    method.addVariable(variable);
    
    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol.KIND));
    ((MutableScope)method.getSpannedScope()).setResolvingFilters(resolvingFilters);
    
    PropertySymbol globalVariable1 = new PropertySymbol("var1", stringReference);
    topScope.add(globalVariable1);

    assertSame(variable, method.getSpannedScope().resolve("var1", PropertySymbol.KIND).get());
    
  }
  
  @Test
  public void testResolveLocallyAllOfOneKind() {
    /**
     * 
     * class C {
     *   int i;
     *   int j;
     *   
     *   void m() {
     *     int f;
     *   }
     * 
     * }
     */

    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();
    resolvingFilters.add(CommonResolvingFilter.create(EntitySymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(PropertySymbol.KIND));
    resolvingFilters.add(CommonResolvingFilter.create(ActionSymbol.KIND));
    
    EntitySymbol clazz = new EntitySymbol("C");
    ((MutableScope)clazz.getSpannedScope()).setResolvingFilters(resolvingFilters);
    
    PropertySymbol i = new PropertySymbol("i", intReference);
    clazz.addProperty(i);
    
    PropertySymbol j = new PropertySymbol("j", intReference);
    clazz.addProperty(j);
    
    ActionSymbol m = new ActionSymbol("m");
    clazz.addAction(m);
    
    PropertySymbol f = new PropertySymbol("f", intReference);
    m.addVariable(f);

    // all fields
    assertEquals(2, clazz.getProperties().size());
    assertTrue(clazz.getProperties().contains(i));
    assertTrue(clazz.getProperties().contains(j));
    
    // all methods
    assertEquals(1, clazz.getActions().size());
    assertTrue(clazz.getActions().contains(m));
  }

}
