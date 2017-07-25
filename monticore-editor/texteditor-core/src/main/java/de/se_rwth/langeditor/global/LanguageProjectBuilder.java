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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;

import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;

public class LanguageProjectBuilder extends CompilationParticipant {
  
  @Override
  public int aboutToBuild(IJavaProject javaProject) {
    DIService.getInstance(ModelStateAssembler.class).scheduleFullRebuild();
    return READY_FOR_BUILD;
  }
  
  @Override
  public boolean isActive(IJavaProject project) {
    return true;
  }
}
