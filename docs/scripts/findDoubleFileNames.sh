#!/bin/bash
# (c) https://github.com/MontiCore/monticore  
#
# Find filenames, that are in use twice
# e.g. same class, but in different packages
# e.g. Java-File and Template with same Basename, but used for different
# purposes (e.g. also in different directories)
#
# Arguments
#  1: directory to handle
#  2: storage for the intermediate result (relation)
# 
# Result:
# (1) List of all Files (ftl and calculators) that occur several times
#
# potential call:
# ./findDoubleFileNames ~/workspace/dex ~/tmp/
#

# List of filenames to be included as standard no-go's:
nogofilenames=`dirname $0`/findDoubleFileNames.JavaStandardNames.txt

# from here we compute the names:
### dir=$HOME/workspace/dex
dir=$1
cd $dir

### tmpdir=$HOME/tmp/
tmpdir=$2
mkdir -p $tmpdir

filelist=$tmpdir/doublefiles.txt

rm -f $filelist $filelist.?  # start fresh

echo "## Duplicate File Names"
echo " ------------------------------------------------"
echo " List of double files:"
echo " ------------------------------------------------"

# get the files from the project
find . -print \
| grep -v "/gen/" \
| grep -v "/target/" \
> $filelist

# add the no-go's
cat $nogofilenames >> $filelist

# filter java and template files
cat $filelist | grep "java$" >> $filelist.java.0

cat $filelist | grep "ftl$" >> $filelist.ftl.0

# sed:
#  get rid of extensions .java,.ftl
# sort -u 
#  to get rid of doubles in the same directory (this happens when a 
#  template uses a Calculator of same name and is OK, as tehy are in the
#  same directory)
# sed:
#  get rid of packages (just the basename remains)
# sort 
#  sort filenames again (but keep doubles)
cat $filelist.ftl.0 \
| sed 's!.ftl!!g' \
| sort -u \
| sed 's!.*/!!g' \
| sort \
> $filelist.ftl.1

cat $filelist.java.0 \
| sed 's!.java!!g' \
| sort -u \
| sed 's!.*/!!g' \
| sort \
> $filelist.java.1

# removing doubles
sort -u $filelist.java.1 > $filelist.java.2
sort -u $filelist.ftl.1 > $filelist.ftl.2

# diff now contains the doubles:
diff -u $filelist.java.2 $filelist.java.1 \
| grep '^+[^+]' \
| sed 's/^+//g' \
> $filelist.java.3

# diff now contains the doubles:
diff -u $filelist.ftl.2 $filelist.ftl.1 \
| grep '^+[^+]' \
| sed 's/^+//g' \
> $filelist.ftl.3

# get the original sources:
echo "#### List of double java files"
for i in `cat $filelist.java.3`
do
  echo $i "    in: <br/>"
  grep "/"$i"\." $filelist.java.0 | sed 's/$/<br\/>/g'
  echo "<br/>"
done

echo "#### List of double ftl files"
for i in `cat $filelist.ftl.3`
do
  echo $i "    in: <br/>"
  grep "/"$i"\." $filelist.ftl.0 | sed 's/$/<br\/>/g'
  echo "<br/>"
done

echo
echo "(EOF)"


#
# Hilfen:
# unzip -l rt.jar | grep java/lang >> ~/tmp/x1
# unzip -l rt.jar | grep java/util >> ~/tmp/x1
# unzip -l rt.jar | grep java/io >> ~/tmp/x1
# unzip -l rt.jar | grep java/awt >> ~/tmp/x1
# unzip -l rt.jar | grep java/beans >> ~/tmp/x1
# cat ~/tmp/x1 | sed "s/^[- 0-9:]*//g" | grep -v '\$' | sed 's/class$/java/g'
#

