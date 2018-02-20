/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo.m2e;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectUtils;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.osgi.service.prefs.BackingStoreException;

/**
 * An {@link AbstractSourcesGenerationProjectConfigurator} for the
 * monticore-maven-plugin.
 * 
 * @author (last commit) $Author: antonio $
 * 2014) $
 */
public class MontiCoreProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {
  
  private static final String GRAMMARS_PARAMETER = "grammars";
  
  private static final String DEFAULT_GRAMMARS_DIRECTORY = "src/main/grammars";
  
  private static final String OUTPUT_DIRECTORY_PARAMETER = "outputDirectory";
  
  private static final String DEFAULT_OUTPUT_DIRECTORY = "target/generated-sources/monticore/sourcecode";
  
  private static final String SYMBOL_TABLE_DIRECTORY_PARAMETER = "symbolTableDirectory";
  
  private static final String DEFAULT_SYMBOL_TABLE_DIRECTORY = "target/generated-sources/monticore/symboltable";
  
  private static final boolean IS_GENERATED = true;
  
  private static final boolean IS_NOT_GENERATED = false;
  
  private ProjectConfigurationRequest request;
  
  private IClasspathDescriptor classpath;
  
  private IProgressMonitor monitor;
  
  private File basedir;
  
  /**
   * Configures all source folders contributed by the plugin to be added as
   * source folders in the Eclipse project.
   */
  @Override
  public void configureRawClasspath(
      ProjectConfigurationRequest request,
      IClasspathDescriptor classpath,
      IProgressMonitor monitor) throws CoreException {
    
    assertHasNature(request.getProject(), JavaCore.NATURE_ID);
    
    this.request = request;
    this.classpath = classpath;
    this.monitor = monitor;
    this.basedir = request.getMavenProject().getBasedir();
    
    addSourceEntries(getModelDirectories(), IS_NOT_GENERATED);
    addSourceEntries(getOutputDirectory(), IS_GENERATED);
    
    configureMontiCoreSettings();
  }
  
