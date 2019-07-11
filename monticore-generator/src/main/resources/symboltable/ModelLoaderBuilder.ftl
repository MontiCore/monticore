<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "languageName")}

<#assign modelLoaderName = languageName + "ModelLoader">
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${genHelper.getTargetPackage()};

public class ${className} {

    protected ${languageName}Language language;

    protected ${className}(){}

    public ${modelLoaderName} build(){
        ${modelLoaderName} obj = new ${modelLoaderName}(language);
        return obj;
    }

    public ${languageName}Language getModelingLanguage(){
        return this.language;
    }

    public ${className} setModelingLanguage(${languageName}Language language){
        this.language = language;
        return this;
    }
}
