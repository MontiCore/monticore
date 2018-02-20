/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelstates;

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
