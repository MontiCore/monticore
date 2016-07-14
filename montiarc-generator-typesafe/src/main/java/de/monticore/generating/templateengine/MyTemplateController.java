/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating.templateengine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import de.monticore.ast.ASTNode;
import freemarker.core.Macro;
import freemarker.template.Template;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MyTemplateController extends TemplateController {
  
  /**
   * Ensures tc.params(...) is only called once
   */
  private boolean parametrized = false;
  
  /**
   * Ensures tc.result(...) is only called once
   */
  private boolean resultized = false;
  
  private TemplateControllerConfiguration tcConfig;
  
  private static final String MYALIASES_TEMPLATE = "de.monticore.generating.templateengine.freemarker.MyAliases";
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyTemplateController
   * 
   * @param tcConfig
   * @param templatename
   */
  public MyTemplateController(TemplateControllerConfiguration tcConfig, String templatename) {
    super(tcConfig, templatename);
    this.tcConfig = tcConfig;
  }
  
  /**
   * @see de.monticore.generating.templateengine.TemplateController#processTemplate(java.lang.String,
   * de.monticore.ast.ASTNode, java.util.List)
   */
  @Override
  public String processTemplate(String templateName, ASTNode astNode,
      List<Object> passedArguments) {
    return super.processTemplate(templateName, astNode, passedArguments);
  }
  
  /**
   * Compares types of the passed params with the arguments passed in the
   * template call. Additionally signatures the names of the parameters. Method
   * is important for inter Template calls and for users who do not use the
   * generated template class with its static generate method.
   * 
   * @param params
   */
  public void params(String... params) {
    checkArgument(!parametrized,
        "0xA5297 Template '" + getTemplatename() + "': tried to invoke params() twice");
    List<Object> arguments = getArguments();
    List<String> names = getSignature();
    checkArgument(params.length == arguments.size(),
        "0xA5298 Template '" + getTemplatename() + "': Parameter size (#" +
            params.length +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");
    List<String> toSignature = new ArrayList<String>();
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      String parameter = params[i];
      String paramType = parameter.substring(0, parameter.indexOf(" "));
      if (paramType.contains("<")) {
        paramType = paramType.substring(0, paramType.indexOf("<"));
      }
      String paramName = parameter.substring(parameter.indexOf(" ") + 1);
      Class argumentClass = argument.getClass();
      String argumentClassName = argumentClass.getName();
      
      if (!paramType.equals(argumentClassName)) {
        Optional<Class> javaLibraryType = getJavaLibraryType(paramType);
        boolean isAssignable = false;
        if (javaLibraryType.isPresent()) {
          isAssignable = javaLibraryType.get().isAssignableFrom(argumentClass);
          if (!isAssignable) {
            isAssignable = Number.class.isAssignableFrom(javaLibraryType.get())
                && Number.class.isAssignableFrom(argumentClass);
          }
        }
        if (!isAssignable || !javaLibraryType.isPresent()) {
          checkArgument(true, "0xA5301 Template '"
              + getTemplatename() + "': passed argument type (" +
              argumentClassName +
              ") and type of signature parameter (" + paramType + " " + paramName + ") mismatch.");
        }
      }
      
      // Case 1: No Signature -> we have to signature the paramnames
      if (names.isEmpty()) {
        toSignature.add(paramName);
      }
      // Case 2: User wrote signature() additionally to params() -> we do not
      // need to signature the parameter names, but compare them to the
      // parameter names in the params() method.
      else {
        String argumentName = names.get(i);
        checkArgument(argumentName.equals(paramName),
            "0xA5300 Template '" + getTemplatename() + "': Parameter name (" +
                paramName +
                ") and name of parameter in signature (" + argumentName + ") mismatch.");
      }
    }
    
    if (!toSignature.isEmpty()) {
      signature(toSignature);
    }
    parametrized = true;
  }
  
  /**
   * TODO: Write me!
   *
   * @param argumentClass
   * @return
   */
  private Optional<Class> getJavaLibraryType(String paramType) {
    Map<String, String> primitiveTypes = new HashMap<String, String>();
    primitiveTypes.put("long", "java.lang.Long");
    primitiveTypes.put("int", "java.lang.Integer");
    primitiveTypes.put("short", "java.lang.Short");
    primitiveTypes.put("double", "java.lang.Double");
    primitiveTypes.put("float", "java.lang.Float");
    primitiveTypes.put("byte", "java.lang.Byte");
    primitiveTypes.put("char", "java.lang.Character");
    primitiveTypes.put("boolean", "java.lang.Boolean");
    
    String[] packagesToSearch = { "java.lang", "java.util" };
    Optional<Class> fqnLibraryType = Optional.empty();
    for (String _package : packagesToSearch) {
      try {
        Class c = Class.forName(_package + "." + paramType);
        return Optional.of(c);
      }
      catch (ClassNotFoundException e) {
        
      }
    }
    try {
      Class c = Class.forName(paramType);
      return Optional.of(c);
    }
    catch (ClassNotFoundException e) {
    }
    
    if (primitiveTypes.containsKey(paramType)) {
      Class c;
      try {
        c = Class.forName(primitiveTypes.get(paramType));
        return Optional.of(c);
        
      }
      catch (ClassNotFoundException e) {
      }
      
    }
    return fqnLibraryType;
  }
  
  /**
   * @see de.monticore.generating.templateengine.TemplateController#initAliases()
   */
  @Override
  protected void initAliases() {
    super.initAliases();
    List<Macro> aliases = newArrayList();
    
    Template aliasesTemplate = tcConfig.getFreeMarkerTemplateEngine().loadTemplate(
        MYALIASES_TEMPLATE);
    Set macros = aliasesTemplate.getMacros().entrySet();
    for (Object o : macros) {
      Entry e = (Entry) o;
      Macro macro = (Macro) e.getValue();
      if (null != this.getAliases()) {
        this.getAliases().add(macro);
      }
    }
  }
  
  /**
   * Checks whether there are more than one result definitions.
   * 
   * @param result
   */
  public void result(String result) {
    checkArgument(!resultized,
        "0xA5301 Template '" + getTemplatename() + "': tried to invoke result() twice");
    resultized = true;
    
  }
  
}
