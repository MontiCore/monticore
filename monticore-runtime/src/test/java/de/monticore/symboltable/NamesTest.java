/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Optional;

import de.monticore.symboltable.mocks.languages.JTypeSymbolMock;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class NamesTest {

  @Test
  public void testDeterminePackageName() {
    ArtifactScope as = new ArtifactScope(Optional.<MutableScope>empty(), "p", new ArrayList<>());

    JTypeSymbolMock c = new JTypeSymbolMock("C");

    as.add(c);
    // Package name is now calculated
    assertEquals("p", c.getPackageName());

    JTypeSymbolMock d = new JTypeSymbolMock("D");
    c.addInnerType(d);
    // package name in a model (resp. artifact) should be the same for all symbols
    assertEquals("p", d.getPackageName());
  }

  @Test
  public void testDeterminePackageNameWhereEnclosingNameIsSetManually() {
    ArtifactScope as = new ArtifactScope(Optional.<MutableScope>empty(), "p", new ArrayList<>());

    JTypeSymbolMock c = new JTypeSymbolMock("C");
    as.add(c);

    c.setPackageName("q");
    assertEquals("q", c.getPackageName());

    JTypeSymbolMock d = new JTypeSymbolMock("D");
    c.addInnerType(d);
    // package name is same as the package name of its enclosing class
    assertEquals("q", d.getPackageName());

  }

  @Test
  public void testDetermineFullName() {
    ArtifactScope as = new ArtifactScope(Optional.<MutableScope>empty(), "p", new ArrayList<>());

    JTypeSymbolMock c = new JTypeSymbolMock("C");
    as.add(c);

    assertEquals("p.C", c.getFullName());

    JTypeSymbolMock d = new JTypeSymbolMock("D");
    c.addInnerType(d);
    assertEquals("p.C.D", d.getFullName());
  }

  @Test
  public void testDetermineFullNameWhereEnclosingNameIsSetManually() {
    ArtifactScope as = new ArtifactScope(Optional.<MutableScope>empty(), "p", new ArrayList<>());

    JTypeSymbolMock c = new JTypeSymbolMock("C");
    as.add(c);

    c.setFullName("q.Foo");
    assertEquals("q.Foo", c.getFullName());

    JTypeSymbolMock d = new JTypeSymbolMock("D");
    c.addInnerType(d);
    assertEquals("q.Foo.D", d.getFullName());
  }



}
