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
package de.monticore.mojo.m2e;

import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

/**
 * An {@link MojoExecutionBuildParticipant} for the monticore-maven-plugin.
 * 
 * @author (last commit) $Author: ahorst $
 * 2013) $
 */
public class MontiCoreBuildParticipant extends MojoExecutionBuildParticipant {
  
  /**
   * Constructor for de.monticore.mojo.m2e.MontiCoreBuildParticipant
   * 
   * @param execution
   */
  public MontiCoreBuildParticipant(MojoExecution execution) {
    super(execution, true);
  }
  
  /**
   * @see org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant#build(int,
   * org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
    return super.build(kind, monitor);
  }
  
}
