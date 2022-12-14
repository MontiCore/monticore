/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.gradle.StatisticListener;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * This class realizes the plugin itself.
 * The plugin is only used to provide task types
 * MCTask and GroovyTask but no predefined task instances
 */
public class MCPlugin implements Plugin<Project> {

  public static final String GRAMMAR_CONFIGURATION_NAME = "grammar";

  public void apply(Project project) {
    project.getExtensions().getExtraProperties().set("MCTask", de.monticore.MCTask.class);
    project.getExtensions().getExtraProperties().set("MontiTransExec", de.monticore.MontiTransExec.class);
    project.getConfigurations().create(GRAMMAR_CONFIGURATION_NAME);

    StatisticListener.registerOnce(project);
  }
}
