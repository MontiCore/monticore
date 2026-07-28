/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.dependencies;

import de.monticore.gradle.gen.MCGenTask;
import de.se_rwth.commons.logging.Log;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Bundling;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


/**
 * Creates "mcTool" configurations for the monticore generator tool.
 * The MCGenTasks run the tool from this configuration
 */
public class MCToolDependenciesPlugin implements Plugin<Project> {

  public static final String MC_CONFIG_TOOL = "mcTool";

  @Override
  public void apply(@Nonnull Project project) {
    project.getPluginManager().apply("java-library");


    // add a new configuration in which the monticore-generator (.jar) is loaded
    //  (this avoids loading the tool+all its dependencies into the shared plugin-classpath)
    var toolConfig = project.getConfigurations().maybeCreate(MC_CONFIG_TOOL);
    // Load the buildInfo.properties file (containing the current version)
    Properties buildInfo = new Properties();
    try(InputStream is = MCToolDependenciesPlugin.class.getResourceAsStream("/buildInfo.properties")) {
      buildInfo.load(is);
    } catch (Exception e){
      Log.error("Can not load /buildInfo.properties from classpath", e);
    }
    String version = Objects.requireNonNull(
            buildInfo.getProperty("version"),
            "Can not find version in buildInfo.properties"
    );
    // Add the (default) dependencies to the toolConfig
    toolConfig.setCanBeResolved(true);
    toolConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("de.monticore:monticore-generator:" + version));
      dependencies.add(project.getDependencies().create("de.monticore:gradle-plugin:" + version));
    });
    toolConfig.attributes(it -> {
      // Do not use the shadowed bundling variant (by default)
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
    });

    // And add the config to the classpath of various tasks
    project.getTasks().withType(MCGenTask.class).configureEach(t -> t.getExtraClasspathElements().from(toolConfig));

  }
}
