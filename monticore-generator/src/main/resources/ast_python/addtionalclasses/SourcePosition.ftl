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
<#-- This template is required in order to store the source position correctly.-->

# generated by template ast_python.addtionalclasses.SourcePosition
class SourcePosition(object):
    __line = None
    __column = None
    __fileName = None

    @classmethod
    def getDefaultSourcePosition(cls):
        """
        :return
        :rtype SourcePosition
        """
        return cls(0, 0)

    def __init__(self, _line=None, _column=None, _fileName=None ):
        """
        :param _line
        :type _line: int
        :param _column:
        :type _column: int
        :param _fileName:
        :type _fileName: str
        """
        self.__line = _line
        self.__column = _column
        self.__fileName = _fileName

    def getLine(self):
        """
        :return
        :rtype int
        """
        return self.__line

    def setLine(self, _line=None):
        """
        :param _line:
        :type _line: int
        """
        self.__line = _line

    def getColumn(self):
        """
        :return
        :rtype int
        """
        return self.__column

    def setColumn(self, _column=None):
        """
        :param _column:
        :type _column: int
        """
        self.__column = _column

    def getFileName(self):
        """
        :return
        :rtype None or str
        """
        return self.__fileName

    def setFileName(self, _fileName=None):
        """
        :param _fileName:
        :type _fileName: str
        """
        self.__fileName = _fileName

    def equals(self, _o=None):
        """
        :param _o:
        :type _o: object
        :return
        :rtype bool
        """
        if not isinstance(_o,SourcePosition):
            return False
        else:
            return (self.__line == _o.getLine()) and (self.__column == _o.getColumn()) and (self.__fileName == _o.getFileName())

    def toString(self):
        """
        :return
        :rtype str
        """
        if self.__fileName is not None:
            return self.__fileName + ':' + '<' + str(self.__line) + ',' + str(self.__column) + '>'
        else:
            return '<' + str(self.__line) + ',' + str(self.__column) + '>'

    def compareTo(self, _o=None):
        """
        :param _o a source position object
        :type _o: SourcePosition
        :return difference between the positions
        :rtype int
        """
        assert (_o is not None and isinstance(SourcePosition))
        if self.__fileName is not None and _o.getFileName() is not None:
            if self.compareToValues(self.__fileName,_o.getFileName()) == 0:
                if self.__line - _o.getLine() == 0:
                    return self.__column - _o.getColumn()
                else:
                    return self.__line - _o.getLine()
            else:
                return self.compareToValues(self.__fileName,_o.getFileName())
        else:
            if self.__line - _o.getLine() == 0:
                return self.__column - _o.getColumn()
            else:
                return self.__line - _o.getLine()

    def clone(self):
        """
        :return
        :rtype SourcePosition
        """
        if self.__fileName is not None:
            return SourcePosition(self.__line, self.__column, self.__fileName)
        else:
            return SourcePosition(self.__line, self.__column)

    def compareToValues(self, _this=None, _that=None):
        """
        :return
        :rtype int
        """
        return ((_this > _that) - (_this < _that))