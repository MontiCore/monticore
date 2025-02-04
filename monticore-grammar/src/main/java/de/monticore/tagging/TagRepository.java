/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import com.google.common.collect.Iterables;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTagUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TagRepository {

  // A cache for tag models loaded from files
  protected static Map<File, ASTTagUnit> loadedFileTags = new HashMap<>();

  // And a cache for temporary tag models
  protected static Map<String, ASTTagUnit> loadedTags = new HashMap<>();

  /**
   * Load a new tag model into this repository
   * @param file location of the tag model
   * @return an empty optional in case the parsing failed, otherwise the tag model
   */
  public static Optional<ASTTagUnit> loadTagModel(File file) throws IOException {
    Optional<ASTTagUnit> tagUnitOpt = TagsMill.parser().parse(file.getAbsolutePath());
    if (tagUnitOpt.isEmpty())
      return tagUnitOpt;
    loadedFileTags.put(file, tagUnitOpt.get());
    return tagUnitOpt;
  }

  /**
   * Create a new (temporary) tag model in this repository.
   * In case a tag model of this name is already present,
   *  this already present model is returned.
   * Otherwise, a new tag model with the given name is returned and stored in this repository
   * @param name the name of the tag model
   * @return a tag model with the given name
   */
  public static Optional<ASTTagUnit> loadTempTagModel(String name) {
    if (loadedTags.containsKey(name))
      return Optional.of(loadedTags.get(name));
    ASTTagUnit unit = TagsMill.tagUnitBuilder()
            .setName(name)
            .build();
    loadedTags.put(name, unit);
    return Optional.of(unit);
  }

  /**
   * Unload a given tag model
   * @param tagUnit the tag model
   * @return true iff the tag model was unloaded from this repository
   */
  public static boolean unloadTagModel(ASTTagUnit tagUnit) {
    return loadedFileTags.values().remove(tagUnit)
             | loadedTags.remove(tagUnit.getName()) != null;
  }

  /**
   * Unload a tag model by its file,
   *  loaded by {@link #loadTagModel(File)}
   * @param file location of the tag model
   * @return true if the model was stored in this repository
   */
  public static boolean unloadTagModel(File file){
    return loadedFileTags.remove(file) != null;
  }

  /**
   * Unload a (temporary) tag model by its name,
   *  loaded by {@link #loadTempTagModel(String)}
   * @param name the name given when loading the model
   * @return true if the model was stored in this repository
   */
  public static boolean unloadTempTagModel(String name){
    return loadedTags.remove(name) != null;
  }

  /**
   * @return an Iterable over all loaded tag models
   */
  public static Iterable<ASTTagUnit> getLoadedTagUnits() {
    return Iterables.concat(loadedFileTags.values(), loadedTags.values());
  }

  /**
   * Unload all tag models
   */
  public static void clearTags() {
    loadedFileTags.clear();
    loadedTags.clear();
  }
}