  /**
   * Puts into the MontiCore project settings the symbol table directory and
   * configuration file value taken from the POM.
   */
  private final void configureMontiCoreSettings() throws CoreException {
    // write into project .settings
    IScopeContext projectContext = new ProjectScope(request.getProject());
    
    IEclipsePreferences prefs = projectContext.getNode("org.eclipse.monticore.core");
    if (prefs != null) {
      try {
        File symbolTableDirectory = getFileOrDirectoryFromFileParameter(
            SYMBOL_TABLE_DIRECTORY_PARAMETER, DEFAULT_SYMBOL_TABLE_DIRECTORY, true);
        prefs.put(SYMBOL_TABLE_DIRECTORY_PARAMETER, symbolTableDirectory.getPath());
        File outputDirectory = getFileOrDirectoryFromFileParameter(OUTPUT_DIRECTORY_PARAMETER,
            DEFAULT_OUTPUT_DIRECTORY, true);
        prefs.put(OUTPUT_DIRECTORY_PARAMETER, outputDirectory.getPath());
        prefs.flush();
      }
      catch (BackingStoreException e) {
        // TODO find out how to properly react in such cases
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Extracts the file or directory parameter value.
   */
  private File getFileOrDirectoryFromFileParameter(String fileParameter, String defaultValue,
      boolean isDirectory) throws CoreException {
    Set<File> files = isDirectory
        ? getDirectoriesFromFileParameter(fileParameter)
        : getFilesFromFileParameter(fileParameter);
    if (files.isEmpty()) {
      files.add(new File(this.basedir, defaultValue));
    }
    // is this a good implementation (we only take the first element returned
    // from the iterator)
    return files.iterator().next();
  }
  
  /**
   * Adds all the given paths to this build's set of source directories.
   */
  private final void addSourceEntries(Set<File> directories, boolean isGenerated) {
    for (File directory : directories) {
      addSourceEntry(directory, isGenerated);
    }
  }
  
  /**
   * Adds the given path to this build's set of source directories.
   */
  private final void addSourceEntry(File directory, boolean isGenerated) {
    
    if (directory != null && directory.isDirectory()) {
      
      IMavenProjectFacade mavenProject = this.request.getMavenProjectFacade();
      IProject project = this.request.getMavenProjectFacade().getProject();
      IPath path = MavenProjectUtils.getProjectRelativePath(project, directory.getAbsolutePath());
      path = project.getFullPath().append(path);
      
      this.classpath.addSourceEntry(path, mavenProject.getOutputLocation(), isGenerated);
      
    }
    
  }
  
  /**
   * Removes all files from a set that are a sub-directory of a file of another
   * set.
   */
  private Set<File> filterNested(Set<File> files) {
    
    Set<File> filteredFiles = new HashSet<File>(files);
    
    for (File f1 : files) {
      for (File f2 : files) {
        if (f1 != f2) {
          File f = f1;
          while (f != null) {
            if (f.equals(f2)) {
              filteredFiles.remove(f1);
              break;
            }
            f = f.getParentFile();
          }
        }
      }
    }
    
    return filteredFiles;
  }
  
  /**
   * Returns a set of directories from the value configured by the given MOJO
   * parameter.
   */
  private Set<File> getDirectoriesFromFileListParameter(String parameterName) throws CoreException {
    Set<File> directories = new HashSet<File>();
    for (MojoExecution mojoExecution : getMojoExecutions(this.request, this.monitor)) {
      List<?> directoryFiles = getMojoParameterValue(parameterName, List.class, mojoExecution);
      if (directoryFiles != null) {
        for (Object object : directoryFiles) {
          if (object != null) {
            File directory = new File(this.basedir, (String) object);
            if (directory.isDirectory()) {
              directories.add(directory);
            }
            else if (directory.isFile()) {
              directories.add(directory.getParentFile());
            }
          }
        }
      }
    }
    
    return directories;
  }
  
  /**
   * Returns a set of directories from the values configured by the given MOJO
   * parameter.
   */
  private Set<File> getDirectoriesFromFileParameter(String parameterName) throws CoreException {
    Set<File> directories = new HashSet<File>();
    for (MojoExecution mojoExecution : getMojoExecutions(this.request, this.monitor)) {
      File directory = getMojoParameterValue(parameterName, File.class, mojoExecution);
      if (directory != null) {
        if (directory.isDirectory()) {
          directories.add(directory);
        }
        else if (directory.isFile()) {
          directories.add(directory.getParentFile());
        }
      }
    }
    
    return directories;
  }
  
  /**
   * Returns a set of files from the values configured by the given MOJO
   * parameter.
   */
  private Set<File> getFilesFromFileParameter(String parameterName) throws CoreException {
    Set<File> files = new HashSet<File>();
    for (MojoExecution mojoExecution : getMojoExecutions(this.request, this.monitor)) {
      File file = getMojoParameterValue(parameterName, File.class, mojoExecution);
      if (file != null && !file.isDirectory()) {
        files.add(file);
      }
    }
    return files;
  }
  
  /**
   * Retrieves the configured model directories of the current MOJO execution.
   */
  private Set<File> getModelDirectories() throws CoreException {
    Set<File> modelDirectories = getDirectoriesFromFileListParameter(GRAMMARS_PARAMETER);
    if (modelDirectories.isEmpty()) {
      modelDirectories.add(new File(this.basedir, DEFAULT_GRAMMARS_DIRECTORY));
    }
    
    modelDirectories = filterNested(modelDirectories);
    
    return modelDirectories;
  }
  
  /**
   * Retrieves the value of the given MOJO parameter.
   * 
   * @throws CoreException
   */
  private final <T> T getMojoParameterValue(String parameterName, Class<T> valueClass,
      MojoExecution mojoExecution)
      throws CoreException {
    
    return MavenPlugin.getMaven().getMojoParameterValue(
        this.request.getMavenProject(),
        mojoExecution,
        parameterName,
        valueClass,
        monitor);
  }
  
  /**
   * Retrieves the configured output directory of the current MOJO execution.
   */
  private final Set<File> getOutputDirectory() throws CoreException {
    return getDirectoriesFromFileParameter(OUTPUT_DIRECTORY_PARAMETER);
  }
  
}
