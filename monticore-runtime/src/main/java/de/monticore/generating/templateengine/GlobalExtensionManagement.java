/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.freemarker.SimpleHashFactory;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;

/**
 * Class for managing hook points, features and (global) variables in templates.
 *
 * @author Pedram Nazari, Alexander Roth
 */
public class GlobalExtensionManagement {


// TODO MB:
// Auch hier wird eine künstliche Unterscheidung zwischen Hook Points mit
// explizitem Namen und Templates gemacht.
// Dabei ist das so einfach und systematisch:
// Hookpoint hat einen namen! (= String oder Template name, was ja auch ein String ist)
// In before, after und replace wird nachgesehen, wie im text beschrieben
//
  private SimpleHash globalData = SimpleHashFactory.getInstance().createSimpleHash();

  // use these list to handle replacements aka template forwardings
  private final Multimap<String, HookPoint> before = ArrayListMultimap.create();

  private final Multimap<String, HookPoint> replace = ArrayListMultimap.create();

  private final Multimap<String, HookPoint> after = ArrayListMultimap.create();

  private final Map<String, Map<ASTNode, HookPoint>> specificReplacement = Maps.newHashMap();

  /**
   * Map of all hook points
   */
  private final Map<String, HookPoint> hookPoints = Maps.newHashMap();

  public GlobalExtensionManagement() {
  }

  /**
   * Set a list of global data. The parameter should not be null.
   *
   * @param data list of global data
   */
  public void setGlobalData(SimpleHash data) {
    Log.errorIfNull(data);

    this.globalData = data;
  }

  /**
   * Retrieve a list of all global data
   */
  SimpleHash getGlobalData(){
    return this.globalData;
  }

  /**
   * Checks whether a value with the given name is defined and is not null
   *
   * @param name of the value to check
   * @return true if a variable exists and its value is not null
   */
  public boolean hasGlobalVar(String name) {
    try {
      return globalData.get(name) != null;
    }
    catch (TemplateModelException e) {
      Log.error("0xA7123 Internal Error on global value for \"" + name + "\"");
      return false;
    }
  }

  /**
   * Sets a new value which can be accessed with the given name in the
   * templates. If the name is already in use an error is reported the previous
   * value is overridden.
   *
   * @param name of the value to set
   * @param value the actual content
   */
  public void setGlobalValue(String name, Object value) {
    Log.errorIfNull(name);

    Reporting.reportSetValue(name, value);
    globalData.put(name, value);
  }

  /**
   * Defines a new value which can be accessed with the given name in the
   * templates. If the name is already in use an error is reported.
   *
   * @param name of the value to set
   * @param value the actual content
   */
  public void defineGlobalVar(String name, Object value) {
    if (hasGlobalVar(name)) {
      Log.error("0xA0122 Global Value '" + name + "' has already been set.\n Old value: " +
          getGlobalVar(name) + "\n New value: " + value);
    }
    setGlobalValue(name, value);
  }

  /**
   * Changes the value of an existing global variable. If the name is
   * not in use an error is reported.
   *
   * @param name of the value to set
   * @param value the actual content
   */
  public void changeGlobalVar(String name, Object value) {
    if (!hasGlobalVar(name)) {
      Log.error("0xA0124 Global Value '" + name + "' has not been defined.");
    } else {
      setGlobalValue(name, value);
    }
  }

