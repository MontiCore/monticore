<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "astImports", "astClasses")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import de.se_rwth.commons.logging.Log;

<#-- handle ast imports from the super grammars -->
<#list astImports as astImport>
import ${astImport};
</#list>


public class ${ast.getName()} extends EFactoryImpl {
  
  // Creates the default factory implementation.
  public static ${ast.getName()} getFactory() {
    try {
      ${ast.getName()} eFactory = (${ast.getName()})EPackage.Registry.INSTANCE.getEFactory("${genHelper.getPackageURI()}"); 
      if (eFactory != null) {
        return eFactory;
     }
    }
    catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    if (factory == null) {
      factory = new ${ast.getName()}();
    }
    return factory;
  }

  
  protected static ${ast.getName()} factory = null;
  
  @Override
  public EObject create(EClass eClass) {
    switch (eClass.getClassifierID()) {
    <#list astClasses as astClass>
      case ${genHelper.getCdName()}Package.${astClass}: return ${ast.getName()}.create${astClass}();
    </#list>
    <#-- eFactoryImplReflectiveCreateMethod -->
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }
  
  // Returns the package supported by this factory.
  ${genHelper.getCdName()}Package get${genHelper.getCdName()}Package() {
    return (${genHelper.getCdName()}Package)getEPackage();
  }
    
<#list ast.getCDAttributeList() as attribute>
  ${tc.includeArgs("ast.Attribute", [attribute, ast])}
</#list>

  protected ${ast.getName()} () {}

  <#-- generate all methods -->
<#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
