<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","languageName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${genHelper.getTargetPackage()};

import java.util.LinkedHashSet;
import java.util.Set;
import de.monticore.IModelingLanguage;

public class ${className} {

    private String name;
    private String fileEnding;

    protected ${className}(){}

    public ${languageName}Language build(){
        ${languageName}Language obj = new ${languageName}Language();
        return obj;
    }

<#--    public String getName(){-->
<#--        return name;-->
<#--    }-->

<#--    public String getFileExtension(){-->
<#--        return fileEnding;-->
<#--    }-->


<#--    public ${className} setName(String name){-->
<#--        this.name=name;-->
<#--        return this;-->
<#--    }-->

<#--    public ${className} setFileExtension(String fileEnding){-->
<#--        this.fileEnding=fileEnding;-->
<#--        return this;-->
<#--    }-->

<#--<#list symbolNames?keys as symbol>-->
<#--    public Set<String> calculateModelNamesFor${symbol.name}(String name){-->
<#--        final Set<String> modelNames = new LinkedHashSet<>();-->
<#--        modelNames.add(name);-->
<#--        return modelsNames;-->
<#--    }-->

<#--</#list>-->
}