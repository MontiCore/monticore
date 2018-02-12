[![Build Status](https://travis-ci.org/MontiCore/monticore.svg?branch=master)](https://travis-ci.org/MontiCore/monticore)


# MontiCore - Language Workbench plus Tool Framework
[MontiCore](http://www.monticore.de) is a language workbench for an efficient development of domain-specific languages (DSLs). It processes an extended grammar format which defines the DSL and generates components for processing the documents written in the DSL. Examples for these components are parser, AST classes, symboltables or pretty printers.This enables a user to rapidly define a language and use it together with the MontiCore-framework to build domain specific tools.

## Licenses
* [LGPL V3.0](https://github.com/MontiCore/monticore/tree/master/00.org/Licenses/LICENSE-LGPL.md) (for handwritten Java code)
* [BSD-3-Clause](https://github.com/MontiCore/monticore/tree/master/00.org/Licenses/LICENSE-BSD3CLAUSE.md) (for templates and all generated artifacts)

## License for Java code in the repository (informal description)

This project is free software; you can redistribute it and/or
modify it under the terms of the LGPL; either
version 3.0 of the License, or (at your option) any later version.
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

## License for for templates and all generated artifacts (informal description)

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain a link to this copyright notice,
this list of conditions and this disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any expressed or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.

## Release Notes
This product includes the following software:
* [AntLR](http://www.antlr.org/)
* [FreeMarker](http://freemarker.org/)

## Build
Please make sure that your complete workspace only uses UNIX line endings (LF)
and all files are UTF-8 without BOM.
On Windows you should configure git to not automatically replace LF with CRLF during checkout by executing the following configuration:

    git config --global core.autocrlf input
