/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.dependencies;

import de.monticore.gradle.MCGeneratorExtension;
import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Publishes the main sourceset and exposes the {@link MCGeneratorExtension},
 * allowing the publishing of further source sets.
 */
public class MCPublishingPlugin implements Plugin<Project> {

  public static final String GRAMMARS_BASE_CLASSIFIER = "grammars";

  /**
   * Attribute to differentiate between outgoing configuratons of multiple source sets
   */
  protected static final Attribute GRAMMAR_SOURCE_SET_ATTRIBUTE = Attribute.of("monticore.generator.sourceset", String.class);

  final SoftwareComponentFactory softwareComponentFactory;
  final TaskDependencyFactory taskDependencyFactory;

  @Inject
  public MCPublishingPlugin(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    this.softwareComponentFactory = softwareComponentFactory;
    this.taskDependencyFactory = taskDependencyFactory;
  }

  @Override
  public void apply(@Nonnull Project project) {
    project.getPluginManager().apply(MCDependenciesPlugin.class);
    project.getPluginManager().apply("java-library");

    project.getPluginManager().withPlugin("java", appliedPlugin -> {
      project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().all(sourceSet -> {
        if (SourceSet.isMain(sourceSet)) {
          setupGrammarsPublication(sourceSet, project);
        }
      });

      MCGeneratorExtension ext = project.getExtensions().create(
              MCGeneratorExtension.class,
              "monticoreGenerator",
              MCGeneratorExtension.DefaultMCGeneratorExtension.class,
              project.getObjects().domainObjectContainer(SourceSet.class)
      );

      // In case only alias publications are set-up, Gradle fails with a cryptic:
      // Failed to query the value of property 'dependencies'. java.util.NoSuchElementException (no error message)
      project.afterEvaluate(evaluatedProject -> {
        // Thus, we throw a human comprehensible error that a default (non-alias) publication should be configured
        if (!ext.getPublishedSourceSets().isEmpty()) { // but only if MC source sets should be published
          @Nullable PublishingExtension publExt = project.getExtensions().findByType(PublishingExtension.class);
          if (publExt == null) {
            doError(evaluatedProject, "Publishing of grammars from source sets requires the maven-publish plugin to be applied first!");
            return;
          }

          if (publExt.getPublications()
                  .matching(publ -> (publ instanceof DefaultMavenPublication && !((DefaultMavenPublication) publ).isAlias()))
                  .isEmpty()) {
            doError(evaluatedProject, "Unable to publish from MC source sets "
                    + ext.getPublishedSourceSets().stream().map(SourceSet::getName).collect(Collectors.toList())
                    + " without a default publication (using the maven-publish plugin) being configured");
          }
        }
      });

      // Set-up publishing of additional source sets
      ext.getPublishedSourceSets().all(sourceSet -> {
        if (SourceSet.isMain(sourceSet)) {
          doError(project, "SourceSet (" + project.getName() + ") " + sourceSet.getName() + " is the main source set - Use traditional maven-publish instead!");
          return;
        }
        // Require the maven-publish plugin to be applied
        if (!project.getPluginManager().hasPlugin("maven-publish")) {
          doError(project, "Publishing of MontiCore source sets requires the maven-publish plugin to be applied first");
          return;
        }
        setupGrammarsPublication(sourceSet, project);

        String grammarsJarTaskName = sourceSet.getTaskName(null, GRAMMARS_BASE_CLASSIFIER + "Jar");
        // And configure the source set specific grammars jar task to use the grammars source directory set
        project.getTasks().named(grammarsJarTaskName, Jar.class)
                .configure(jar -> jar.from(MCGrammarsSourceDirectorySet.getGrammars(sourceSet)));

      });
    });
  }

