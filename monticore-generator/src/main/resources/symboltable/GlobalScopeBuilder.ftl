${signature("className", "languageName")}

<#assign globalScopeName = languageName+"GlobalScope">
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}


package ${genHelper.getTargetPackage()};

import de.monticore.io.paths.ModelPath;


public class ${className}{

protected ${languageName}Language language;

protected ModelPath modelPath;

public ${globalScopeName} build(){
${globalScopeName} scope = new ${globalScopeName}(modelPath, language);
return scope;
}

public ${className} setLanguage(${languageName}Language language){
this.language= language;
return this;
}

public ${languageName}Language getLanguage(){
return this.language;
}

public ModelPath getModelPath(){
return this.modelPath;
}

public ${className} setModelPath(ModelPath modelPath){
this.modelPath = modelPath;
return this;
}

}
