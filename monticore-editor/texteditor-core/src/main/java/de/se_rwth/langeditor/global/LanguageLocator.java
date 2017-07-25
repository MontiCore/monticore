/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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
package de.se_rwth.langeditor.global;

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