  /**
   * Set-up the publication of the grammars jar of a source set
   */
  public void setupGrammarsPublication(SourceSet sourceSet, Project project) {
    // a consumable config containing the grammar-files
    Configuration outgoingGrammarsConfig = addSymbolDependenciesConfig(sourceSet, project);
    // a task creating the jar (containing the grammars)
    TaskProvider<Jar> grammarsJarTask = createGrammarsJarTask(sourceSet, project);


    // The artifact created from the grammars task
    PublishArtifact grammarsJarArtifact = createPublishedArtifact(grammarsJarTask, project);

    setUpPublicationOf(grammarsJarArtifact, outgoingGrammarsConfig, project, sourceSet);

    // let the outgoing, published configuration extend from the declaring configuration (e.g., grammar)
    linkDeclaredDependenciesToOutgoingConfiguration(sourceSet, project);

    if (!SourceSet.isMain(sourceSet)) {
      // Add an extra attribute to the compile classpath configuration
      // to be able to differentiate between sourcesets.
      // Skip the main source set, as this is done by the java-library plugin
      project.getConfigurations().getByName(sourceSet.getCompileClasspathConfigurationName()).attributes(it -> {
        it.attribute(GRAMMAR_SOURCE_SET_ATTRIBUTE, sourceSet.getName());
      });
    }
  }


  protected void setUpPublicationOf(PublishArtifact grammarsJarArtifact, Configuration outgoingGrammarsConfig, Project project, SourceSet sourceSet) {
    // Add the grammars artifact to the published-by-default artifact set
    project.getExtensions().getByType(DefaultArtifactPublicationSet.class)
            .addCandidate(grammarsJarArtifact);

    AdhocComponentWithVariants component;

    // Add the grammar jar as a variant to the main java component
    if (SourceSet.isMain(sourceSet)) {
      component = ((AdhocComponentWithVariants) project.getComponents().getByName("java"));
    } else {
      // Unless it originates from a non-main source set
      // Then we create a new component
      component = softwareComponentFactory.adhoc(GRAMMARS_BASE_CLASSIFIER + sourceSet.getName());
      project.getComponents().add(component);
    }

    // And add the outgoing configuration as a variant (with the grammars attributes) of the component
    // when using maven, the jar is now an optional dependency
    component.addVariantsFromConfiguration(outgoingGrammarsConfig, ConfigurationVariantDetails::mapToOptional);

    // And add the jar artifact
    outgoingGrammarsConfig.getOutgoing().getArtifacts().add(grammarsJarArtifact);

    // The main source set (and its main component) do not require further work, as the java-library plugin does the work for us
    if (!SourceSet.isMain(sourceSet)) {
      setupNonMainPublish(grammarsJarArtifact, project, sourceSet, component);
    }
  }

