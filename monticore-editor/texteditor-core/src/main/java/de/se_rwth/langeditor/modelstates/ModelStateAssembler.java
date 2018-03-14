/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelstates;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.langeditor.global.LanguageLocator;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.modelstates.ModelState.ModelStateBuilder;
import de.se_rwth.langeditor.util.Misc;
import de.se_rwth.langeditor.util.ResourceLocator;

@Singleton
public class ModelStateAssembler {
  
  private final ScheduledExecutorService executor;
  
  private final LanguageLocator locator;
  
  private final ObservableModelStates observableModelStates;
  
  private final ConcurrentHashMap<IStorage, ScheduledFuture<?>> scheduledRebuilds = new ConcurrentHashMap<>();
  
  private final Nodes nodes;
  
  @Inject
  public ModelStateAssembler(
      ScheduledExecutorService executor,
      LanguageLocator locator,
      ObservableModelStates observableModelStates,
      Nodes nodes) {
    this.executor = executor;
    this.locator = locator;
    this.observableModelStates = observableModelStates;
    this.nodes = nodes;
  }
  
  public void scheduleRebuild(IStorage storage, IProject project, String content) {
    ScheduledFuture<?> scheduledRebuild = scheduledRebuilds.get(storage);
    if (scheduledRebuild != null) {
      scheduledRebuild.cancel(false);
    }
    Runnable runnable = () -> {
      try {
        rebuildModelState(storage, project, content);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    };
    scheduledRebuilds.put(storage, executor.schedule(runnable, 1, TimeUnit.SECONDS));
  }
  
  public void scheduleProjectRebuild(IProject project) {
    Set<IStorage> storages = Maps.filterValues(ResourceLocator.getStorages(),
        (projectValue -> projectValue == project)).keySet();
    
    storages.stream()
        .map(scheduledRebuilds::get)
        .filter(scheduledRebuild -> scheduledRebuild != null)
        .forEach(scheduledRebuild -> scheduledRebuild.cancel(false));
    
    executor.execute(() -> {
      try {
        rebuildProject(project, storages);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
  
  public void scheduleFullRebuild() {
    for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      scheduleProjectRebuild(project);
    }
  }
  
  private synchronized void rebuildModelState(IStorage storage, IProject project, String content) {
    Optional<Language> findLanguage = locator.findLanguage(storage);
    findLanguage.ifPresent(language -> {
      ModelState modelState = createNewModelState(storage, project, content, language);
      if (modelState.isLegal()) {
        language.buildModel(modelState);
      }
      observableModelStates.acceptModelState(modelState);
    });
  }
  
  private synchronized void rebuildProject(IProject project, Set<IStorage> storages) {
    for (Language language : locator.getLanguages()) {
      Set<ModelState> modelStates = storages.stream()
          .filter(storage -> locator.findLanguage(storage).map(language::equals).orElse(false))
          .map(storage ->
              createNewModelState(storage, project, Misc.getContents(storage), language))
          .collect(Collectors.toSet());
      
      ImmutableSet<ModelState> legalModelStates = ImmutableSet.copyOf(modelStates.stream()
          .filter(ModelState::isLegal)
          .collect(Collectors.toSet()));
      
      language.buildProject(project, legalModelStates, ResourceLocator.assembleModelPath(project));
      modelStates.forEach(observableModelStates::acceptModelState);
    }
  }
  
  private ModelState createNewModelState(
      IStorage storage, IProject project, String content, Language language) {
    
    ImmutableMultimap.Builder<SourcePosition, String> syntaxErrorBuilder = ImmutableMultimap.builder();
    
    ParserRuleContext rootContext = language.getParserConfig().parse(content,
        (pos, message) -> syntaxErrorBuilder.put(
            pos, message));
    
    nodes.addNodes(rootContext);
    
    return new ModelStateBuilder()
        .setStorage(storage)
        .setProject(project)
        .setContent(content)
        .setLanguage(language)
        .setRootNode(Nodes.getAstNode(rootContext).get())
        .setRootContext(rootContext)
        .setSyntaxErrors(syntaxErrorBuilder.build())
        .setLastModelState(observableModelStates.findModelState(storage).orElse(null))
        .build();
  }
}
