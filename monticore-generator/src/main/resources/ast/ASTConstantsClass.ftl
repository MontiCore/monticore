<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "qualifiedCdName", "superGrammars")}

<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign nameHelper = glex.getGlobalVar("nameHelper")>

package ${genHelper.getAstPackage()};

import java.util.Collection;
import com.google.common.collect.Sets;

public class ${ast.getName()} extends Object {

  public static final String LANGUAGE = "${qualifiedCdName}";
  
  public static final int DEFAULT = 0;
<#list ast.getCDAttributeList() as constant>
  public static final int ${constant.getName()} = ${constant_index + 1};
</#list>

  <#assign del = " ">
  /** All direct super grammars.*/
  public static String[] superGrammars = {<#list superGrammars as superGrammar>${del}"${superGrammar}"<#assign del = ","></#list>};
  
  public static Collection<String> getAllLanguages() {
    Collection<String> allLanguages = Sets.newLinkedHashSet();
    <#list superGrammars as superGrammar>
    <#assign superPackage = astHelper.getAstPackage(superGrammar)>
    allLanguages.addAll(${superPackage}ASTConstants${nameHelper.getSimpleName(superGrammar)}.getAllLanguages());
    </#list>
    allLanguages.add(LANGUAGE);
    return allLanguages;
  }

  /** Constructs a new ASTConstantsFile.*/
  public ${ast.getName()}() {
  }

}
