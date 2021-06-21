/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.Sets;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides access to the aggregated configuration of a MontiCore instance
 * derived from (1) its command line arguments, and (2) system properties (not
 * implemented yet).
 */
public final class MontiCoreConfiguration implements Configuration {

  public static final String MC4_EXTENSION = "mc4";

  public static final String JAVA_EXTENSION = "java";

  public static final String FTL_EXTENSION = "ftl";

  public static final Set<String> MC4_EXTENSIONS = Sets.newHashSet(MC4_EXTENSION);

  public static final Set<String> HWC_EXTENSIONS = Sets.newHashSet(JAVA_EXTENSION);

  public static final Set<String> FTL_EXTENSIONS = Sets.newHashSet(FTL_EXTENSION);

  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_PATH = "out";
  
  public static final String DEFAULT_HANDCODED_JAVA_PATH = "java";

  public static final String DEFAULT_REPORT_PATH = "reports";

  public static final String DEFAULT_HANDCODED_TEMPLATE_PATH = "resource";

  public static final String DEFAULT_GRAMMAR_PATH = "grammars";

  /**
   * Constants for the allowed CLI options in their long and short froms.
   * Stored in constants as they are used multiple times in MontiCore.
   */
  public static final String GRAMMAR = "g";
  public static final String OUT = "o";
  public static final String MODELPATH = "mp";
  public static final String HANDCODEDPATH = "hcp";
  public static final String SCRIPT = "sc";
  public static final String GROOVYHOOK1 = "gh1";
  public static final String GROOVYHOOK2 = "gh2";
  public static final String TEMPLATEPATH = "fp";
  public static final String CONFIGTEMPLATE = "ct";
  public static final String DEV = "d";
  public static final String CUSTOMLOG = "cl";
  public static final String REPORT = "r";
  public static final String HELP = "h";

  public static final String GRAMMAR_LONG = "grammar";
  public static final String OUT_LONG = "out";
  public static final String MODELPATH_LONG = "modelPath";
  public static final String HANDCODEDPATH_LONG = "handcodedPath";
  public static final String SCRIPT_LONG = "script";
  public static final String GROOVYHOOK1_LONG = "groovyHook1";
  public static final String GROOVYHOOK2_LONG = "groovyHook2";
  public static final String TEMPLATEPATH_LONG = "templatePath";
  public static final String CONFIGTEMPLATE_LONG = "configTemplate";
  public static final String DEV_LONG = "dev";
  public static final String CUSTOMLOG_LONG = "customLog";
  public static final String REPORT_LONG = "report";
  public static final String HELP_LONG = "help";

  private final CommandLine cmdConfig;

  /**
   * Factory method for {@link MontiCoreConfiguration}.
   */
  public static MontiCoreConfiguration withConfiguration(Configuration configuration) {
    return new MontiCoreConfiguration(configuration);
  }

  /**
   * Factory method for {@link MontiCoreConfiguration}.
   */
  public static MontiCoreConfiguration withCLI(CommandLine options) {
    return new MontiCoreConfiguration(options);
  }

  /**
   * Constructor for {@link MontiCoreConfiguration}
   */
  private MontiCoreConfiguration(Configuration internal) {
    this.cmdConfig = internal.getConfig();
  }

  /**
   * Constructor for {@link MontiCoreConfiguration}
   */
  private MontiCoreConfiguration(CommandLine cmdConfig) {
    this.cmdConfig = cmdConfig;
  }

