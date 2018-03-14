/* (c) https://github.com/MontiCore/monticore */
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
