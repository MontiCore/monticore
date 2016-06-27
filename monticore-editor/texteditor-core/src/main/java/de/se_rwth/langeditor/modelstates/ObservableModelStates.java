/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
package de.se_rwth.langeditor.modelstates;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.resources.IStorage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;

@Singleton
public class ObservableModelStates {
  
  private final Set<ModelState> modelStates = new HashSet<>();
  
  private final Set<Consumer<ModelState>> globalObservers = new HashSet<>();
  
  private final Multimap<IStorage, Consumer<ModelState>> storageObservers = HashMultimap.create();
  
  public void acceptModelState(ModelState modelState) {
    removePreviousState(modelState);
    modelStates.add(modelState);
    globalObservers.forEach(observer -> observer.accept(modelState));
    storageObservers.get(modelState.getStorage()).forEach(observer -> observer.accept(modelState));
  }
  
  public Set<ModelState> getModelStates() {
    return modelStates;
  }
  
  public void addGlobalObserver(Consumer<ModelState> observer) {
    globalObservers.add(observer);
  }
  
  public void addStorageObserver(IStorage storage, Consumer<ModelState> observer) {
    storageObservers.put(storage, observer);
  }
  
  public Optional<ModelState> findModelState(IStorage storage) {
    return modelStates.stream()
        .filter(modelState -> modelState.getStorage().equals(storage))
        .findFirst();
  }
  
  public void removeStorageObservers(IStorage storage) {
    storageObservers.removeAll(storage);
  }
  
  private void removePreviousState(ModelState modelState) {
    modelStates.stream()
        .filter(previousModelState -> {
          boolean sameStorage = previousModelState.getStorage().equals(modelState.getStorage());
          boolean sameProject = previousModelState.getProject().equals(modelState.getProject());
          return sameStorage && sameProject;
        })
        .findFirst()
        .ifPresent(modelStates::remove);
  }
  
}
