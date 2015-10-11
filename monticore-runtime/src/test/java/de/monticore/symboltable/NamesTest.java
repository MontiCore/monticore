/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
