/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.sources;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.lambdas.SerializableLambdas;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

import javax.annotation.Nonnull;

/**
 * Set up the grammars-source-directory per source-set
 * ```
 * sourceSets {
 * main {
 * grammars {
 * srcDir("src/main/grammars")
 * }
 * }
 * }
 * ```
 */
public class MCSourcesPlugin implements Plugin<Project> {
  @Override
  public void apply(@Nonnull Project target) {
    target.getPluginManager().apply("java-base");
    target.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
      addSourceSetExtension(sourceSet, target);
    });
  }

  /**
   * Adds the "grammar" extension to every source set
   */
  protected void addSourceSetExtension(SourceSet sourceSet, Project project) {
    SourceDirectorySet grammarsSrcSet = project.getObjects().sourceDirectorySet("grammars", sourceSet.getName() + " MontiCore grammars source");

    MCGrammarsSourceDirectorySet mcSrcSet = sourceSet.getExtensions().create(
            MCGrammarsSourceDirectorySet.class,
            MCGrammarsSourceDirectorySet.GRAMMARS,
            MCGrammarsSourceDirectorySet.DefaultMCGrammarsSourceDirectorySet.class,
            grammarsSrcSet);


    // By default, output into a generated/test-${NonMainName}sources/monticore/sourcecode directory
    String buildDir = "generated-" + (SourceSet.isMain(sourceSet) ? "" : sourceSet.getName()) + "sources/monticore/sourcecode";

    Provider<Directory> destinationDir = project.getLayout().getBuildDirectory().dir(buildDir);
    mcSrcSet.getDestinationDirectory().convention(destinationDir);

    // Use the src/${sourcesetname}/grammars as an input by default
    mcSrcSet.srcDir(project.file("src/" + sourceSet.getName() + "/grammars"));
    // and only work on mc4 and mlc files
    mcSrcSet.getFilter().include("**/*.mc4", "**/*.mlc");


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    FileCollection mcSrcSetCast = mcSrcSet;
    sourceSet.getResources().exclude(SerializableLambdas.spec(el -> mcSrcSetCast.contains(el.getFile())));
    sourceSet.getAllSource().source(mcSrcSet);

  }
}
