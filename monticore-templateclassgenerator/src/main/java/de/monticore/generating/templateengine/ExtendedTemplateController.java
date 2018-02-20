/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;

/**
 * This class adds the signature methods tc.params(...) and tc.result(...) to
 * the existing TemplateController.
 *
 * @author Jerome Pfeiffer
 */
public class ExtendedTemplateController extends TemplateController {
  
  /**
   * Ensures tc.params(...) is only called once
   */
  private boolean parametrized = false;
  
  /**
   * Ensures tc.result(...) is only called once
   */
  private boolean resultized = false;
  
  /**
   * Constructor for de.montiarc.generator.codegen.MyTemplateController
   * 
   * @param tcConfig
   * @param templatename
   */
  public ExtendedTemplateController(GeneratorSetup setup, String templatename) {
    super(setup, templatename);
  }
  
  /**
   * Compares types of the passed params with the arguments passed in the
   * template call. Additionally signatures the names of the parameters. Method
   * is important for inter Template calls and for users who do not use the
   * generated template class with its static generate method. All types that
   * are no java library types have to be defined fully qualified.
   * 
   * @param params
   */
  public void params(String... params) {
    checkArgument(!parametrized,
        "0xA5299 Template '" + getTemplatename() + "': tried to invoke params() twice");
    List<Object> arguments = getArguments();
    // throws error when argument size != param size
    checkArgument(params.length == arguments.size(),
        "0xA5296 Template '" + getTemplatename() + "': Parameter size (#" +
            params.length +
            ") and number of arguments (#" + arguments.size() + ") mismatch.");
    List<String> toSignature = new ArrayList<String>();
    
    // compares params and args
    for (int i = 0; i < arguments.size(); i++) {
      Object argument = arguments.get(i);
      String parameter = params[i];
      String paramType = parameter.substring(0, parameter.indexOf(" "));
      paramType = paramType.trim();
      if (paramType.contains("<")) {
        paramType = paramType.substring(0, paramType.indexOf("<"));
      }
      String paramName = parameter.substring(parameter.indexOf(" ") + 1);
      Class argumentClass = argument.getClass();
      String argumentClassName = argumentClass.getName();
      
      // checks whether argument type and param type are equal try with java
      // library types otherwise
      if (!paramType.equals(argumentClassName)) {
        Optional<Class> javaLibraryType = getJavaLibraryType(paramType);
        boolean isAssignable = false;
        if (javaLibraryType.isPresent()) {
          isAssignable = javaLibraryType.get().isAssignableFrom(argumentClass);
          // if it is not assignable with java library type it might be a number
          if (!isAssignable) {
            isAssignable = Number.class.isAssignableFrom(javaLibraryType.get())
                && Number.class.isAssignableFrom(argumentClass);
          }
        }
        checkArgument(isAssignable, "0xA5301 Template '"
            + getTemplatename() + "': passed argument type (" +
            argumentClassName +
            ") and type of signature parameter (" + paramType + " " + paramName + ") mismatch.");
      }
      
      // Case 1: No Signature -> we have to signature the paramnames
//      if (names.isEmpty()) {
        toSignature.add(paramName);
//      }
      // TODO MB  signature?
//      // Case 2: User wrote signature() additionally to params() -> we do not
//      // need to signature the parameter names, but compare them to the
//      // parameter names in the params() method.
//      else {
//        String argumentName = names.get(i);
//        checkArgument(argumentName.equals(paramName),
//            "0xA5300 Template '" + getTemplatename() + "': Parameter name (" +
//                paramName +
//                ") and name of parameter in signature (" + argumentName + ") mismatch.");
//      }
    }
    
    if (!toSignature.isEmpty()) {
      signature(toSignature);
    }
    parametrized = true;
  }
  
  /**
   * Loads java library types to the passed {@link paramType}
   *
   * @param argumentClass
   * @return
   */
  private Optional<Class> getJavaLibraryType(String paramType) {
    // maps primitive data types to wrapper classes
    Map<String, String> primitiveTypes = new HashMap<String, String>();
    primitiveTypes.put("long", "java.lang.Long");
    primitiveTypes.put("int", "java.lang.Integer");
    primitiveTypes.put("short", "java.lang.Short");
    primitiveTypes.put("double", "java.lang.Double");
    primitiveTypes.put("float", "java.lang.Float");
    primitiveTypes.put("byte", "java.lang.Byte");
    primitiveTypes.put("char", "java.lang.Character");
    primitiveTypes.put("boolean", "java.lang.Boolean");
    
    // 1. tries to load the passed type
    try {
      Class c = Class.forName(paramType);
      return Optional.of(c);
    }
    catch (ClassNotFoundException e) {
//      Log.info("Class " + paramType + " not found!", "ExtendedTemplateController");
    }
    
    // 2. this packages are searched to find the fqn of the passed paramType
    String[] packagesToSearch = { "java.lang", "java.util" };
    Optional<Class> fqnLibraryType = Optional.empty();
    for (String _package : packagesToSearch) {
      try {
        Class c = Class.forName(_package + "." + paramType);
        return Optional.of(c);
      }
      catch (ClassNotFoundException e) {
//        Log.info("Class " + _package + "." + paramType + " not found!", "ExtendedTemplateController");
      }
    }
    
    // 3. try primitive types
    if (primitiveTypes.containsKey(paramType)) {
      Class c;
      try {
        c = Class.forName(primitiveTypes.get(paramType));
        return Optional.of(c);
        
      }
      catch (ClassNotFoundException e) {
//        Log.info("Class " + primitiveTypes.get(paramType) + " not found!", "ExtendedTemplateController");
      }
      
    }
    return fqnLibraryType;
  }
  
  /**
   * Checks whether there are more than one result definitions.
   * 
   * @param result
   */
  public void result(String result) {
    checkArgument(!resultized,
        "0xA5302 Template '" + getTemplatename() + "': tried to invoke result() twice");
    resultized = true;
    
  }
  
}