  /**
   * Create the Jar tasks for non-main source sets and configure them for publication
   */
  protected void setupNonMainPublish(PublishArtifact grammarsJarArtifact, Project project, SourceSet sourceSet, AdhocComponentWithVariants component) {
    if (Objects.toString(project.getGroup()).isEmpty()) {
      doError(project, "Unable to publish MC-source set " + sourceSet.getName() + " due to no group being set");
    }

    // Similar to the java feature, we a source set specific Jar and sourcesJar task
    TaskProvider<Jar> jarTask = createJarTask(sourceSet, project);
    TaskProvider<Jar> sourcesJarTask = createSourcesJarTask(sourceSet, project);

    // And their published artifacts
    PublishArtifact jarArtifact = createPublishedArtifact(jarTask, project);
    PublishArtifact sourcesJarArtifact = createPublishedArtifact(sourcesJarTask, project);

    // Next, we add a runtime variant from the runtime classpath configuration (and add the jar Artifact)
    Configuration runtimeClasspathConfig = project.getConfigurations().getByName(sourceSet.getRuntimeClasspathConfigurationName());
    component.addVariantsFromConfiguration(runtimeClasspathConfig, new JavaConfigurationVariantMapping("runtime", false));
    runtimeClasspathConfig.getOutgoing().getArtifacts().add(jarArtifact);

    // Next, we add a separate documentation/sources variant
    Configuration sourcesElementsConfig = createDocumentationConfig(project, sourceSet, DocsType.SOURCES);
    // and add the sources artifact to this config
    sourcesElementsConfig.getOutgoing().getArtifacts().add(sourcesJarArtifact);
    // Then, add a variant to the component, as otherwise no variant is published
    component.addVariantsFromConfiguration(sourcesElementsConfig, new JavaConfigurationVariantMapping("compile", true));

    // Next, we add also a compile variant from the compile classpath configuration (and add both outgoing jar artifacts)
    Configuration compileClasspathConfig = project.getConfigurations().getByName(sourceSet.getCompileClasspathConfigurationName());
    component.addVariantsFromConfiguration(compileClasspathConfig, new JavaConfigurationVariantMapping("compile", false));
    compileClasspathConfig.getOutgoing().getArtifacts().add(jarArtifact);

    // Ensure maven-publish is applied
    @Nullable PublishingExtension publExt = project.getExtensions().findByType(PublishingExtension.class);
    if (publExt == null) {
      doError(project, "Publishing using the MontiCore Generator plugin requires the maven-publish plugin to be applied first!");
      return;
    }

    // Set up a Maven publication for non-main source sets
    publExt
            .getPublications().create(sourceSet.getName(), MavenPublication.class, mavenPublication -> {
              // And append the source set name as an appendix to the artifact id
              mavenPublication.setArtifactId(project.getName() + "-" + sourceSet.getName());
              mavenPublication.setGroupId(project.getGroup().toString());
              // version is set implicitly

              // and add all three jars as an artifact
              mavenPublication.getArtifacts().artifact(grammarsJarArtifact);
              mavenPublication.getArtifacts().artifact(jarArtifact);
              mavenPublication.getArtifacts().artifact(sourcesJarArtifact);

              // Next, provide the source set specific component (for the Gradle module system)
              mavenPublication.from(component);

              // The publication should not be considered when converting project dependencies to published metadata
              // avoids:
              // Publishing is not able to resolve a dependency on a project with multiple publications that have different coordinates
              ((DefaultMavenPublication) mavenPublication).setAlias(true);
            });
  }

  /**
   * Create a lazy {@link org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact} from a given
   * {@link Jar} task.
   * Provides compatibility between gradle 7 and 8
   */
  protected PublishArtifact createPublishedArtifact(TaskProvider<Jar> grammarsJarTask, Project project) {
    // the LazyPublishArtifact constructor has changed between gradle versions:
    try {
      // Gradle 7.4.2
      return new LazyPublishArtifact(grammarsJarTask, null, ((ProjectInternal) project).getFileResolver());
    } catch (NoSuchMethodError incompatibleVersion) {
      try {
        // Gradle 8 - the TaskDependencyFactory parameter was added
        return LazyPublishArtifact.class.getConstructor(Provider.class, String.class, FileResolver.class, TaskDependencyFactory.class)
                .newInstance(grammarsJarTask, null, ((ProjectInternal) project).getFileResolver(), taskDependencyFactory);
      } catch (ReflectiveOperationException e) {
        throw new IllegalStateException("Incompatible LazyPublishArtifact constructor in gradle " + project.getGradle().getGradleVersion(), e);
      }
    }
  }


  protected Configuration addSymbolDependenciesConfig(SourceSet sourceSet, Project project) {
    Configuration config = project.getConfigurations().maybeCreate(MCSourceSets.getOutgoingSymbolConfigName(sourceSet));
    config.setCanBeConsumed(true); // this config is published
    config.setCanBeResolved(false);
    config.setDescription("Published grammars of source set " + sourceSet.getName());

    // Configure the attributes
    MCSourceSets.addSymbolJarAttributes(config, project);
    // For non-main sourcesets, add an extra attribute to differentiate between them
    // (Consumable configurations with identical capabilities within a project (other than the default configuration) must have unique attributes)
    if (!SourceSet.isMain(sourceSet)) {
      config.attributes(it -> {
        it.attribute(GRAMMAR_SOURCE_SET_ATTRIBUTE, sourceSet.getName());
      });
    }
    return config;
  }