  /**
   * Adds a new value to the given name. It converts a single value into a list
   * if necessary.
   *
   * @param name of the value to set
   * @param value the actual content
   */
  @SuppressWarnings({ "unchecked", "deprecation" })
  public void addToGlobalVar(String name, Object value) {
    if (hasGlobalVar(name)){
      Object currentValue = null;
      try {
        currentValue = BeansWrapper.getDefaultInstance().unwrap(globalData.get(name));
      }
      catch (TemplateModelException e) {
        Log.error("0xA8123 Internal Error on global value for \"" + name + "\"");
      }

      Collection<Object> newValue = new ArrayList<>();
      // the global variable has already a value assigned
      if (currentValue != null) {
        if (currentValue instanceof Collection<?>) {
          newValue.addAll((Collection<Object>) currentValue);
        }
        else {
          newValue.add(currentValue);
        }
        if (value instanceof Collection<?>) {
          newValue.addAll((Collection<Object>) value);
          Reporting.reportAddValue(name, value, newValue.size());
        }
        else {
          newValue.add(value);
          Reporting.reportAddValue(name, value, 1);
        }
        setGlobalValue(name, newValue);
      }
      // the variable has been defined but it has no value assigned
      else {
        if (value instanceof Collection<?>) {
          newValue.addAll((Collection<Object>) value);
        }else {
          newValue.add(value);
        }
        setGlobalValue(name, newValue);
      }
    } else {
      Log.error("0xA8124 Global value with name \"" + name + "\" does not exist!");
    }
  }

  /**
   * Returns the value of the given variable.
   *
   * @param name of the variable
   * @return the value
   */
  @SuppressWarnings("deprecation")
  public Object getGlobalVar(String name) {
    try {
      return BeansWrapper.getDefaultInstance().unwrap(globalData.get(name));
    }
    catch (TemplateModelException e) {
      Log.error("0xA0123 Internal Error on global value for \"" + name + "\"");
    }
    return null;
  }

  /**
   * Returns the value of the given variable.
   *
   * @param name of the variable
   * @param default replaces if the variable is not present
   * @return the value or the default
   */
  @SuppressWarnings("deprecation")
  public Object getGlobalVar(String name, Object defaultObject) {
    if (hasGlobalVar(name)) {
      try {
        return BeansWrapper.getDefaultInstance().unwrap(globalData.get(name));
      }
        catch (TemplateModelException e) {
          Log.error("0xA0123 Internal Error on global value for \"" + name + "\"");
        }
    }
    return defaultObject;
  }


  /**
   * check whether the variable name (parameter) is defined: if not issue an
   * error and continue
   *
   * @param name variable name
   */
  public void requiredGlobalVar(String name) {
    if (getGlobalVar(name) == null) {
      Log.error("0xA0126 Missing required value \"" + name + "\"");
    }
  }

  /**
   * check whether the list of variable names (parameter) is defined: if not
   * issue an error and continue
   *
   * @param names list of variable names
   */
  public void requiredGlobalVars(String... names) {
    for (int i = 0; i < names.length; i++){
      requiredGlobalVar(names[i]);
    }
  }

  
  // ----------------------------------------------------------------------
  // Section on Hook Points
  // ----------------------------------------------------------------------


  /**
   * @param hookName name of the hook point
   * @param content String to be used as hook point
   */
  public void bindStringHookPoint(String hookName, String content) {
    bindHookPoint(hookName, new StringHookPoint(content));
  }

  /**
   * @param hookName name of the hook point
   * @param content Template-content to be used as hook point
   */
  public void bindTemplateStringHookPoint(String hookName, String content) {
    try {
      bindHookPoint(hookName, new TemplateStringHookPoint(content));
    }
    catch (IOException e) {
      Log.error("0xA7124 Cannot bind hookpoint " + hookName);
    }
  }

  /**
   * @param hookName name of the hook point
   * @param tpl Template to be used as hook point
   */
  public void bindTemplateHookPoint(String hookName, String tpl) {
    bindHookPoint(hookName, new TemplateHookPoint(tpl));
  }

