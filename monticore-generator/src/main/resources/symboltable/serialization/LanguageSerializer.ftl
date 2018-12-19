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

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.serializing.ArtifactScopeSerialization;
import de.monticore.symboltable.serializing.CommonLanguageSerialization;
import de.monticore.symboltable.serializing.DelegatingSerializer;
import de.monticore.symboltable.serializing.ISerialization;

public class ${languageName}Language${serializationSuffix} extends CommonLanguageSerialization {

  public ${languageName}Language${serializationSuffix}() {
    super();
    List<ISerialization<?>> serializers = ImmutableList.of(
  <#list genHelper.getAllSymbolDefiningRules() as symbol>
      new ${symbol}Symbol${serializationSuffix}(),
  </#list>
      new ${languageName}Scope${serializationSuffix}()
    );
    
    DelegatingSerializer delegatingSerializer = new DelegatingSerializer(serializers);
    
    gson.registerTypeAdapter(ArtifactScope.class, new ArtifactScopeSerialization());
    gson.registerTypeAdapter(MutableScope.class, delegatingSerializer);
    gson.registerTypeAdapter(Symbol.class, delegatingSerializer);
    
    for (ISerialization<?> serializer : serializers) {
      gson.registerTypeAdapter(serializer.getSerializedClass(), serializer);
    }
  }
}
