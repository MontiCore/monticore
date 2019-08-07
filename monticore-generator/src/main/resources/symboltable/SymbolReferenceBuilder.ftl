${signature("className", "languageName", "symbolReferenceName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};


public class ${className}{

protected String name;

protected I${languageName}Scope enclosingScopeOfReference;

protected ${className}(){

}

public ${symbolReferenceName} build(){
${symbolReferenceName} symbolReference = new ${symbolReferenceName}(name, enclosingScopeOfReference);
return symbolReference;
}

public ${className} setName(String name){
this.name = name;
return this;
}

public String getName(){
return this.name;
}

public ${className} setEnclosingScopeOfReference(I${languageName}Scope enclosingScopeOfReference){
this.enclosingScopeOfReference = enclosingScopeOfReference;
return this;
}

public I${languageName}Scope getEnclosingScopeOfReference(){
return this.enclosingScopeOfReference;
}

}
