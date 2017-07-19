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
package de.se_rwth.langeditor.global;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;
import de.se_rwth.langeditor.util.Misc;

public class Startup implements IStartup {
  
  @Override
  public void earlyStartup() {
    Log.enableFailQuick(false);
    
    ModelStateAssembler assembler = DIService.getInstance(ModelStateAssembler.class);
    assembler.scheduleFullRebuild();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
      if (event != null && event.getResource() instanceof IStorage) {
        IStorage storage = (IStorage) event.getResource();
        IProject project = Misc.getProject(storage);
        String content = Misc.getContents(storage);
        assembler.scheduleRebuild(storage, project, content);
      }
    });
  }
}
