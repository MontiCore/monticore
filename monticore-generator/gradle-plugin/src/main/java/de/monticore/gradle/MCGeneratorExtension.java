/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import org.gradle.api.DomainObjectCollection;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

/**
 * This extension (to a {@link Project}) allows further configuration of the MC Generator plugin
 * <p>
 * ```
 * monticoreGenerator {
 * // publish the MC artifacts of the "trafo" source set
 * publishSourceSet(project.sourceSets.trafo)
 * }
 * ```
 */
public interface MCGeneratorExtension {

  /**
   * @return The (non-main) source sets which should also be published
   */
  DomainObjectCollection<SourceSet> getPublishedSourceSets();

  /**
   * Publish additional grammars of a source set
   * The published artifact is suffixed with "-${sourceSetName}"
   *
   * @param sourceSet the source set whose grammars should be published
   */
  default void publishSourceSet(SourceSet sourceSet) {
    getPublishedSourceSets().add(sourceSet);
  }

  class DefaultMCGeneratorExtension implements MCGeneratorExtension {

    private final DomainObjectCollection publishedSourceSets;

    public DefaultMCGeneratorExtension(DomainObjectCollection publishedSourceSets) {
      this.publishedSourceSets = publishedSourceSets;
    }

    @Override
    public DomainObjectCollection<SourceSet> getPublishedSourceSets() {
      return this.publishedSourceSets;
    }

  }
}
