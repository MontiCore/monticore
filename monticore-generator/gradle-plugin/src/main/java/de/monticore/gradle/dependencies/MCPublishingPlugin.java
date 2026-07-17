/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.dependencies;

import de.monticore.gradle.MCGeneratorExtension;
import de.monticore.gradle.common.APublishingPlugin;
import de.monticore.gradle.sources.MCGrammarsSourceDirectorySet;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.*;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * Publishes the main sourceset and exposes the {@link MCGeneratorExtension},
 * allowing the publishing of further source sets.
 */
public class MCPublishingPlugin extends APublishingPlugin implements Plugin<Project>  {

  public static final String GRAMMARS_BASE_CLASSIFIER = "grammars";

  /**
   * Attribute to differentiate between outgoing configuratons of multiple source sets
   */
  protected static final Attribute<String> GRAMMAR_SOURCE_SET_ATTRIBUTE = Attribute.of("monticore.generator.sourceset", String.class);

  final SoftwareComponentFactory softwareComponentFactory;

  @Inject
  public MCPublishingPlugin(SoftwareComponentFactory softwareComponentFactory,
                            TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
    this.softwareComponentFactory = softwareComponentFactory;
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
      project.getPluginManager().withPlugin("maven-publish", _p -> {
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
                      + ext.getPublishedSourceSets().stream().map(SourceSet::getName).toList()
                      + " without a default publication (using the maven-publish plugin) being configured");
            }
          }
        });
      });

      // Set-up publishing of additional source sets
      ext.getPublishedSourceSets().all(sourceSet -> {
        if (SourceSet.isMain(sourceSet)) {
          doError(project, "SourceSet (" + project.getName() + ") " + sourceSet.getName() + " is the main source set - Use traditional maven-publish instead!");
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

  /*
   * Set-up the publication of the grammars jar of a source set
   */
  public void setupGrammarsPublication(SourceSet sourceSet, Project project) {
    // a consumable config containing the grammar-files
    Configuration outgoingGrammarsConfig = addSymbolDependenciesConfig(sourceSet, project);
    // a task creating the jar (containing the grammars)
    TaskProvider<Jar> grammarsJarTask = createGrammarsJarTask(sourceSet, project);


    // The artifact created from the grammars task
    PublishArtifact grammarsJarArtifact = createPublishedArtifact(grammarsJarTask, project);

    preparePublicationOf(grammarsJarTask, grammarsJarArtifact, outgoingGrammarsConfig, project, sourceSet);

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


  protected void preparePublicationOf(TaskProvider<?> grammarsJarTask, PublishArtifact grammarsJarArtifact, Configuration outgoingGrammarsConfig, Project project, SourceSet sourceSet) {
    // Add the grammars artifact to the published-by-default artifact set
    project.getTasks().named("assemble").configure(et -> et.dependsOn(grammarsJarTask));

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
      prepareNonMainPublish(grammarsJarArtifact, project, sourceSet, component);
    }
  }

  /*
   * Create the Jar tasks for non-main source sets and configure them for publication
   */
  protected void prepareNonMainPublish(PublishArtifact grammarsJarArtifact, Project project, SourceSet sourceSet, AdhocComponentWithVariants component) {
    // Similar to the java feature, we add a source set specific Jar and sourcesJar task
    TaskProvider<Jar> jarTask = createJarTask(sourceSet, project);
    TaskProvider<Jar> sourcesJarTask = createSourcesJarTask(sourceSet, project);

    // And their published artifacts
    PublishArtifact jarArtifact = createPublishedArtifact(jarTask, project);
    PublishArtifact sourcesJarArtifact = createPublishedArtifact(sourcesJarTask, project);

    // Next, we add a runtime variant from the runtime classpath configuration (and add the jar Artifact)
    Configuration runtimeClasspathConfig = project.getConfigurations().getByName(sourceSet.getRuntimeClasspathConfigurationName());
    // component.addVariantsFromConfiguration is done via setupNonMainPublish
    runtimeClasspathConfig.getOutgoing().getArtifacts().add(jarArtifact);

    // Next, we add a separate documentation/sources variant
    Configuration sourcesElementsConfig = createDocumentationConfig(project, sourceSet, DocsType.SOURCES);
    // and add the sources artifact to this config
    sourcesElementsConfig.getOutgoing().getArtifacts().add(sourcesJarArtifact);
    // component.addVariantsFromConfiguration is done via setupNonMainPublish

    // Next, we add also a compile variant from the compile classpath configuration (and add both outgoing jar artifacts)
    Configuration compileClasspathConfig = project.getConfigurations().getByName(sourceSet.getCompileClasspathConfigurationName());
    // component.addVariantsFromConfiguration is done via setupNonMainPublish
    compileClasspathConfig.getOutgoing().getArtifacts().add(jarArtifact);

    // Add the jar to the ${SourceSetName}RuntimeElements configuration
    project.getConfigurations().maybeCreate(sourceSet.getRuntimeElementsConfigurationName())
            .getArtifacts().add(jarArtifact);
    // This allows consumption from another project
    // See https://docs.gradle.org/current/userguide/how_to_share_outputs_between_projects.html

    // Create (to-be consumed) configurations
    createElementsConfigurations(sourceSet, project, jarTask);

    project.getPluginManager().withPlugin("maven-publish", p -> {
      setupNonMainPublish(grammarsJarArtifact, project, sourceSet, component, jarArtifact, sourcesJarArtifact);
    });
  }

  /*
   * Actually do publish the artifacts
   */
  protected void setupNonMainPublish(PublishArtifact grammarsJarArtifact, Project project,
                                     SourceSet sourceSet, AdhocComponentWithVariants component,
                                     PublishArtifact jarArtifact, PublishArtifact sourcesJarArtifact) {

    // We have to use afterEvaluate due to accessing the Project#getGroup() value
    project.afterEvaluate(evalProj -> {
      // Only register the publication when the maven-publish plugin is loaded
      evalProj.getPluginManager().withPlugin("maven-publish", mavenPublishPlugin -> {
        evalProj.getExtensions().configure(PublishingExtension.class, publExt -> {
          // Set up a Maven publication for non-main source sets
          // First, check if the publication already exists
          var pubOpt = publExt.getPublications()
                  .matching(publication -> publication.getName().equals(sourceSet.getName())
                          && publication instanceof MavenPublication).stream().findAny();
          if (pubOpt.isPresent()) {
            // If present, properly configure it
            configureNonMainPublication(grammarsJarArtifact, sourceSet, component, evalProj, (MavenPublication) pubOpt.get(), jarArtifact, sourcesJarArtifact);
          } else {
            // Otherwise create it & configure it then
            publExt.getPublications().create(sourceSet.getName(), MavenPublication.class, mavenPublication -> {
              configureNonMainPublication(grammarsJarArtifact, sourceSet, component, evalProj, mavenPublication, jarArtifact, sourcesJarArtifact);
            });
          }
        });
      });
    });

    // Next, we add a runtime variant from the runtime classpath configuration (and add the jar Artifact)
    Configuration runtimeClasspathConfig = project.getConfigurations().getByName(sourceSet.getRuntimeClasspathConfigurationName());
    component.addVariantsFromConfiguration(runtimeClasspathConfig, new JavaConfigurationVariantMapping("runtime", false));

    // Next, we add a separate documentation/sources variant
    Configuration sourcesElementsConfig = project.getConfigurations().getByName(sourceSet.getSourcesElementsConfigurationName());
    // Then, add a variant to the component, as otherwise no variant is published
    component.addVariantsFromConfiguration(sourcesElementsConfig, new JavaConfigurationVariantMapping("compile", true));

    // Next, we add also a compile variant from the compile classpath configuration (and add both outgoing jar artifacts)
    Configuration compileClasspathConfig = project.getConfigurations().getByName(sourceSet.getCompileClasspathConfigurationName());
    component.addVariantsFromConfiguration(compileClasspathConfig, new JavaConfigurationVariantMapping("compile", false));

  }

  protected void configureNonMainPublication(PublishArtifact grammarsJarArtifact, SourceSet sourceSet, AdhocComponentWithVariants component, Project evalProj, MavenPublication mavenPublication, PublishArtifact jarArtifact, PublishArtifact sourcesJarArtifact) {
    // And append the source set name as an appendix to the artifact id
    mavenPublication.setArtifactId(evalProj.getName() + "-" + sourceSet.getName());
    // Use the same groupId as the evalProj
    if (Objects.toString(evalProj.getGroup()).isEmpty()) {
      doError(evalProj, "Unable to publish MC-source set " + sourceSet.getName() + " due to no group being known for the project " + evalProj.getName() + ". \nTry `group='example'` in your build.gradle");
    }
    mavenPublication.setGroupId(evalProj.getGroup().toString());
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

  /*
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

  /*
   * Create a new Jar task on a given source set,
   * set its archive appendix (if non-main),
   * and add it to the assemble task dependencies.
   * Further configurations MUST be done using the consumer
   */
  protected TaskProvider<Jar> createJarTaskPartial(SourceSet sourceSet, Project project, String target, Consumer<Jar> c) {
    TaskProvider<Jar> jarTask = super.createJarTaskPartial(sourceSet, project, target, c);
    jarTask.configure(jar -> jar.setGroup("build"));
    return jarTask;
  }

  /*
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
    sourcesElementsConfig.getAttributes().attribute(GRAMMAR_SOURCE_SET_ATTRIBUTE, sourceSet.getName());

    return sourcesElementsConfig;
  }

  /**
   * Asserts that declared dependencies of the project appear as transitive dependencies in the publication.
   * To this end, this method lets the outgoing configuration of the given [SourceSet] extend from
   * it's `grammars` configuration.
   *
   * @param sourceSet the [SourceSet] whose symbols/grammars should be published and for which this method will
   *                  add the transitive dependencies.
   * @param project the project
   */
  protected void linkDeclaredDependenciesToOutgoingConfiguration(SourceSet sourceSet, Project project) {
    Configuration declaringDependencyConfig = project.getConfigurations().getByName(MCSourceSets.getDependencyDeclarationConfigName(sourceSet));
    Configuration outgoingConfiguration = project.getConfigurations().getByName(MCSourceSets.getOutgoingSymbolConfigName(sourceSet));

    outgoingConfiguration.extendsFrom(declaringDependencyConfig);
  }

  /*
   * Log an error and abort the gradle build process (by throwing a GradleException)
   */
  protected void doError(Project project, String msg) {
    project.getLogger().error(msg);
    throw new GradleException(msg);
  }

  protected void createElementsConfigurations(SourceSet sourceSet, Project project, TaskProvider<?> jarTask) {
    // These configurations are the to-be used by a consumer

    ObjectFactory objects = project.getObjects();

    Configuration runtimeElements = project.getConfigurations().maybeCreate(sourceSet.getRuntimeElementsConfigurationName()); // ${name}RuntimeElements
    runtimeElements.setCanBeConsumed(true);
    runtimeElements.setCanBeResolved(false);
    runtimeElements.extendsFrom(project.getConfigurations().getByName(sourceSet.getRuntimeOnlyConfigurationName())); // ${name}RuntimeOnly

    //
    runtimeElements.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.JAVA_RUNTIME));
    runtimeElements.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.class, Category.LIBRARY));
    runtimeElements.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.class, LibraryElements.JAR));
    runtimeElements.getAttributes().attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.class, Bundling.EXTERNAL));
    runtimeElements.getAttributes().attribute(GRAMMAR_SOURCE_SET_ATTRIBUTE, sourceSet.getName());

    setupElementsConfigurationWithArtifacts(sourceSet, runtimeElements, jarTask, objects, project);

    Configuration apiElements = project.getConfigurations().maybeCreate(sourceSet.getApiElementsConfigurationName()); // ${name}ApiElements
    apiElements.setCanBeConsumed(true);
    apiElements.setCanBeResolved(false);
    apiElements.extendsFrom(project.getConfigurations().getByName(sourceSet.getCompileOnlyConfigurationName())); // ${name}CompileOnly

    //
    apiElements.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.JAVA_API));
    apiElements.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.class, Category.LIBRARY));
    apiElements.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.class, LibraryElements.JAR));
    apiElements.getAttributes().attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.class, Bundling.EXTERNAL));
    apiElements.getAttributes().attribute(GRAMMAR_SOURCE_SET_ATTRIBUTE, sourceSet.getName());

    setupElementsConfigurationWithArtifacts(sourceSet, apiElements, jarTask, objects, project);
  }

  protected void setupElementsConfigurationWithArtifacts(SourceSet sourceSet, Configuration runtimeElements, TaskProvider<?> jarTask, ObjectFactory objects, Project project) {
    runtimeElements.outgoing(outgoing -> {
      outgoing.artifact(jarTask);

      project.afterEvaluate(p -> {
        // AfterEvaluate due to group being a weird attribute
        outgoing.capability(p.getGroup() + ":" + p.getName() + "-" + sourceSet.getName() + ":" + p.getVersion());
      });

      // Also register a variant for local builds without the jarTask
      outgoing.variants(configurationVariants -> {
        configurationVariants.create("classes", configurationVariant -> {
          configurationVariant.artifact(sourceSet.getOutput().getClassesDirs().getSingleFile(), a -> {
            a.builtBy(sourceSet.getClassesTaskName());
          });
          configurationVariant.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.class, LibraryElements.CLASSES));
        });
        if (sourceSet.getOutput().getResourcesDir() != null) {
          configurationVariants.create("resources", configurationVariant -> {
            configurationVariant.artifact(sourceSet.getOutput().getResourcesDir(), a -> {
              a.builtBy(sourceSet.getProcessResourcesTaskName());
            });
            configurationVariant.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.class, LibraryElements.RESOURCES));
          });
        }
      });
    });
  }

}
