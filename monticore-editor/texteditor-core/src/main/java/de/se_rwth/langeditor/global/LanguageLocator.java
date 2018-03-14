/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import java.util.Optional;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Singleton;

import de.se_rwth.langeditor.language.Language;

@Singleton
public class LanguageLocator {
  
  private final ImmutableSet<Language> languages;
  
  public LanguageLocator() {
    Builder<Language> builder = ImmutableSet.builder();
    
    IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
        .getConfigurationElementsFor(Constants.LANGUAGE_EXTENSION_POINT);
    
    for (IConfigurationElement configurationElement : configurationElements) {
      try {
        Language language = (Language) configurationElement
            .createExecutableExtension(Constants.LANGUAGE_EXTENSION_POINT_IMPLEMENTATION);
        builder.add(new ErrorCatchingLanguage(language));
      }
      catch (CoreException e) {
        throw new RuntimeException(e);
      }
    }
    languages = builder.build();
  }
  
  public Optional<Language> findLanguage(String extension) {
    return languages.stream()
        .filter(description -> description.getExtension().equals(extension))
        .findFirst();
  }
  
  public Optional<Language> findLanguage(IStorage storage) {
    return findLanguage(storage.getFullPath().getFileExtension());
  }
  
  public ImmutableSet<Language> getLanguages() {
    return languages;
  }
}
