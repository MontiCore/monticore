/* (c) https://github.com/MontiCore/monticore */

package de.monticore.incremental;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;

import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

/**
 * Utility methods for checking whether for a given input file path based on the
 * {@link InputOutputFilesReporter} processing can be skipped or not, i.e., the
 * realization of incremental model processing.
 *
 * @since 4.1.5
 */
public class IncrementalChecker {
  
  /**
   * Checks whether the given input model (as path) is up to date based on any
   * previous input output reports to be examined from the given output
   * directory and based on the currently configured model path and handwritten
   * artifacts path.
   * 
   * @param inputPath path to the model to check
   * @param outputDirectory the location where to look for both a corresponding
   * input output report for the given model as well as any previously generated
   * artifacts
   * @param modelPath the current model path used to check the current state of
   * any dependencies reported for the previous processing of the input model
   * @param hwcPath the current handwritten artifacts path used to check the
   * current state of any previously found/not found handwritten artifacts
   * @return whether there are no significant changes in the model or its
   * context based on the current configuration
   */
  public static boolean isUpToDate(Path inputPath, File outputDirectory,
      ModelPath modelPath, IterablePath templatePath, IterablePath hwcPath) {
    if (inputPath == null) {
      throw new IllegalArgumentException(
          "0xA4062 Input path to check for incremental regeneration must not be null.");
    }
    if (modelPath == null) {
      throw new IllegalArgumentException(
          "0xA4064 Model path for checking incremental regeneration must not be null.");
    }
    if (templatePath == null) {
      throw new IllegalArgumentException(
          "0xA4065 User template path for checking incremental regeneration must not be null.");
    }
    if (hwcPath == null) {
      throw new IllegalArgumentException(
          "0xA4075 Handwritten code path for checking incremental regeneration must not be null.");
    }
    if (!outputDirectory.exists()) {
      Log.debug("Output directory does not exist.", IncrementalChecker.class.getName());
      // apparently there is no output yet, so (re)generate
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    Optional<InputOutputStory> story = getStoryFor(inputPath);
    
    // this signifies that there is no matching entry for the given input; it is
    // hence a new input file
    if (!story.isPresent()) {
      Log.debug("There is no input output report for " + inputPath.toString(),
          IncrementalChecker.class.getName());
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    // check whether the input model changed contentwise
    if (mainInputChanged(story.get().mainInputStory)) {
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    // after the easy stuff above comes the hard stuff
    if (dependenciesChanged(story.get().inputStories, modelPath)) {
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    // check if user templates changed
    if (userTemplatesChanged(story.get().templateStories, templatePath)) {
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
   // check if handwritten files changed
    if (handwrittenCodeChanged(story.get().hwcStories, hwcPath)) {
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    // check whether the previous output files for the given input file still
    // exist otherwise regenerate
    if (outputFilesChanged(story.get().outputStories)) {
      Log.info("Changes detected for " + inputPath.toString() + ". Regenerating...",
          IncrementalChecker.class.getName());
      return false;
    }
    
    Log.info(inputPath.toString() + " already up to date.",
        IncrementalChecker.class.getName());
    return true;
  }
  
  /**
   * Checks whether the main input given via the supplied input story has
   * changed. This check methods covers actual changes in the main input model
   * artifact.
   * 
   * @param story
   * @return whether the main input model artifact changed
   */
  protected static boolean mainInputChanged(InputStory story) {
    String previousChecksum = story.state;
    String currentChecksum = getChecksum(story.inputPath);
    // signifies that the input file was changed
    if (!currentChecksum.equals(previousChecksum)) {
      Log.debug("The input file " + story.inputPath.toString() + " has changed.",
          IncrementalChecker.class.getName());
      return true;
    }
    return false;
  }
  
  /**
   * Checks whether any of the dependencies of the main input changed; either
   * contentwise or if their actually resolved location changed (this would
   * indicate a change of a dependency version).
   * 
   * @param stories
   * @param modelPath
   * @return whether any dependency of the main input changed
   */
  protected static boolean dependenciesChanged(Map<String, InputStory> stories,
      ModelPath modelPath) {
    // here we analyze the dependencies of the file we want to check according
    // to the last report
    for (Entry<String, InputStory> story : stories.entrySet()) {
      // for each dependency we get the respective state (hash or "missing")
      // from the last report
      String input = story.getKey();
      InputStory inputStory = story.getValue();
      
      ModelCoordinate currentResolution = ModelCoordinates.createQualifiedCoordinate(Paths
          .get(inputStory.inputPath));
      currentResolution = modelPath.resolveModel(currentResolution);
      
      if (!currentResolution.hasLocation()) {
        Log.debug("The dependency " + inputStory.inputPath + " could not be resolved.",
            IncrementalChecker.class.getName());
        Log.debug("  Previous location was " + input, IncrementalChecker.class.getName());
        return true;
      }
      
      // if it's a file within a jar file we read it and compare hashes for
      // changes
      if (input.startsWith("jar:file:")) {
        Log.debug("Examining " + input, IncrementalChecker.class.getName());
        try {
          URL url = new URL(input);
          
          if (!currentResolution.getLocation().sameFile(url)) {
            // this will detect changes in jar versions etc.
            Log.debug("The location of the dependency " + inputStory.inputPath + " changed.",
                IncrementalChecker.class.getName());
            Log.debug("  Previous location was " + input, IncrementalChecker.class.getName());
            Log.debug("  Current location is " + currentResolution.getLocation().toString(),
                IncrementalChecker.class.getName());
            return true;
          }
          
          String inputModel = CharStreams.toString(new InputStreamReader(url.openStream()));
          MessageDigest md = MessageDigest.getInstance("MD5");
          md.update(inputModel.getBytes());
          String currentState = Hashing.md5().hashString(inputModel, Charset.forName("UTF-8"))
              .toString();
          if (!currentState.equals(inputStory.state)) {
            Log.debug("The dependency " + input + " has changed.",
                IncrementalChecker.class.getName());
            Log.debug("  Previous state was " + inputStory.state,
                IncrementalChecker.class.getName());
            Log.debug("  Current state is " + currentState, IncrementalChecker.class.getName());
            return true;
          }
        }
        catch (IOException | NoSuchAlgorithmException e) {
          Log.error("Error during analysis of dependencies for incremental check.", e);
          return true;
        }
        
      }
      
      // if it's a regular file we check whether it's state changed (hash and
      // missing vs. there)
      else {
        File file = new File(input);
        String currentState = file.exists()
            ? IncrementalChecker.getChecksum(input)
            : InputOutputFilesReporter.MISSING;
        if (!currentState.equals(inputStory.state)) {
          Log.debug("The dependency file " + input + " has changed.",
              IncrementalChecker.class.getName());
          Log.debug("  Previous state was " + inputStory.state, IncrementalChecker.class.getName());
          Log.debug("  Current state is " + currentState, IncrementalChecker.class.getName());
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Checks whether any of the user templates of the last execution
   * changed, i.e., whether a user template which was there the last time
   * is missing this time or has been changed.
   * 
   * @param stories
   * @param templatePath
   * @return whether the altogether state of handwritten artifacts changed
   */
  protected static boolean userTemplatesChanged(Set<String> stories, IterablePath templatePath) {
    List<File> templateNames = new ArrayList<>();
    templatePath.getResolvedPaths().forEachRemaining(p -> templateNames.add(p.toFile()));
    if (templateNames.isEmpty() && !stories.isEmpty()) {
      Log.debug("The user template path is empty.",
          IncrementalChecker.class.getName());
      return  true;
    }
    for (String template : stories) {
      Optional<InputStory> inputStory = parseInput(template);
      if (inputStory.isPresent()) {
        String fileName = calculateInputFileNameFrom(inputStory.get().parentPath,
            inputStory.get().inputPath);
        File file = new File(fileName);
        String currentState = file.exists()
            ? IncrementalChecker.getChecksum(fileName)
            : InputOutputFilesReporter.MISSING;
        if (!currentState.equals(inputStory.get().state)) {
          Log.debug("The template " + fileName + " has changed.",
              IncrementalChecker.class.getName());
          Log.debug("  Previous state was " + inputStory.get().state, IncrementalChecker.class.getName());
          Log.debug("  Current state is " + currentState, IncrementalChecker.class.getName());
          return true;
        }
        templateNames.remove(file);
      }
    }
    return !templateNames.isEmpty();
  }

  /**
   * Checks whether any of the handwritten artifacts of the last execution
   * changed, i.e., whether a handwritten artifact which was there the last time
   * is missing this time and vice versa.
   * 
   * @param stories
   * @param hwcPath
   * @return whether the altogether state of handwritten artifacts changed
   */
  protected static boolean handwrittenCodeChanged(Set<String> stories, IterablePath hwcPath) {
    for (String hwc : stories) {
      String[] elements = hwc.split(InputOutputFilesReporter.PARENT_FILE_SEPARATOR);
      boolean existed = !elements[0].isEmpty();
      boolean exists = hwcPath.exists(Paths.get(elements[1]));
      if (existed ^ exists) {
        Log.debug("The existence of the handwritten file " + elements[1] + " has changed.",
            IncrementalChecker.class.getName());
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether all of the previously generated artifacts are still there.
   * 
   * @param outputFiles
   * @return whether any of the previously generated artifacts is missing
   */
  protected static boolean outputFilesChanged(Set<String> outputFiles) {
    for (String output : outputFiles) {
      String[] line = output.split(InputOutputFilesReporter.PARENT_FILE_SEPARATOR);
      File file;
      if (line.length == 2) {
        file = new File(line[0], line[1]);
      } else {
        file = new File(outputDirectory, output);
      }
      if (!file.exists()) {
        Log.debug("The output file " + output + " was deleted.", IncrementalChecker.class.getName());
        return true;
      }
    }
    return false;
  }
  
  /**
   * Clean up all output files that were previously - if any - generated from
   * the given input model.
   * 
   * @param inputPath
   */
  public static void cleanUp(Path inputPath) {
    Optional<InputOutputStory> story = getStoryFor(inputPath);
    if (story.isPresent()) {
      for (String output : story.get().outputStories) {
        String[] line = output.split(InputOutputFilesReporter.PARENT_FILE_SEPARATOR);
        File outputFile;
        if (line.length == 2) {
          outputFile = new File(line[0], line[1]);
        } else {
          outputFile = new File(outputDirectory, output);
        }
        Path toDelete = Paths.get(outputFile.toString());
        try {
          Files.deleteIfExists(toDelete);
        }
        catch (IOException e) {
          Log.warn("0xA4072 Failed to clean up output.");
          Log.debug("Error while deleting " + toDelete.toString(), e,
              IncrementalChecker.class.getName());
        }
      }
    }
  }
  
  /**
   * Getter for the (optional) input output story for a given input path.
   * 
   * @param inputPath
   * @return
   */
  protected static Optional<InputOutputStory> getStoryFor(Path inputPath) {
    String inputFile = inputPath.toAbsolutePath().toString();
    Map<String, InputOutputStory> cache = getInputOutputStoryCache();
    // find the key representing the file we want to check
    return cache.keySet().stream()
        .filter(inputFile::equals)
        .findFirst()
        .map(cache::get);
  }
  
  /**
   * @return the current input output story cache or error if IncrementalChecker
   * was not properly initialized.
   * @see IncrementalChecker#initialize(File)
   */
  protected static final Map<String, InputOutputStory> getInputOutputStoryCache() {
    if (!isInitialized()) {
      Log.error("0xA4059 Must initialize the incremental checker using it.");
    }
    return inputOutputStoryCache;
  }
  
  /* The input output story cache. */
  static Map<String, InputOutputStory> inputOutputStoryCache;
  
  /* The currently configured output directory. */
  static File outputDirectory;
  
  /**
   * Initializes the IncrementalChecker with the given output directory.
   * Searches for input output reports in the given directory and parses them.
   * The gathered input output stories are cached and ready for subsequent
   * incremental checks.
   * 
   * @param outputDirectory
   */
  public static void initialize(File outputDirectory) {
    if (outputDirectory == null) {
      throw new IllegalArgumentException(
          "0xA4063 Output directory for checking incremental regeneration must not be null.");
    }
    List<Path> inputOutputReportsFiles = getAllInputOutputReports(outputDirectory);
    Map<String, InputOutputStory> result = new HashMap<String, InputOutputStory>();
    for (Path path : inputOutputReportsFiles) {
      collectInputOutputMapFromReport(path, result);
    }
    inputOutputStoryCache = result;
    IncrementalChecker.outputDirectory = outputDirectory;
    initialized = true;
  }
  
  /* Initialization flag. */
  static boolean initialized = false;
  
  /**
   * @return whether the IncrementalChecker has been initialized.
   */
  public static boolean isInitialized() {
    return initialized;
  }
  
  /**
   * Container POJO for storing all gathered information from an input output
   * report.
   *
   */
  protected static class InputOutputStory {
    
    /* The actual input model of this story. */
    private final InputStory mainInputStory;
    
    /* All other input dependencies of the main model. */
    private final Map<String, InputStory> inputStories;
    
    /* All user template stories of the main model. */
    private final Set<String> templateStories;
    
    /* All handwritten file stories of the main model. */
    private final Set<String> hwcStories;
    
    /* All output stories of the main model. */
    private final Set<String> outputStories;
    
    /**
     * Constructor for de.monticore.incremental.InputOutputStory
     */
    protected InputOutputStory(
        InputStory mainInputStory,
        Map<String, InputStory> inputStories,
        Set<String> templateStories,
        Set<String> hwcStories,
        Set<String> outputStories) {
      this.mainInputStory = mainInputStory;
      this.inputStories = inputStories;
      this.templateStories = templateStories;
      this.hwcStories = hwcStories;
      this.outputStories = outputStories;
    }
    
  }
  
  /**
   * Container POJO for storing the necessary information for each input story
   * individually.
   *
   */
  protected static class InputStory {
    
    /* The (optional) parent path for secondary inputs (e.g., dependencies and
     * handwritten files). */
    private final String parentPath;
    
    /* The (qualified) input path for secondary inputs and the full path for
     * main inputs. */
    private final String inputPath;
    
    /* Denotes the state of the input story (e.g., missing, md5 hash, ...). */
    private final String state;
    
    /**
     * Constructor for de.monticore.incremental.IncrementalChecker.InputStory
     */
    protected InputStory(String inputPath, String state) {
      this("", inputPath, state);
    }
    
    /**
     * Constructor for de.monticore.incremental.IncrementalChecker.InputStory
     */
    protected InputStory(String parentPath, String inputPath, String state) {
      this.parentPath = parentPath;
      this.inputPath = inputPath;
      this.state = state;
    }
    
  }
  
  /**
   * Collects all input output report files from the given directory.
   * 
   * @param outputDirectory to search for input output reports
   * @return list of paths to all found input output reports
   */
  protected static List<Path> getAllInputOutputReports(File outputDirectory) {
    if (!outputDirectory.exists()) {
      return Collections.emptyList();
    }
    try {
      return Files.walk(Paths.get(outputDirectory.getPath())).filter(isInputOutputReportFile())
          .collect(Collectors.toList());
    }
    catch (IOException e) {
      Log.warn("0xA1037 Unable to load input output reports", e);
      return Collections.emptyList();
    }
  }
  
  /**
   * @return a predicate for finding files that match the naming pattern
   * "17_InputOutputFiles.txt".
   * @see InputOutputFilesReporter#SIMPLE_FILE_NAME
   * @see ReportingConstants#REPORT_FILE_EXTENSION
   */
  protected static Predicate<Path> isInputOutputReportFile() {
    return new Predicate<Path>() {
      
      @Override
      public boolean test(Path t) {
        File file = t.toFile();
        return file.isFile()
            && file.getName().equals(
                InputOutputFilesReporter.SIMPLE_FILE_NAME + "."
                    + ReportingConstants.REPORT_FILE_EXTENSION);
      }
    };
  }
  
  /**
   * Extracts the main input story from the given line.
   * 
   * @param from
   * @return
   */
  protected static Optional<InputStory> parseMainInput(String from) {
    String[] data = from.split(InputOutputFilesReporter.INPUT_STATE_SEPARATOR);
    if (data.length != 2) {
      return Optional.empty();
    }
    return Optional.of(new InputStory(data[0], data[1]));
  }
  
  /**
   * Extracts the input story from the given line.
   * 
   * @param from
   * @return
   */
  protected static Optional<InputStory> parseInput(String from) {
    String[] stateData = from.split(InputOutputFilesReporter.INPUT_STATE_SEPARATOR);
    if (stateData.length != 2) {
      return Optional.empty();
    }
    String[] inputData = stateData[0].split(InputOutputFilesReporter.PARENT_FILE_SEPARATOR);
    if (inputData.length != 2) {
      return Optional.empty();
    }
    return Optional.of(new InputStory(inputData[0], inputData[1], stateData[1]));
  }
  
  /**
   * Calculate the actual file name for the given parent and child files. The
   * calculated file name for jar file parents is in fact a URL which allows to
   * read the actual file content.
   * 
   * @param parent
   * @param child
   * @return
   */
  protected static String calculateInputFileNameFrom(String parent, String child) {
    if (parent.endsWith(".jar")) {
      String result = parent.startsWith("/") ? "jar:file:" : "jar:file:\\";
      return result + parent.concat("!/").concat(child).replaceAll("\\" + File.separator, "/");
    }
    else {
      return parent.concat(File.separator).concat(child);
    }
  }
  
  /**
   * Parses and collects all input output related information from the report
   * located at the given path. The results are stored in the given map.
   * 
   * @param report path to report file to parse and process
   * @param inputOutputMap the map to store the gathered information in
   */
  protected static void collectInputOutputMapFromReport(Path report,
      Map<String, InputOutputStory> inputOutputMap) {
    FileReaderWriter io = new FileReaderWriter();
    String reportContent = io.readFromFile(report);
    // read all lines at once
    List<String> lines = Arrays.asList(reportContent.split("\\r?\\n"));
    
    Iterator<String> it = lines.iterator();
    if (!it.hasNext()) {
      Log.warn("0xA4073 Empty input output report " + report.toString());
      return;
    }
    
    InputStory mainInputStory = null;
    Map<String, InputStory> inputStories = new HashMap<>();
    Set<String> templateStories = new LinkedHashSet<>();
    Set<String> hwcStories = new LinkedHashSet<>();
    Set<String> outputStories = new LinkedHashSet<>();
    
    it.next(); // skip first line (it's the input heading)
    String line = it.next();
    if (line.equals(InputOutputFilesReporter.USER_TEMPLATE_HEADING)) {
      Log.warn("0xA4066 Empty input section in report " + report.toString());
      return;
    }
    else {
      Optional<InputStory> mainInput = parseMainInput(line);
      if (!mainInput.isPresent()) {
        Log.warn("0xA4067 Failed to parse main input from report " + report.toString());
      }
      mainInputStory = mainInput.get();
      line = it.next();
    }
    
    // collect all the input files mentioned in the report
    while (!line.equals(InputOutputFilesReporter.USER_TEMPLATE_HEADING) && it.hasNext()) {
      Optional<InputStory> inputStory = parseInput(line);
      if (inputStory.isPresent()) {
        String input = calculateInputFileNameFrom(inputStory.get().parentPath,
            inputStory.get().inputPath);
        inputStories.put(input, inputStory.get());
      }
      line = it.next();
    }
    
    // again we skip a line (here it's the user template heading)
    if (it.hasNext()) {
      line = it.next();
    }
    
    // collect all the hwc files associated with the input file(s)
    while (!line.equals(InputOutputFilesReporter.HWC_FILE_HEADING) && it.hasNext()) {
      templateStories.add(line);
      line = it.next();
    }

    // again we skip a line (here it's the hwc heading)
    if (it.hasNext()) {
      line = it.next();
    }
    
    // collect all the hwc files associated with the input file(s)
    while (!line.equals(InputOutputFilesReporter.OUTPUT_FILE_HEADING) && it.hasNext()) {
      hwcStories.add(line);
      line = it.next();
    }
    
    // again we skip a line (here it's the output heading)
    if (it.hasNext()) {
      line = it.next();
    }
    
    // collect all the output files associated with the input file(s)
    while (!line.equals(InputOutputFilesReporter.FOOTER_HEADING) && it.hasNext()) {
      outputStories.add(line);
      line = it.next();
    }
    
    inputOutputMap.put(mainInputStory.inputPath, new InputOutputStory(mainInputStory, inputStories,
        templateStories, hwcStories, outputStories));
  }
  
  /**
   * Calculate the MD5 checksum for the given file.
   * 
   * @param file
   * @return
   */
  public static String getChecksum(String file) {
    try {
      return com.google.common.io.Files.hash(new File(file), Hashing.md5()).toString();
    }
    catch (IOException e) {
      Log.error("0xA1021 Failed to calculate current checksum for file " + file, e);
      return "";
    }
  }
  
}
