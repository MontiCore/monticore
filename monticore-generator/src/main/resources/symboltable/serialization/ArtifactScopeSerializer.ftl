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
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.serializing.ArtifactScopeSerialization;
import de.monticore.symboltable.serializing.CommonArtifactScopeSerializer;
import de.monticore.symboltable.serializing.DelegatingSerializer;
import de.monticore.symboltable.serializing.ISerialization;

public class ${languageName}ArtifactScope${serializationSuffix} extends CommonArtifactScopeSerializer {

  public ${languageName}ArtifactScope${serializationSuffix}() {
    super();
    List<ISerialization<?>> serializers = ImmutableList.of(
  <#list genHelper.getAllSymbolDefiningRules() as symbol>
      new ${symbol}Symbol${serializationSuffix}(),
  </#list>
      new ${languageName}Scope${serializationSuffix}()
    );
    
    DelegatingSerializer symbolAndScopeSerializer = new DelegatingSerializer(serializers);
    
    gson.registerTypeAdapter(ArtifactScope.class, new ArtifactScopeSerialization());
    gson.registerTypeAdapter(Scope.class, symbolAndScopeSerializer);
    gson.registerTypeAdapter(Symbol.class, symbolAndScopeSerializer);
    
    for (ISerialization<?> serializer : serializers) {
      gson.registerTypeAdapter(serializer.getSerializedClass(), serializer);
    }
  }
}
