<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName","serializationSuffix","rules")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.List;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.serializing.CommonLanguageSerialization;
import de.monticore.symboltable.serializing.ISerialization;

public class ${languageName}Language${serializationSuffix} extends CommonLanguageSerialization {


  @Override
  protected List<ISerialization<?>> getSerializers() {
    return ImmutableList.of(
      //add symbols
  <#list genHelper.getAllSymbolDefiningRules() as symbol>
      new ${symbol}Symbol${serializationSuffix}(),
  </#list>
      //add language scope
      new ${languageName}Scope${serializationSuffix}()
      );
  }
  
}