  /**
   * @param hookName name of the hook point
   * @param hp
   */
  public void bindHookPoint(String hookName, HookPoint hp) {
    Reporting.reportSetHookPoint(hookName, hp);
    warnIfHookPointExists(hookName);
    hookPoints.put(hookName, hp);
  }

  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, ASTNode ast) {

    String result = null;
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, ast);

    if (hookPoints.containsKey(hookName)) {
      result = hp.processValue(controller, ast);
    }

    Reporting.reportCallHookPointEnd(hookName);

    return Strings.nullToEmpty(result);
  }

  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, ASTNode ast, Object... args) {

    String result = null;
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, ast);

    if (hookPoints.containsKey(hookName)) {
      result = hp.processValue(controller, ast, Arrays.asList(args));
    }

    Reporting.reportCallHookPointEnd(hookName);

    return Strings.nullToEmpty(result);
  }

  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName, Object... args) {

    String result = null;
    HookPoint hp = hookPoints.get(hookName);
    Reporting.reportCallHookPointStart(hookName, hp, controller.getAST());

    if (hookPoints.containsKey(hookName)) {
      result = hp.processValue(controller, Arrays.asList(args));
    }

    Reporting.reportCallHookPointEnd(hookName);

    return Strings.nullToEmpty(result);
  }

  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public String defineHookPoint(TemplateController controller, String hookName) {
    return defineHookPoint(controller, hookName, controller.getAST());
  }

  /**
   * @param hookName name of the hook point
   * @return the (processed) value of the hook point
   */
  public boolean existsHookPoint(String hookName) {
    return hookPoints.containsKey(hookName);
  }

  /**
   * Returns a set of templates that have been defined to replace the template
   * <code>templateName</code>. If no template forwardings have been defined,
   * then <code>templateName</code> is returned.
   *
   * @param templateName The name of the template
   * @return A list of templates that have been defined to replace the
   * 'templateNames' templates
   */
  protected List<HookPoint> getTemplateForwardings(String templateName, ASTNode ast) {
    List<HookPoint> replacements = Lists.newArrayList();
    Collection<HookPoint> before = this.before.get(templateName);
    Collection<HookPoint> after = this.after.get(templateName);

    if (before != null) {
      replacements.addAll(before);
      Reporting.reportCallBeforeHookPoint(templateName, before, ast);
    }

    List<HookPoint> hps = getSpecificReplacement(templateName, ast);
    if(hps != null){
      Reporting.reportCallSpecificReplacementHookPoint(templateName, hps, ast);
    }
    else {
      hps = getTemplateForwardingsX(templateName, ast);
    }
    replacements.addAll(hps);

    if (after != null) {
      replacements.addAll(after);
      Reporting.reportCallAfterHookPoint(templateName, after, ast);
    }
    return replacements;
  }

  protected List<HookPoint> getSpecificReplacement(String templateName, ASTNode ast) {
    Map<ASTNode, HookPoint> replacedTemplates = this.specificReplacement.get(templateName);
    if (replacedTemplates != null && replacedTemplates.containsKey(ast)) {
      return Lists.newArrayList(replacedTemplates.get(ast));
    }
    return null;
  }

  /**
   * Returns a set of templates that have been defined to replace the
   * 'templateName' template. If no template forwardings have been defined, then
   * the 'templateName' template is returned.
   *
   * @param templateName The name of the template
   * @return A list of templats that have been defined to replace the
   * 'templateName' template
   */
  protected List<HookPoint> getTemplateForwardingsX(String templateName, ASTNode ast) {
    List<HookPoint> forwardings = Lists.newArrayList();

    if (containsTemplateForwarding(templateName)) {
      if(this.replace.containsKey(templateName)){
        forwardings.addAll(this.replace.get(templateName));
        Reporting.reportCallReplacementHookPoint(templateName, forwardings, ast);
      } else{
        forwardings.addAll(Lists.newArrayList(new TemplateHookPoint(templateName)));
      }
    }
    else {
      forwardings.add(new TemplateHookPoint(templateName));
      Reporting.reportExecuteStandardTemplate(templateName, ast);
    }
    return forwardings;
  }

  private boolean containsTemplateForwarding(String templateName) {
    return this.before.containsKey(templateName)
        | this.replace.containsKey(templateName)
        | this.after.containsKey(templateName);
  }

  /**
   * Future inclusion of 'oldTemplate' will be replaced by 'newTemplate'. NOTE:
   * This replacement has only an effect if 'oldTemplate' is included directly.
   *
   * @param oldTemplate qualified name of template to be replaced
   */
  public void replaceTemplate(String oldTemplate, HookPoint hp) {
    replaceTemplate(oldTemplate, Lists.newArrayList(hp));
  }

  /**
   * Future inclusion of 'oldTemplate' will be replaced by list of
   * 'newTemplates'. NOTE: This replacement has only an effect if 'oldTemplate'
   * is included directly.
   *
   * @param oldTemplate qualified name of template to be replaced
   */
  public void replaceTemplate(String oldTemplate, List<? extends HookPoint> newHps) {
    Reporting.reportTemplateReplacement(oldTemplate, newHps);

    if (!newHps.isEmpty()) {
      // remove all previous replacements
      this.replace.removeAll(oldTemplate);
      this.replace.putAll(oldTemplate, newHps);
    }
    else {
      this.replace.removeAll(oldTemplate);
    }
  }

  public void replaceTemplate(String oldTemplate, ASTNode node, HookPoint newHp) {
    Reporting.reportASTSpecificTemplateReplacement(oldTemplate, node, newHp);

    Map<ASTNode, HookPoint> replacedTemplates = this.specificReplacement.get(oldTemplate);
    if (replacedTemplates != null && !replacedTemplates.containsKey(node)) {
      replacedTemplates.put(node, newHp);
    }
    else {
      Map<ASTNode, HookPoint> specificTemplate = Maps.newHashMap();
      specificTemplate.put(node, newHp);
      this.specificReplacement.put(oldTemplate, specificTemplate);
    }
  }

  /**
   * Everytime 'template' is included directly (e.g. by
   * {@link TemplateController#include(String, ASTNode)}), 'beforeTemplate' will
   * be included before it.
   *
   * @param template qualified name of the template
   */
  public void setBeforeTemplate(String template, HookPoint beforeHp) {
    setBeforeTemplate(template, Lists.newArrayList(beforeHp));
  }

  /**
   * Everytime 'template' is included directly (e.g. by
   * {@link TemplateController#include(String, ASTNode)}), the templates in
   * 'beforeTemplate' will be included before it.
   *
   * @param template qualified name of the template
   */
  public void setBeforeTemplate(String template, List<? extends HookPoint> beforeHps) {
    Reporting.reportSetBeforeTemplate(template, beforeHps);

    // remove all previous replacements
    this.before.removeAll(template);
    if (!beforeHps.isEmpty()) {
      this.before.putAll(template, beforeHps);
    }
  }

  /**
   * Everytime 'template' is included directly (e.g. by
   * {@link TemplateController#include(String, ASTNode)}), 'afterTemplate' will
   * be included after it.
   *
   * @param template qualified name of the template
   */
  public void setAfterTemplate(String template, HookPoint afterHp) {
    setAfterTemplate(template, Lists.newArrayList(afterHp));
  }

  /**
   * Everytime 'template' is included directly (e.g. by
   * {@link TemplateController#include(String, ASTNode)}), the templates in
   * 'afterTemplate' will be included after it.
   *
   * @param template qualified name of the template
   */
  public void setAfterTemplate(String template, List<? extends HookPoint> afterHps) {
    Reporting.reportSetAfterTemplate(template, afterHps);

    // remove all previous replacements
    this.after.removeAll(template);
    if (!afterHps.isEmpty()) {
      this.after.putAll(template, afterHps);
    }
  }

  private void warnIfHookPointExists(String hookName) {
    if (hookPoints.containsKey(hookName)) {
      Log.warn("0xA1036 Hook point '" + hookName + "' is already defined. It will be overwritten.");
    }
  }
}
