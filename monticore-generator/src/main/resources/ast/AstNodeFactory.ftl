<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "astImports")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;

<#-- handle ast imports from the super grammars -->
<#list astImports as astImport>
import ${astImport};
</#list>


public class ${ast.getName()} {
  
  private static ${ast.getName()} getFactory() {
    if (factory == null) {
      factory = new ${ast.getName()}();
    }
    return factory;
  }
  
  protected static ${ast.getName()} factory = null;

<#list ast.getCDAttributeList() as attribute>
  ${tc.includeArgs("ast.Attribute", [attribute, ast])}
</#list>

  protected ${ast.getName()} () {}

  <#-- generate all methods -->
<#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