  /**
   * Creates a jar task that should package the symbols produced by compiling the models of the source set into a jar.
   * However, the jar task is not configured to contain anything. This is ought to be done by a later plugin.
   */
  protected TaskProvider<Jar> createGrammarsJarTask(SourceSet sourceSet, Project project) {
    return createJarTaskPartial(sourceSet, project, GRAMMARS_BASE_CLASSIFIER + "Jar",
            jar -> {
              // Set the archive classifier (appended to the source set name)
              jar.getArchiveClassifier().set(GRAMMARS_BASE_CLASSIFIER);
            });
  }

  protected TaskProvider<Jar> createJarTask(SourceSet sourceSet, Project project) {
    return createJarTaskPartial(sourceSet, project, "Jar",
            jar -> {
              jar.from(sourceSet.getOutput());
            });

  }

  protected TaskProvider<Jar> createSourcesJarTask(SourceSet sourceSet, Project project) {
    return createJarTaskPartial(sourceSet, project, "SourcesJar",
            jar -> {
              jar.from(sourceSet.getJava());
              jar.getArchiveClassifier().set("sources");
            });
  }

  /**
   * Create a new Jar task on a given source set,
   * set its archive appendix (if non-main),
   * and add it to the assemble task dependencies.
   * Further configurations MUST be done using the consumer
   */
  protected TaskProvider<Jar> createJarTaskPartial(SourceSet sourceSet, Project project, String target, Consumer<Jar> c) {
    TaskProvider<Jar> jarTask = project.getTasks().register(sourceSet.getTaskName(null, target), Jar.class,
            jar -> {
              jar.setGroup("build");
              // And for non main source sets, also the archive appendix
              if (!SourceSet.isMain(sourceSet))
                jar.getArchiveAppendix().set(sourceSet.getName());
              // further configure it via the consumer
              c.accept(jar);
            });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(jarTask));

    return jarTask;
  }

  /**
   * Creates an outgoing configuration containing the sources of a SourceSet (if it does not already exist)
   */
  protected Configuration createDocumentationConfig(Project project, SourceSet sourceSet, String docsType) {
    Configuration sourcesElementsConfig = project.getConfigurations().maybeCreate(sourceSet.getSourcesElementsConfigurationName());
    sourcesElementsConfig.setCanBeConsumed(true);
    sourcesElementsConfig.setVisible(false);
    sourcesElementsConfig.setCanBeResolved(false);
    sourcesElementsConfig.setDescription(docsType + " elements for " + sourceSet.getName());

    sourcesElementsConfig.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME));
    sourcesElementsConfig.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.DOCUMENTATION));
    sourcesElementsConfig.getAttributes().attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
    sourcesElementsConfig.getAttributes().attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.getObjects().named(DocsType.class, docsType));

    return sourcesElementsConfig;
  }

  /**
   * Asserts that declared dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the outgoing configuration of the given [SourceSet] extend from
   * it's `grammars` configuration.
   *
   * @param sourceSet the [SourceSet] whose symbols/grammars should be published and for which this method will
   *                  add the transitive dependencies.
   */
  protected void linkDeclaredDependenciesToOutgoingConfiguration(SourceSet sourceSet, Project project) {
    Configuration declaringDependencyConfig = project.getConfigurations().getByName(MCSourceSets.getDependencyDeclarationConfigName(sourceSet));
    Configuration outgoingConfiguration = project.getConfigurations().getByName(MCSourceSets.getOutgoingSymbolConfigName(sourceSet));

    outgoingConfiguration.extendsFrom(declaringDependencyConfig);
  }

  /**
   * Log an error and abort the gradle build process (by throwing a GradleException)
   */
  protected void doError(Project project, String msg) {
    project.getLogger().error(msg);
    throw new GradleException(msg);
  }
}
