<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import java.util.function.Predicate;
import java.util.Collection;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.MutableScope;

public interface ${interfaceName} {
 <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "SymbolVisitor")>
 public void accept(${langVisitorType} visitor);

}

