/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import de.monticore.gradle.StatisticListener
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicBoolean

/**
 * This class realizes the plugin itself.
 * The plugin is only used to provide task types
 * MCTask and GroovyTask but no predefined task instances
 */
public class MCPlugin implements Plugin<Project> {

  public void apply(Project project) {
    project.ext.MCTask = de.monticore.MCTask
    project.ext.MontiTransExec = de.monticore.MontiTransExec
    project.configurations.create("grammar")

    if(!project.getPlugins().hasPlugin(this.getClass())){
      StatisticListener.registerOnce(project);
    }

  }
}
