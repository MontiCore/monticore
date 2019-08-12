<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","languageName")}

<#assign scopeName=languageName+"Scope">
<#assign artifactScopeName = languageName+"ArtifactScope">
<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}


package ${genHelper.getTargetPackage()};

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import de.monticore.symboltable.ImportStatement;

public class ${className}{

protected String packageName;

protected List<ImportStatement> imports = new ArrayList<>();

    protected I${scopeName} enclosingScope;

    public ${artifactScopeName} build(){
    ${artifactScopeName} scope;
    if(enclosingScope==null){
    scope = new ${artifactScopeName}(packageName, imports);
    }else{
    scope = new ${artifactScopeName}(Optional.of(enclosingScope), packageName, imports);
    }
    return scope;
    }

    public ${className} setEnclosingScope(I${scopeName} enclosingScope){
    this.enclosingScope = enclosingScope;
    return this;
    }

    public I${scopeName} getEnclosingScope(){
    return this.enclosingScope;
    }

    public ${className} setPackageName(String packageName){
    this.packageName = packageName;
    return this;
    }

    public String getPackageName(){
    return this.packageName;
    }

    public ${className} setImportList(List<ImportStatement> imports){
        this.imports = imports;
        return this;
        }

        public List<ImportStatement> getImportList(){
            return this.imports;
            }

            public ${className} addImport(ImportStatement statement){
            imports.add(statement);
            return this;
            }

            public ${className} removeImport(ImportStatement statement){
            imports.remove(statement);
            return this;
            }
            }
