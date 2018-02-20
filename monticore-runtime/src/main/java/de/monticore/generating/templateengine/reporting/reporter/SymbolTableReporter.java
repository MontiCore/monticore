/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.CommonJFieldSymbol;
import de.monticore.symboltable.types.CommonJMethodSymbol;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.Names;


public class SymbolTableReporter extends AReporter {
  
  static final String SIMPLE_FILE_NAME = "13_SymbolTable";
  
  protected final String outputDir;
  
  protected final String modelName;
  
  protected final ReportingRepository repository;
  
  protected boolean printEmptyOptional = false;
  
  protected boolean printAllFieldsCommented = false;
  
  protected boolean printEmptyList = false;
  
  public SymbolTableReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        Names.getSimpleName(modelName) + "_ST", ReportingConstants.OD_FILE_EXTENSION);
    this.outputDir = outputDir;
    this.modelName = modelName;
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("//========================================================== Symbol Table");
  }
  
  private void writeFooter() {
    writeLine("//========================================================== Explanation");
    writeLine("//Shows symbol table after finishing the generation process.");
  }
  
  @Override
  public void flush(ASTNode ast) {
    if (ast != null) {
      Optional<? extends Scope> scope = ast.getSpannedScopeOpt();
      while (scope.isPresent() && !(scope.get() instanceof GlobalScope)) {
        scope = scope.get().getEnclosingScope();
      }
      if (scope.isPresent()) {
        reportSymbolTableScope(scope.get());
      }
    }
    writeFooter();
    super.flush(ast);
  }
  
  protected void reportScope(Scope scope, IndentPrinter printer) {
    final Collection<Symbol> symbols = Scopes.getLocalSymbolsAsCollection(scope);
    String type;
    String scopeName;
    scopeName = repository.getScopeNameFormatted(scope);
    type = Names.getSimpleName(scope.getClass().getName());
    printer.println(scopeName + ": " + type + "{");
    printer.indent();
    reportAllFields(scope.getClass(), printer);
    
    if (scope.getName().isPresent()) {
      printer.println("name = \"" + scope.getName().get() + "\";");
    } else if (printEmptyOptional) {
      printer.println("name = absent;");
    }

    if (scope instanceof ArtifactScope) {
      ArtifactScope artifactScope = (ArtifactScope) scope;
      if (!artifactScope.getImports().isEmpty()) {
        printer.print("imports = [\"");
        String sep = "";
        for (ImportStatement imp: artifactScope.getImports()) {
          printer.print(sep);
          sep = ", ";
          printer.print(imp.getStatement());
          printer.print("\"");
        }
        printer.println("];");
      } else if (printEmptyList) {
        printer.println("imports = [];");
      }
      
      printer.println("packageName = \"" + artifactScope.getPackageName() + "\";");
    }

    printer.println("isShadowingScope = " + scope.isShadowingScope() + ";");
    printer.println("exportsSymbols = " + scope.exportsSymbols() + ";");
    
    if (scope.getAstNode().isPresent()) {
      printer.print("astNode = ");
      printer.print(repository.getASTNodeNameFormatted(scope.getAstNode().get()));
      printer.println(";");      
    } else if (printEmptyOptional) {
      printer.println("astNode = absent;");
    }
    
    if (scope.getSpanningSymbol().isPresent()) {
      printer.print("spanningSymbol = ");
      reportSymbol(scope.getSpanningSymbol().get(), printer);
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.println("spanningSymbol = absent;");
    }
    
    if (scope.getEnclosingScope().isPresent()) {
      printer.print("enclosingScope = ");
      printer.print(repository.getScopeNameFormatted(scope.getEnclosingScope().get()));
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.println("enclosingScope = absent;");
    }
    
    Collection<Symbol> reportedSymbols = symbols.stream()
        .filter(sym -> !(sym instanceof ScopeSpanningSymbol)).collect(Collectors.toList());
    if (!reportedSymbols.isEmpty()) {
      printer.print("symbols = [");
      printer.println("// *size: " + reportedSymbols.size());
      printer.indent();
    }
    else if (printEmptyList) {
      printer.println("symbols = [];");
    }
    String sep = "";
    for (Symbol symbol : reportedSymbols) {
      if (!(symbol instanceof ScopeSpanningSymbol)) {
        printer.print(sep);
        sep = ",\n";
        reportSymbol(symbol, printer);
      }
    }
    if (!reportedSymbols.isEmpty()) {
      printer.println("];");
      printer.unindent();
    }
    
    if (!scope.getSubScopes().isEmpty()) {
      printer.print("subScopes = [");
      printer.println("// *size: " + scope.getSubScopes().size());
      printer.indent();
    }
    else if (printEmptyList) {
      printer.println("subScopes = [];");
    }
    sep = "";
    for (Scope subScope : scope.getSubScopes()) {
      printer.print(sep);
      sep = ",\n";
      reportScope(subScope, printer);
    }
    if (!scope.getSubScopes().isEmpty()) {
      printer.println("];");
      printer.unindent();
    }
    
    printer.unindent();
    printer.print("}");
  }
  
  @Override
  public void reportSymbolTableScope(Scope scope) {
    IndentPrinter printer = new IndentPrinter();
    printer.println("objectdiagram " + Names.getSimpleName(modelName) + "_ST {");
    printer.indent();
    reportScope(scope, printer);
    printer.println(";");
    printer.println();
    printer.unindent();
    printer.println("}");
    writeLine(printer.getContent());
  }
  
  protected void reportSymbol(Symbol sym, IndentPrinter printer) {
    String type = Names.getSimpleName(sym.getClass().getSimpleName());
    String symName = repository.getSymbolNameFormatted(sym);
    printer.println(symName + ": " + type + " {");
    printer.indent();
    reportAttributes(sym, printer);
    printer.unindent();
    printer.print("}");
  }
  
  protected void reportKind(SymbolKind kind, IndentPrinter printer) {
    String type = kind.getClass().getSimpleName();
    printer.println("KIND = :" + type + "{" );
    printer.indent();
    printer.println("name = \"" + kind.getName()+ "\";");
    printer.unindent();
    printer.println("};");    
  }
  
  protected void reportAccessModifier(AccessModifier.AllInclusionAccessModifier modifier, IndentPrinter printer) {
    String type = modifier.getClass().getSimpleName();
    printer.println("accessModifier = :" + type + "{};" );
  }

  protected void reportAccessModifier(BasicAccessModifier modifier, IndentPrinter printer) {
    printer.println("accessModifier = \"BasicAccessModifier." + modifier.toString() + "\";");
  }
  
  protected void reportAccessModifier(AccessModifier modifier, IndentPrinter printer) {
    if (modifier instanceof BasicAccessModifier) {
      reportAccessModifier((BasicAccessModifier) modifier, printer);
    } else if (modifier instanceof AccessModifier.AllInclusionAccessModifier) {
      reportAccessModifier((AccessModifier.AllInclusionAccessModifier) modifier, printer);
    }
  }

  protected void reportAttributes(Symbol sym, IndentPrinter printer) {
    reportAllFields(sym.getClass(), printer);
    printer.println("name = \"" + sym.getName() + "\";");
    reportKind(sym.getKind(), printer);
    if (sym.getAstNode().isPresent()) {
      printer.print("astNode = ");
      printer.print(repository.getASTNodeNameFormatted(sym.getAstNode().get()));
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.print("astNode = absent;");
    }
    if (sym instanceof ScopeSpanningSymbol) {
      ScopeSpanningSymbol spanningSym = (ScopeSpanningSymbol) sym;
      printer.println("spannedScope = "
          + repository.getScopeNameFormatted(spanningSym.getSpannedScope()) + ";");
    }

    printer.print("enclosingScope = ");
    printer.print(repository.getScopeNameFormatted(sym.getEnclosingScope()));
    printer.println(";");
    
    reportAccessModifier(sym.getAccessModifier(), printer);    
  }
  
  protected void reportCommonJFieldAttributes(
      CommonJFieldSymbol<? extends JTypeReference<? extends JTypeSymbol>> sym,
      IndentPrinter printer) {
    printer.println("isFinal = " + sym.isFinal() + ";");
    printer.println("isParameter = " + sym.isParameter() + ";");
    printer.println("isStatic = " + sym.isStatic() + ";");
  }
  
  protected void reportCommonJMethodAttributes(
      CommonJMethodSymbol<? extends JTypeSymbol, ? extends JTypeReference<? extends JTypeSymbol>, ? extends JFieldSymbol> sym,
      IndentPrinter printer) {
    printer.println("isFinal = " + sym.isFinal() + ";");
    printer.println("isStatic = " + sym.isStatic() + ";");
    printer.println("isAbstract = " + sym.isAbstract() + ";");
    printer.println("isConstructor = " + sym.isConstructor() + ";");
    printer.println("isEllipsisParameterMethod = " + sym.isEllipsisParameterMethod() + ";");
    if (!sym.isConstructor()) {
      printer.println("returnType = "
          + repository.getSymbolNameFormatted(sym.getReturnType()) + ";");
    }
    reportListOfReferences("exceptions", sym.getExceptions(), printer);
  }
  
  protected void reportCommonJTypeAttributes(
      CommonJTypeSymbol<? extends JTypeSymbol, ? extends JFieldSymbol, ? extends JMethodSymbol, ? extends JTypeReference<? extends JTypeSymbol>> sym,
      IndentPrinter printer) {
    printer.println("isFinal = " + sym.isFinal() + ";");
    printer.println("isAbstract = " + sym.isAbstract() + ";");
    printer.println("isEnum = " + sym.isEnum() + ";");
    printer.println("isFormalTypeParameter = " + sym.isFormalTypeParameter() + ";");
    printer.println("isInnerType = " + sym.isInnerType() + ";");
    printer.println("isInterface = " + sym.isInterface() + ";");
    if (sym.getSuperClass().isPresent()) {
      printer.println("superClass = "
          + repository.getSymbolNameFormatted(sym.getSuperClass().get())
          + ";");
    }
    else if (printEmptyOptional) {
      printer.print("superClass = absent;");
    }
    reportListOfReferences("interfaces", sym.getInterfaces(), printer);
  }
  
  protected void reportListOfReferences(String listName,
      Collection<? extends JTypeReference<? extends JTypeSymbol>> refs, IndentPrinter printer) {
    if (!refs.isEmpty()) {
      printer.print(listName + " = ");
      String delim = "";
      for (JTypeReference<? extends JTypeSymbol> anno : refs) {
        printer.print(delim);
        printer.print(repository.getSymbolNameFormatted(anno));
        delim = ", ";
      }
      printer.println(";");
    }
    else if (printEmptyList) {
      printer.println(listName + " = [];");
    }
  }
  
  /**
   * @return the printEmptyOptional
   */
  public boolean isPrintEmptyOptional() {
    return this.printEmptyOptional;
  }
  
  /**
   * @param printEmptyOptional the printEmptyOptional to set
   */
  public void setPrintEmptyOptional(boolean printEmptyOptional) {
    this.printEmptyOptional = printEmptyOptional;
  }
  
  /**
   * @return the printEmptyList
   */
  public boolean isPrintEmptyList() {
    return this.printEmptyList;
  }
  
  
  /**
   * @return the printAllFieldsCommented
   */
  public boolean isPrintAllFieldsCommented() {
    return this.printAllFieldsCommented;
  }

  
  /**
   * @param printAllFieldsCommented the printAllFieldsCommented to set
   */
  public void setPrintAllFieldsCommented(boolean printAllFieldsCommented) {
    this.printAllFieldsCommented = printAllFieldsCommented;
  }

  /**
   * @param printEmptyList the printEmptyList to set
   */
  public void setPrintEmptyList(boolean printEmptyList) {
    this.printEmptyList = printEmptyList;
  }
  
  protected void reportAllFields(Class<?> clazz, IndentPrinter printer) {
    if (!isPrintAllFieldsCommented()) {
      return;
    }
    printer.print("/* fields = ");
    for (Field field : clazz.getDeclaredFields()) {
      printer.print(field.getName() + " ");
    }
    while (clazz.getSuperclass() != null) {
      clazz = clazz.getSuperclass();
      for (Field field : clazz.getDeclaredFields()) {
        printer.print(field.getName() + " ");
      }      
    }
    printer.println("*/");
  }
  
}
