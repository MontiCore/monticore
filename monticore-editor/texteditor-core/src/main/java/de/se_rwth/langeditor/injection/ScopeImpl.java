/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.injection;

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