  private boolean checkPath(List<String> grammars) {
    for (String g: grammars) {
      Path p = Paths.get(g);
      if (!Files.exists(p)) {
        Log.error("0xA1019 The requested path " + p.toString() + " does not exist.");
        return false;
      }
    }
    return true;
  }
  /**
   * Getter for the {@link MCPath} consisting of grammar files stored in
   * this configuration.
   *
   * @return iterable grammar files
   */
  public MCPath getGrammars() {
    Optional<List<String>> grammars = getAsStrings(Options.GRAMMARS);
    if (grammars.isPresent() && checkPath(grammars.get())) {
      return new MCPath(toFileList(grammars.get()));
    }
    grammars = getAsStrings(Options.GRAMMARS_SHORT);
    if (grammars.isPresent() && checkPath(grammars.get())) {
      return new MCPath(toFileList(grammars.get()));
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xA1013 Please specify the grammar file(s).");
    return new MCPath();
  }

  /**
   * Getter for the actual value of the grammar argument. This is not the
   * prepared {@link MCPath} as in
   * {@link MontiCoreConfiguration#getGrammars()} but the raw input arguments.
   *
   * @return
   */
  public List<String> getGrammarsAsStrings() {
    Optional<List<String>> grammars = getAsStrings(GRAMMAR);
    if (grammars.isPresent()) {
      return grammars.get();
    }
    // no default; must specify grammar files/directories to process
    Log.error("0xA1014 Please specify the grammar file(s).");
    return Collections.emptyList();
  }

  /**
   * Getter for the list of model path elements (files and directories) stored
   * in this configuration.
   *
   * @return list of model path files
   */
  public MCPath getModelPath() {
    Optional<MCPath> modelPath = getAsStrings(Options.MODELPATH)
        .map(this::convertEntryNamesToMCPath);
    if (modelPath.isPresent()) {
      return modelPath.get();
    }
    // default model path is empty
    return new MCPath();
  }

  private MCPath convertEntryNamesToMCPath(List<String> modelPathEntryNames) {
    List<Path> modelPathFiles = toFileList(modelPathEntryNames);
    List<Path> modelPathEntries = modelPathFiles.stream()
        .map(Path::toAbsolutePath)
        .collect(Collectors.toList());
    return new MCPath(modelPathEntries);
  }

  /**
   * Getter for the actual value of the model path argument. This is not the
   * prepared {@link MCPath} as in
   * {@link MontiCoreConfiguration#getModelPath()} but the raw input arguments.
   *
   * @return
   */
  public List<String> getModelPathAsStrings() {
    Optional<List<String>> modelPath = getAsStrings(MODELPATH);
    if (modelPath.isPresent()) {
      List<String> result = new ArrayList<>(modelPath.get());
      return result;
    }
    // default model path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "monticore/sourcecode".
   *
   * @return output directory file
   */
  public File getOut() {
    Optional<String> out = getAsString(OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "out"
    return new File(DEFAULT_OUTPUT_PATH);
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "monticore/sourcecode".
   *
   * @return output directory file
   */
  public File getReport() {
    Optional<String> report = getAsString(REPORT);
    if (report.isPresent()) {
      return new File(report.get());
    }
    // fallback default is "out/report"
    return new File(getOut(), DEFAULT_REPORT_PATH);
  }

  /**
   * Getter for the handcoded path directories stored in this configuration.
   *
   * @return iterable handcoded files
   */
  public IterablePath getHandcodedPath() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return new MCPath(toFileList(handcodedPath.get()));
    }
    // default handcoded path is "java"
    File defaultFile = new File(DEFAULT_HANDCODED_JAVA_PATH);
    if (!defaultFile.exists()) {
      return new MCPath();
    }
    return new MCPath(new File(DEFAULT_HANDCODED_JAVA_PATH).toPath());
  }

  /**
   * Getter for the actual value of the handcoded path argument. This is not the
   * prepared {@link MCPath} as in
   * {@link MontiCoreConfiguration#getHandcodedPath()} but the raw input
   * arguments.
   *
   * @return
   */
  public List<String> getHandcodedPathAsStrings() {
    Optional<List<String>> handcodedPath = getAsStrings(HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    // default handcoded path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the target path directories stored in this configuration.
   *
   * @return iterable template files
   */
  public IterablePath getTemplatePath() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return new MCPath(toFileList(templatePath.get()));
    }
    // default handcoded template path is "resource"
    File defaultFile = new File(DEFAULT_HANDCODED_TEMPLATE_PATH);
    if (!defaultFile.exists()) {
      return new MCPath();
    }
    return new MCPath(new File(DEFAULT_HANDCODED_TEMPLATE_PATH).toPath());
  }

  /**
   * Getter for the actual value of the template path argument. This is not the
   * prepared {@link MCPath} as in
   * {@link MontiCoreConfiguration#getTemplatePath()} but the raw input
   * arguments.
   *
   * @return
   */
  public List<String> getTemplatePathAsStrings() {
    Optional<List<String>> templatePath = getAsStrings(TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }

  /**
   * Getter for the optional config template.
   *
   * @return Optional of the config template
   */
  public Optional<String> getConfigTemplate() {
    return getAsString(CONFIGTEMPLATE);
  }

  /**
   * Getter for the optional groovy script for hook point one.
   *
   * @return Optional path to the script
   */
  public Optional<String> getGroovyHook1() {
    return getAsString(GROOVYHOOK1);
  }

  /**
   * Getter for the optional groovy script for hook point two.
   *
   * @return Optional path to the script
   */
  public Optional<String> getGroovyHook2() {
    return getAsString(GROOVYHOOK2);
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<Path> toFileList(List<String> files) {
    return files.stream().collect(
        Collectors.mapping(file -> new File(file).getAbsoluteFile().toPath(), Collectors.toList()));
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getConfig()
   */
  public CommandLine getConfig() {
    return cmdConfig;
  }
}
