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

package de.monticore.generating.templateengine.freemarker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * Builder for FreeMarker configuration (see {@link Configuration}).
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class FreeMarkerConfigurationBuilder {
  
  private ClassLoader classLoader = getClass().getClassLoader();
  
  private List<File> additionalTemplatePaths = new ArrayList<>();
  
  private Collection<TemplateAutoImport> autoImports = new ArrayList<>();
  
  public FreeMarkerConfigurationBuilder classLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
    return this;
  }
  
  public FreeMarkerConfigurationBuilder additionalTemplatePaths(
      List<File> additionalTemplatePaths) {
    this.additionalTemplatePaths = additionalTemplatePaths;
    return this;
  }
  
  public FreeMarkerConfigurationBuilder autoImports(Collection<TemplateAutoImport> autoImports) {
    this.autoImports = autoImports;
    return this;
  }
  
  public Configuration build() {
    Configuration config = new Configuration();
    
    config.setObjectWrapper(new DefaultObjectWrapper());
    // don't look for templateName.de.ftl when templateName.ftl requested
    config.setLocalizedLookup(false);
    
    MontiCoreTemplateLoader mcClassLoader = new MontiCoreTemplateLoader(classLoader);
    
    if (additionalTemplatePaths.isEmpty()) {
      config.setTemplateLoader(mcClassLoader);
    }
    else {
      List<TemplateLoader> loaders = new ArrayList<>();
      
      // this loader is added first such that all "built-in" templates located on the classpath are
      // taking precedence over any other templates
      loaders.add(mcClassLoader);
      
      for (File file : additionalTemplatePaths) {
        // add file loaders. IO checks by FileTemplateLoader constructor
        try {
          loaders.add(new MontiCoreFileTemplateLoader(file));
        }
        catch (IOException e) {
          Log.error("0xA1020 Unable to load templates from path " + file.getPath(), e);
        }
      }
            
      config.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders
          .size()])));
    }
    
    for (TemplateAutoImport i : autoImports) {
      config.addAutoImport(i.getNamespaceHash(), i.getTemplatePath());
    }
    
    config.setTemplateExceptionHandler(new MontiCoreTemplateExceptionHandler(
        MontiCoreTemplateExceptionHandler.THROW_ERROR));
        
    return config;
  }
  
}
