/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
 *******************************************************************************/
package de.se_rwth.langeditor.injection;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

public final class ScopeImpl implements Scope {
  
  private final Table<Object, Key<?>, Object> objects = HashBasedTable.create();
  
  private Object context;
  
  public void enter(Object context) {
    this.context = context;
  }
  
  public void leave() {
    this.context = null;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
    return () -> {
      checkState(context != null, "Tried to retrieve " + key + " out of context");
      T object = (T) objects.get(context, key);
      if (object == null) {
        object = unscoped.get();
        objects.put(context, key, object);
      }
      return object;
    };
  }
}
