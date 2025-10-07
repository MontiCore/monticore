/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.sources;

import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.SourceSet;

import javax.annotation.Nonnull;

/**
 * A set of source files "compiled"/generated using the MCGenTask
 */
public interface MCGrammarsSourceDirectorySet extends SourceDirectorySet {

  /**
   * Constant of where this SourceDirectorySet can be found (similar to java or ressources)
   */
  final String GRAMMARS = "grammars";

  static MCGrammarsSourceDirectorySet getGrammars(@Nonnull SourceSet sourceSet) {
    return sourceSet.getExtensions().getByType(MCGrammarsSourceDirectorySet.class);
  }

  // Default implementation class
  class DefaultMCGrammarsSourceDirectorySet extends DefaultSourceDirectorySet implements MCGrammarsSourceDirectorySet {

    public DefaultMCGrammarsSourceDirectorySet(SourceDirectorySet sourceSet) {
      super(sourceSet);
    }
  }
}
