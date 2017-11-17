/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.cd2python.ast;

import com.google.common.base.Joiner;
import de.monticore.cdattributes._ast.ASTCD;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AstPythonGenerator {

    private static final String PYTHON_EXTENSION = ".py";

    /**
     * Generates ast python files for the given class diagram AST
     *
     * @param glex - object for managing hook points, features and global
     * variables
     * @param astClassDiagram - class diagram AST
     * @param outputDirectory - target directory
     */
    public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope, ASTCDCompilationUnit astClassDiagram,
                                File outputDirectory, IterablePath templatePath, boolean emfCompatible, ASTMCGrammar astGrammar) {
        final String diagramName = astClassDiagram.getCDDefinition().getName();
        final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
        setup.setModelName(diagramName);
        setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
        AstPythonGeneratorHelper astHelper = new AstPythonGeneratorHelper(astClassDiagram, globalScope);
        glex.setGlobalValue("astHelper", astHelper);
        glex.setGlobalValue("pythonNameHelper", new PythonNamesHelper());

        //TODO by KP: The following part is not really nice: In order to get the starting rule we have
        //TODO by KP: to resolve the whole symbol...
        String qualifiedGrammarName = astGrammar.getPackage().isEmpty()
                ? astGrammar.getName()
                : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackage()),
                astGrammar.getName());
        MCGrammarSymbol grammarSymbol = globalScope.<MCGrammarSymbol> resolve(
                qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
        Log.errorIfNull(grammarSymbol, "0xA4034 Grammar " + qualifiedGrammarName
                + " can't be resolved in the scope " + globalScope);
        // TODO: grammarInfo as parameter for this method?
        MCGrammarInfo grammarInfo = new MCGrammarInfo(grammarSymbol);

        setup.setGlex(glex);
        // we deactivate tracing in order to preserve the sensitive syntax of python
        setup.setTracing(false);
        //python requires an __init__ file in order to be able to import modules
        List<String> moduleInitList = new ArrayList<>();

        final GeneratorEngine generator = new GeneratorEngine(setup);
        final String astPackage = astHelper.getAstPackage();
        final String visitorPackage = AstGeneratorHelper.getPackageName(astHelper.getPackageName(),
                VisitorGeneratorHelper.getVisitorPackageSuffix());
        //we have to store all classes where conrete instance can be created
        final List<ASTCDClass> parsableClasses = new ArrayList<>();

        for (ASTCDClass clazz : astClassDiagram.getCDDefinition().getCDClasses()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(clazz.getName()) + PYTHON_EXTENSION);
            if (astHelper.isAstClass(clazz)) {
                generator.generate("ast_python.AstClass", filePath, clazz, clazz, astHelper.getASTBuilder(clazz));
                moduleInitList.add(clazz.getName());
                parsableClasses.add(clazz);
            }
            else if (!AstGeneratorHelper.isBuilderClass(astClassDiagram.getCDDefinition(), clazz)) {
                generator.generate("ast_python.Class", filePath, clazz);
            }

        }
        //interfaces are per-se not a part of python contract system, and are therefore implemented as abstract classes
        for (ASTCDInterface interf : astClassDiagram.getCDDefinition().getCDInterfaces()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(interf.getName()) + PYTHON_EXTENSION);
            generator.generate("ast_python.AstAbstractClass", filePath, interf, visitorPackage,
                    VisitorGeneratorHelper.getVisitorType(diagramName));
            moduleInitList.add(interf.getName());
        }
        for (ASTCDEnum enm : astClassDiagram.getCDDefinition().getCDEnums()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(enm.getName()) + PYTHON_EXTENSION);
            generator.generate("ast_python.AstEnum", filePath, enm);
            moduleInitList.add(enm.getName());
        }
        // the ast node is the superclass of all the classes
        Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                "ASTNode" + PYTHON_EXTENSION);
        generator.generate("ast_python.addtionalclasses.AstNode", filePath,
                astClassDiagram.getCDDefinition().getCDEnums().get(0));// the last argument in order to meed the signature
        //generate the comment and source position classes
        filePath = Paths.get(Names.getPathFromPackage(astPackage),
                "Comment" + PYTHON_EXTENSION);
        generator.generate("ast_python.addtionalclasses.Comment", filePath,
                astClassDiagram.getCDDefinition().getCDEnums().get(0));// the last argument in order to meed the signature
        filePath = Paths.get(Names.getPathFromPackage(astPackage),
                "SourcePosition" + PYTHON_EXTENSION);
        generator.generate("ast_python.addtionalclasses.SourcePosition", filePath,
                astClassDiagram.getCDDefinition().getCDEnums().get(0));// the last argument in order to meed the signature
        //TODO by KP: currently not supported: generate the parser module

        filePath = Paths.get(Names.getPathFromPackage(astPackage),
                "Parser" + PYTHON_EXTENSION);
        // the name of the overall language, it is required to have a correct link to the generated Lexer/Parser
        String name = astClassDiagram.getCDDefinition().getName();
        generator.generate("ast_python.addtionalclasses.Parser",
                filePath,astClassDiagram.getCDDefinition().getCDEnums().get(0), name,parsableClasses);
        //add the remaining pre-generated files to the list of inits
        moduleInitList.add("Comment");
        //TODO by KP:The Parser is currently not supported
        // moduleInitList.add("Parser");
        moduleInitList.add("SourcePosition");
        //and generate the init file finally
        filePath = Paths.get(Names.getPathFromPackage(astPackage),
                "__init__" + PYTHON_EXTENSION);
        generator.generate("ast_python.addtionalclasses.ModuleInit",filePath,
                astClassDiagram.getCDDefinition().getCDEnums().get(0),
                moduleInitList);

    }

    private AstPythonGenerator() {
        // noninstantiable
    }

}
