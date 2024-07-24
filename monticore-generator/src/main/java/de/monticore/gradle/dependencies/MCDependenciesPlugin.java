/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.dependencies;

import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import javax.annotation.Nonnull;


/**
 * Creates "grammar" configurations for each sourceSet,
 *  which allows depending on MontiCore grammars and their artifacts.
 *  Example usage:
 * ```
 * dependencies {
 * grammar("de.monticore:grammar:$mc_version")
 * testGrammar("de.monticore.lang:sd-language:$mc_version")
 * }
 * ```
 * Internally, further configurations are used.
 */
public class MCDependenciesPlugin implements Plugin<Project> {

  @Override
  public void apply(@Nonnull Project project) {
    project.getPluginManager().apply("java-library");

    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
      // a non-consumable nor resolveable grammars configuration to declare grammar dependencies
      Configuration declConfig = addDependencyDeclConfig(sourceSet, project);

      // a configuration which is added to the symbol path (which resolves the dependencies)
      Configuration symbolConfig = addSymbolDependenciesConfig(sourceSet, project);

      // The symbol configuration extends from the declaring grammars config (and resolves the grammar-jars)
      project.getConfigurations().named(MCSourceSets.getSymbolDependencyConfigName(sourceSet))
              .configure(configuration -> configuration.extendsFrom(declConfig));
      // The implementation configuration extends from the grammar configuration (and resolves the runtime-jars)
      project.getConfigurations().named(sourceSet.getImplementationConfigurationName())
              .configure(configuration -> configuration.extendsFrom(declConfig));

      // In case the source set is the main one, we also add the content of the grammar configuration to the api configuration
      if (SourceSet.isMain(sourceSet)) {
        // This ensures transitive compilation dependencies (such as the runtime)
        project.getConfigurations().named(sourceSet.getApiConfigurationName())
                .configure(configuration -> configuration.extendsFrom(declConfig));
      }

    });

    project.getPluginManager().withPlugin("java", appliedPlugin -> {
      // In case the java plugin is used, let testGrammars extend from the grammars configuration
      SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();

      // Fetch the testGrammar configuration
      project.getConfigurations().named(MCSourceSets.getDependencyDeclarationConfigName(sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)))
              .configure(testGrammars -> testGrammars.extendsFrom(
                      // Fetch the grammar configuration
                      project.getConfigurations().named(MCSourceSets.getDependencyDeclarationConfigName(sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME))).get())
              );

    });
  }

  /**
   * Creates a configuration used to declare dependencies on grammars and their implementation simultaneously.
   */
  protected Configuration addDependencyDeclConfig(SourceSet sourceSet, Project project) {
    Configuration config = project.getConfigurations().maybeCreate(MCSourceSets.getDependencyDeclarationConfigName(sourceSet));
    config.setCanBeConsumed(true); // can be consumed for inter-project dependencies
    config.setCanBeResolved(false); // can not be resolved - instead, the more specific configs resolve based on attributes
    config.setVisible(true);
    config.setDescription("Used to declare dependencies to other MontiCore grammar projects. " +
            "This will add both their java implementation and grammars artifacts to the respective configurations");

    return config;
  }

  protected Configuration addSymbolDependenciesConfig(SourceSet sourceSet, Project project) {
    Configuration config = project.getConfigurations().maybeCreate(MCSourceSets.getSymbolDependencyConfigName(sourceSet));
    // A non-resolved configuration
    config.setCanBeConsumed(false);
    // This configuration is added to the symbolpath/mcpath
    config.setCanBeResolved(true);
    config.setVisible(false);
    config.setDescription("Contains MontiCore grammar dependencies. For declaring dependencies, use the _grammars_ configuration instead.");

    // Configure the attributes
    MCSourceSets.addSymbolJarAttributes(config, project);

    return config;
  }
}
