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

import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ScopeTest {

  @Test
  public void testInitEnclosingAndSubScopesUsingConstructors() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(enclosing));
    MutableScope sub2 = new CommonScope(Optional.of(enclosing));

    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testInitEnclosingAndSubScopesUsingAddSubScopeMethod() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(false);
    MutableScope sub2 = new CommonScope(false);

    assertEquals(0, enclosing.getSubScopes().size());
    assertFalse(sub1.getEnclosingScope().isPresent());
    assertFalse(sub2.getEnclosingScope().isPresent());

    enclosing.addSubScope(sub1);
    enclosing.addSubScope(sub2);

    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    // the enclosing scope is also set in the sub scopes
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());

    enclosing.addSubScope(sub1);
    // Nothing changes, because sub1 had already been added.
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testInitEnclosingAndSubScopesUsingSetEnclosingScopeMethod() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(false);
    MutableScope sub2 = new CommonScope(false);

    assertEquals(0, enclosing.getSubScopes().size());
    assertFalse(sub1.getEnclosingScope().isPresent());
    assertFalse(sub2.getEnclosingScope().isPresent());

    sub1.setEnclosingScope(enclosing);
    sub2.setEnclosingScope(enclosing);

    // the sub scopes are also added to the enclosing scopes
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());

    sub1.setEnclosingScope(enclosing);
    // Nothing changes, because the enclosing scope had already been set.
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testChangingEnclosingAndSubScopesUsingSetEnclosingMethod() {
    MutableScope oldEnclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(oldEnclosing));
    MutableScope sub2 = new CommonScope(Optional.of(oldEnclosing));

    assertEquals(2, oldEnclosing.getSubScopes().size());
    assertSame(sub1, oldEnclosing.getSubScopes().get(0));
    assertSame(sub2, oldEnclosing.getSubScopes().get(1));
    assertSame(oldEnclosing, sub1.getEnclosingScope().get());
    assertSame(oldEnclosing, sub2.getEnclosingScope().get());

    MutableScope newEnclosing = new CommonScope(false);

    sub1.setEnclosingScope(newEnclosing);

    // sub1 is now sub of newEnclosing...
    assertEquals(1, newEnclosing.getSubScopes().size());
    assertSame(sub1, newEnclosing.getSubScopes().get(0));
    assertSame(newEnclosing, sub1.getEnclosingScope().get());
    // ...and removed from oldEnclosing
    assertEquals(1, oldEnclosing.getSubScopes().size());
    assertSame(sub2, oldEnclosing.getSubScopes().get(0));
  }

  @Test
  public void testChangingEnclosingAndSubScopesUsingAddSubScopeMethod() {
    MutableScope oldEnclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(oldEnclosing));
    MutableScope sub2 = new CommonScope(Optional.of(oldEnclosing));

    assertEquals(2, oldEnclosing.getSubScopes().size());
    assertSame(sub1, oldEnclosing.getSubScopes().get(0));
    assertSame(sub2, oldEnclosing.getSubScopes().get(1));
    assertSame(oldEnclosing, sub1.getEnclosingScope().get());
    assertSame(oldEnclosing, sub2.getEnclosingScope().get());

    MutableScope newEnclosing = new CommonScope(false);

    newEnclosing.addSubScope(sub1);

    // sub1 is now sub of newEnclosing...
    assertEquals(1, newEnclosing.getSubScopes().size());
    assertSame(sub1, newEnclosing.getSubScopes().get(0));
    assertSame(newEnclosing, sub1.getEnclosingScope().get());
    // ...and removed from oldEnclosing
    assertEquals(1, oldEnclosing.getSubScopes().size());
    assertSame(sub2, oldEnclosing.getSubScopes().get(0));
  }

}
