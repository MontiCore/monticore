/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.gradle.dependencies.MCSourceSets;
import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

import java.util.Map;

/**
 * This plugin sets-up a MontiCore project for an optional TR setup
 * This is done by:
 * - adding a trafoGrammar configuration
 * - adding a dependency to the default project to the trafoGrammar configuration
 * No work is done when configuring this plugin
 */
@SuppressWarnings("unused")
public class MCGeneratorWithTRSetupPlugin implements Plugin<Project> {

  private final static String TR_PROPERTY = "genTR";

  public void apply(Project project) {
    // Create a new "trafo" source set
    SourceSet trafoSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
            .getSourceSets().maybeCreate("trafo");

    // Add the main grammars (as files) to the trafoGrammar-symbol-dependencies
    project.getConfigurations().named(MCSourceSets.getSymbolDependencyConfigName(trafoSourceSet))
            .configure(trafoGrammar -> {
              SourceSet main = project.getExtensions().getByType(JavaPluginExtension.class)
                      .getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
              FileCollection grammarSrc = MCGrammarsSourceDirectorySet.getGrammars(main).getSourceDirectories();
              Dependency localFilesDependency = project.getDependencies().create(grammarSrc);
              trafoGrammar.getDependencies().add(localFilesDependency);
            });

    project.getConfigurations().named(trafoSourceSet.getImplementationConfigurationName())
            .configure(configuration -> {
              // Add an implementation dependency to the main project
              Dependency mainDependency = project.getDependencies().project(Map.of("path", project.getPath()));
              configuration.getDependencies().add(mainDependency);
            });

  }

}
