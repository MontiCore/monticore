#!/bin/bash
# (c) https://github.com/MontiCore/monticore  
#
# gets the list of currently defined errors
#
# Arguments
#  1: directory to handle
#  2: storage for the intermediate result 
# 
# Result:
# (1) List of java and template files considered
# (2) List of all error codes found (sorted)
# (3) List of all REPEATED ERROR CODES
# (abandoned, because there are so many: 
#                        (4) List of all ILLFORMED ERROR CODES
#
# Shortcomming: 
# ($!) wegen der "grep-basierten" syntaktischen Analyse werden evtl.
# Fehlermeldungen nicht richtig erkannt
#
# potential call:
# bin/errorList.sh ~/dex ~/tmp/
#

# this is the directory, we examine:
filedir=$1
tmpdir=$2

### For testing purposes only: ###
### filedir=$HOME/tmp/sources  ###
### filedir=$HOME/dex/  ###
### tmpdir=$HOME/tmp/ ###

cd $filedir
mkdir -p $tmpdir

filelist=$tmpdir/filelist.txt
errorcodes=$tmpdir/errorcodes.txt

rm -rf $errorcodes 	# start fresh

echo "## Report for dublicate usage of error codes"
echo " ------------------------------------------------"
echo " List of java and template files considered:"
echo " ------------------------------------------------"
echo " "

find . -print| grep -v ".svn" \
 | grep "\.java" > $filelist.j
echo "We found  " `cat $filelist.j | wc -l` " java files. <br/>"

find . -print | grep -v ".svn" \
| grep "\.ftl" > $filelist.f
echo "We found  " `cat $filelist.f | wc -l` " ftl files. "

cat $filelist.j $filelist.f > $filelist
echo "in total: " `cat $filelist | wc -l`

echo " "
echo " ------------------------------------------------"
echo " List of all error codes "
echo " "
echo "    an error code is starting with \"0x"
echo "    then has exactly 5 digits where the"
echo "    first one is a letter and it ends with"
echo "    a space or \" (anything else is ignored)"
echo " "
echo " The list is sorted"
echo " ------------------------------------------------"
echo " "

# grep the error codes
for i in `cat $filelist`
do
    grep -H "\"0x[A-Z0-9]\{5\}[ \"]" $i >> $errorcodes
done

# prepare and sort
cat $errorcodes \
| sed 's/^.*0x/0x/g' \
| sed 's/ .*$//g' \
| sed 's/\".*$//g' \
| sort - > $errorcodes.sort

echo "We found  " `cat $errorcodes.sort | wc -l` " error codes. "
echo " "

cat $errorcodes.sort

echo "## Dublicate error codes"
echo " ------------------------------------------------"
echo " List of all error codes THAT OCCUR MORE THAN ONCE"
echo " "
echo " ------------------------------------------------"
echo " "
sort -u $errorcodes.sort > $errorcodes.uniquesort
diff $errorcodes.sort $errorcodes.uniquesort \
| grep "^-0x" \
> $errorcodes.doubles

sed "s/^-/ALERT: this error occurs twice::  /g" $errorcodes.doubles > $errorcodes.doubles

sed 's/$/<bd\/>/' $errorcodes.doubles > $errorcodes.doubles

cat $errorcodes.doubles

echo "We found  " `cat $errorcodes.doubles | wc -l` " repeated error codes. "
echo " "

## 
## This piece of check needs to be abandoned, as 
## Monticore itself has so many 0x codes within such that checking illegal ones
## doesn't help at all:
## echo " "
## echo " ------------------------------------------------"
## echo " List of all error codes THAT HAVE ILLEGAL FORM"
## echo " "
## echo "  the allowed form is:"
## echo "  0x[A-F0-9]^5[ ]  "
## echo "  (i.e. 0x + 5 hexadecimal digits + a space)"
## echo " ------------------------------------------------"
## echo " "
## 
## cat $errorcodes.uniquesort \
## | grep -v "0x[A-F0-9]\{5\}" \
## | sed "s/^/ALERT: illegal form of error code::  /g" \
## > $errorcodes.illformed
## 
## cat $errorcodes.illformed
## 
## echo "We found  " `cat $errorcodes.illformed | wc -l` " illformed error codes. "
## echo " "
## 

echo
echo "(EOF)"

##
# some helpers:
# cat x2.ftl | sed "s/:.*//g" | sort -u
# cat x2.ftl | sed "s|:.*| ~/tmp/sources/|g" | sed "s/^/cp /g" | sort -u > y
# 

