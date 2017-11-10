<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
<#-- This template generates a single parser for the corresponding language -->
${tc.signature("name")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
from antlr4 import *
from ${name}AntlrParser import ${name}AntlrParser
from ${name}AntlrLexer import ${name}AntlrLexer
from AstBuilderVisitor import AstBuilderVisitor


class Parser(object):
    """
    This class contains several method used to parse handed over models and returns them as one or more AST trees.
    """

    @classmethod
    def parseModel(cls, file_path=None):
        """
        Parses a handed over model and returns the ast representation of it.
        :param file_path: the path to the file which shall be parsed.
        :type file_path: str
        :return a new ast
        :rtype AST${name}
        """
        try:
            inputFile = FileStream(file_path)
        except IOError:
            print('(Parser) File ' + str(file_path) + ' not found. Processing is stopped!')
            return
        # create a lexer and hand over the input
        lexer = ${name}AntlrLexer(inputFile)
        # create a token stream
        stream = CommonTokenStream(lexer)
        # parse the file
        parser = ${name}AntlrParser(stream)
        # create a new visitor and return the new AST
        astBuilderVisitor = AstBuilderVisitor()
        return astBuilderVisitor.visit(parser.${name}())
