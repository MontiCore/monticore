/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;


import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import org.apache.tools.ant.taskdefs.Java;

import java.util.*;

/**
 * Provides JavaDoc builder-likes to be used with the CD generator
 */
public class JavaDoc {

  protected final List<String> description = new ArrayList<>();
  protected final Map<String, String> blocks = new LinkedHashMap<>();

  /**
   * Adds to the description part of the JavaDoc (above the blocks)
   *
   * @param descriptions the lines
   */
  public JavaDoc description(String... descriptions) {
    for (String description : descriptions)
      this.description.add(description);
    return this;
  }

  public JavaDoc param(String name, String descr) {
    return this.block("param " + name, descr);
  }

  /**
   * Adds a black to the JavaDoc.
   * Blocks follow the description and their tags prefixed with an @ and padded to a common width
   *
   * @param tag     the tag, such as return, see, param $name, ...
   * @param content the content of the block
   */
  public JavaDoc block(String tag, String content) {
    this.blocks.put(tag, content);
    return this;
  }

  public String makeString() {
    StringBuilder builder = new StringBuilder();
    builder.append("/**\n");

    // Write the description
    for (String descriptionLine : this.description) {
      builder.append("* ").append(descriptionLine).append("\n");
    }
    builder.append("*\n");
    // Next, pad the blocks
    if (!this.blocks.isEmpty()) {
      int maxBlockTagSize = this.blocks.keySet().stream().map(String::length).max(Integer::compare).get() + 1;
      for (Map.Entry<String, String> e : this.blocks.entrySet()) {
        builder.append("* ")
                .append("@") // prefix @
                .append(String.format("%-" + maxBlockTagSize + "s", e.getKey())) // pad to common length
                .append(e.getValue())
                .append("\n");
      }
    }

    builder.append("*/");

    return builder.toString();
  }

  /**
   * @return This JavaDoc as a {@link HookPoint}
   */
  public HookPoint asHP() {
    return new StringHookPoint(this.makeString());
  }

  /**
   * Creates a new JavaDoc (builder-like) to describe a CD element.
   */
  public static JavaDoc of(String... description) {
    return new JavaDoc().description(description);
  }
}
