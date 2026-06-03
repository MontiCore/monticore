/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.gradle.dependencies.MCSourceSets;
import de.monticore.gradle.gen.MCGenTask;
import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import java.util.Objects;
import java.util.Map;

/**
 * This plugin extends the {@link MCGeneratorPlugin} by also adding MontiTrans transformation languages
 * This is done by:
 * - extracting only the generated TR.mc4 files into a "trafo" source set
 * - setting the {@link MCGenTask#getGenDST()} property of the trafos MCGenTask
 * - adding a dependency to the default project to the trafoGrammar configuration
 * - exposing the trafoJar via a consumable "trafoOutElements" configuration
 * - publishing the trafo grammars, code & sources
 */
@SuppressWarnings("unused")
public class MCGeneratorWithTRPlugin implements Plugin<Project> {

  private final static String TR_PROPERTY = "genTR";

  public void apply(Project project) {
    project.getPlugins().apply(MCGeneratorPlugin.class);

    // in case a genTR property is present and not set to true, skip the TR setup
    if (project.getProperties().containsKey(TR_PROPERTY)
            && !"true".equals(Objects.toString(project.getProperties().get(TR_PROPERTY)))) {
      project.getLogger().info("Skipping MontiCore-TR setup due to " + TR_PROPERTY + " property being present & non-true");
      return;
    }

    // Output directory in which only the trafo grammars are extracted
    Provider<Directory> trafoGrammarDir = project.getLayout().getBuildDirectory()
            .dir("generated-sources/monticore/trafo");

    // Task, which copies/syncs (only) the trafo grammars into a separate directory
    // This is done, as a source set directory (e.g. sourceSets.grammars) can not be filtered by glob filters
    TaskProvider<Sync> extractTRTask = extractTRTask(project, trafoGrammarDir);

    // Create a new "trafo" source set
    SourceSet trafoSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
            .getSourceSets().maybeCreate("trafo");
    // with its grammars being the output of the extractTRTask filtering sync task
    trafoSourceSet.getExtensions().getByType(MCGrammarsSourceDirectorySet.class)
            .srcDir(extractTRTask);

    // Add the main grammars (as files) to the trafoGrammar-symbol-dependencies
    // (done in the WithTRSetup plugin)

    // Enable the DST flag
    project.getTasks().named("generateTrafoMCGrammars", MCGenTask.class).configure(it -> {
      it.getGenDST().set(true);
    });

    // Register a trafoJar task
    TaskProvider<Jar> trafoJarProvider = project.getTasks().register("trafoJar", Jar.class, trafoJar -> {
      trafoJar.getArchiveAppendix().set("trafo");
      trafoJar.from(trafoSourceSet.getOutput());
    });

    // Register a consumable configuration to share the trafoJar with other projects
    // https://docs.gradle.org/current/userguide/how_to_share_outputs_between_projects.html
    project.getConfigurations().consumable("trafoOutElements");
    project.getArtifacts().add("trafoOutElements", trafoJarProvider);

    // And mark the "trafo" source set for publishing
    // This bundles and publishes the generated TR artifacts separately as $projectname-trafo
    project.getPluginManager().withPlugin("maven-publish", appliedPlugin -> {
      project.getExtensions().getByType(MCGeneratorExtension.class)
              .publishSourceSet(trafoSourceSet);

      // Add a dependency to the main project itself to the outgoing trafo config
      Dependency mainDependency = project.getDependencies().project(Map.of("path", project.getPath()));
      project.getDependencies().add(MCSourceSets.getOutgoingSymbolConfigName(trafoSourceSet), mainDependency);
    });
  }

  protected TaskProvider<Sync> extractTRTask(Project project, Provider<Directory> trafoGrammarDir) {
    return project.getTasks().register("extractTRGrammars", Sync.class, sync -> {
      sync.from(project.getTasks().getByName("generateMCGrammars").getOutputs());
      sync.include("**/*TR.mc4"); // only include TR.mc4 files
      sync.setIncludeEmptyDirs(false);
      sync.into(trafoGrammarDir);
    });
  }


}
