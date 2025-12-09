package de.monticore;

import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

/**
 * A BuildService used to ensure that multiple tasks using MontiCore Mills do not run in parallel
 */
public class MCMillBuildService implements BuildService<BuildServiceParameters.None> {
  @Override
  public BuildServiceParameters.None getParameters() {
    return null;
  }
}
