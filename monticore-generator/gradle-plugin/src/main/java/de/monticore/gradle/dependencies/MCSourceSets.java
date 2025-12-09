/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.dependencies;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.*;
import org.gradle.api.tasks.SourceSet;

public class MCSourceSets {

  public static final String MC_API_SYMBOL_USAGE = "monticore-grammar-api";

  /**
   * The name of the configuration which defines dependencies, but does not resolve them itself
   *
   * @see MCDependenciesPlugin#addDependencyDeclConfig(SourceSet, Project)
   */
  public static String getDependencyDeclarationConfigName(SourceSet sourceSet) {
    if (SourceSet.isMain(sourceSet)) return "grammar";
    return sourceSet.getName() + "Grammar";
  }

  /**
   * The name of the configuration which resolves the symbol dependencies
   *
   * @see MCDependenciesPlugin#addSymbolDependenciesConfig(SourceSet, Project)
   */
  public static String getSymbolDependencyConfigName(SourceSet sourceSet) {
    if (SourceSet.isMain(sourceSet)) return "grammarSymbolDependencies";
    return sourceSet.getName() + "GrammarSymbolDependencies";
  }

  /**
   * The name of the configuration which is published
   *
   * @see MCPublishingPlugin#addSymbolDependenciesConfig(SourceSet, Project)
   */
  public static String getOutgoingSymbolConfigName(SourceSet sourceSet) {
    if (SourceSet.isMain(sourceSet)) return "grammarSymbolOutElements";
    return sourceSet.getName() + "GrammarSymbolOutElements";
  }

  /**
   * Add the configuration-attributes for the symbol/grammars jar
   */
  public static void addSymbolJarAttributes(Configuration configuration, Project project) {
    configuration.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, MC_API_SYMBOL_USAGE));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

}
