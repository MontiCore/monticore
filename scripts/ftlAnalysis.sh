#!/bin/bash
# (c) https://github.com/MontiCore/monticore  
#
# Derive some infos about the templates in use
#
# Arguments
#  1: list of directories to search for templates, separated by ":"
#  2: starting node (Qualified name, like "mc.umlp.Bla")
#  3: storage for the intermediate result (relation)
# 
# Result:
# (1) List of all Files (ftl and calculators) considered
# (2) Info about their statical use (= reachability)
#	"UNUSED" means: the file is not used within
#
# Shortcomming: 
# (1) java Files that are Calculators are identified by
# containing the match     "extends *[a-zA-Z.]*Calculator"
# which may not be the case
#
# (2) Filenames may be computed within the ftl, such that a static
# analysis does not detect all filenames
#
# (3) Filenames may be statically present, but not used in the same
# source, e.g. they can be stored in variables and called in sub-templates.
#
# ($!) wegen der "grep-basierten" syntaktischen Analyse werden einige Dateien
# nicht richtig erkannt. So werden manche Java-Files nicht als Calculatoren erkannt,
# obwohl sie welche sind und manche Java-Dateien einbezogen, die mit instantiate
# in den Templates benutzt werden (das füu falschen Warnungen "dangling" reference.
# Deshalb: ist die dangling reference war nung fuer Java-Files abgeschaltet.
#
# potential call:
# scripts/ftlAnalysis.sh $HOME/workspace/dex/gtr/src:$HOME/workspace/dex/use/src configure.StartAllOutput ~/tmp/relation.txt
#

# from here we compute the reachable Names:
### dir=$HOME/workspace/dex/gtr/src
dirs=$1

# extract directory list, space separated
dirs2=`echo $dirs | sed "s!:! !g"`

### startNode="configure.StartAllOutput"
startNode=$2

### relfile=$HOME/akt/relation.txt
relfile=$3

rootdir=`pwd`

rm -rf $relfile	# start fresh

echo "start" $startNode >> $relfile
echo "## ftlAnalysis"
echo " ------------------------------------------------"
echo "### List of java calculators considered:"
echo " ------------------------------------------------"

# iterate over the given directories & files within
for dir in $dirs2
do
  cd $dir
  echo "iterate over directory for java-files:" $dir "<br/>"
  for i in `find . -print | grep -v ".svn" | grep java`
  do
    # sucht alle Klassen, die Calculatoren sind und von ftl aufzurufen
    # waeren
    ipkg=`echo $i | sed 's!^./!!g' | sed 's!.java!!g' | sed 's!/!.!g'`
    grep "extends *[a-zA-Z.]*Calculator" $i > /dev/null
    if test $? -eq 0
    then
     echo "**java node**" $ipkg " <br/>"
     echo "node" $ipkg >> $relfile
    fi
  done
  cd $rootdir
done


echo " ------------------------------------------------"
echo "### List of templates considered:"
echo " ------------------------------------------------"

# iterate over the given directories & files within
for dir in $dirs2
do
  cd $dir
  echo "iterate over directory for ftl-files:" $dir "<br/>"
  for i in `find . -print | grep -v ".svn" | grep ftl`
  do
    # filtert nach strings, die nach filenamen aussehen
    # und fuegt den enthaltenden Dateinamen vorne dazu
    ipkg=`echo $i | sed 's!^./!!g' | sed 's!.ftl!!g' | sed 's!/!.!g'`
    echo "**ftl node**" $ipkg " <br/>"
    echo "node" $ipkg >> $relfile

    cat $i \
    | grep '"' - \
    | sed 's/<#-.*//g' - \
    | sed 's/^[^"]*"//g' - \
    | sed 's/"[^"]*"/\n/g' - \
    | sed 's/"[^"]*$//g' - \
    | grep '^[a-zA-Z][a-zA-Z0-9]*\.[a-zA-Z][a-zA-Z0-9]*' \
    | sed 's!^!'$ipkg' --> !g' - \
    >> $relfile
  done
  cd $rootdir
done

echo " ------------------------------------------------"
echo "### List of all nodes with static reachability infos"
echo " "
echo " UNUSED means, file (ftl/java) is not statically(!) identifyable"
echo "  to be used when invoking " $startNode
echo "  Care: the filename may be calculated during runtime"
echo "  e.g. myconfigure.StartCDEXT-* are calculated."
echo "  (we cannot check this statically)"
echo "  Furthermore: static inclusion/knowledge of a file does not "
echo "  necessarily mean it is called during each execution."
echo " "
echo " (s:?,t:?) means: the file calls s other files"
echo "      and is called by t other files"
echo "      (s=source; t=target)"
echo " "
echo " For a concrete list of who calls whom see:"
echo ' grep "\--" /tmp/relation.txt | sort -u'
echo " "
echo " ------------------------------------------------"
echo " "
echo " "

# compute the transitive closure (and with it the list of 
# files referenced from the starter

#
# $relfile contains the following content:
# "start NAME" defines the start node (line 1 only)
# "node NAME" defines a node
# "NAME --> NAME" defines an arc
#
# Variables:
# strn[i]  contains NAMES (we use indices internally)
# nums[s]  maps a name to its index
#
# m[i,j]   matrix storing how often an arc from i to j is defined 
#          (=0, means no link; >=1 means there is a link)
#          then also holds the transitive closure
# r[i,j]   original relation (no transitive clseure)
#
# target[n]  counts how often a node is target in an edge
# source[n]  counts how often a node is source in an edge
# nodeexists[n]  1 when the node exists (otherwise it is a dangling pointer)
#

cat $relfile | awk '
  BEGIN { ncount = 0; }
  /start/{ startStr=$2; start=addStr($2); 
           m[start,start] = 1; }
  /node/{ n=addStr($2); nodeexists[n]=1; }
  /-->/ { lcount++;
          n1 = addStr($1); n2 = addStr($3);
	  # fill matrix
	  m[n1,n2] = 1;
	  r[n1,n2] = r[n1,n2] + 1;
	  source[n1]++; target[n2]++;
        }

  END {
    print "Summary:<br/> #of node:  " ncount " <br/>";
    print "         #of arcs:  " lcount " <br/>";
    print "StartNode:   " strn[start] " <br/>";
    print " ";
    transclosure(m); 
    for (i in strn) {
      if( m[start,i] >= 1 ) 
        x = "      ";
      else
        x = "**UNUSED**";
      if( nodeexists[i] >= 1 ) 
        dangling = "";
      else
        dangling = "DANGLING REF (was not followed)!";
      printf "%6s %-50s  (s:%d,t:%d) %s  <br/>\n", x, strn[i], source[i], target[i], dangling;
    }

    print " ";
    print " ------------------------------------------------";
    print "### The relation: Source --> Target (+ Number of inclusions)";
    print " ------------------------------------------------";
    print " ";
    for (i in strn) {
      printf "**source** %s\n <br/>", strn[i];
      for (j in strn) {
        if(r[i,j] >= 2) {
          printf "        --> %s (%d) <br/>\n", strn[j], r[i,j];
	} else if (r[i,j] >= 1) {
          printf "        --> %s <br/>\n", strn[j];
	}
      }
    }
    
    print " ";
    print " ------------------------------------------------";
    print "### The inverse relation: Target --> Source";
    print " ------------------------------------------------";
    print " ";
    for (i in strn) {
      printf "**target** %s\n <br/>", strn[i];
      for (j in strn) {
        if(r[j,i] >= 1) {
          printf "        <-- %s <br/>\n", strn[j];
	}
      }
    }
    
  }

  # calculate the transitive closure (Warshall)
  function transclosure(m)
  {
    for(k = 1; k <=ncount; k++) {
      for(i = 1; i <=ncount; i++) {
        if(m[i,k] == 1) {
	  for(j = 1; j <=ncount; j++) {
	    if(m[k,j] == 1) m[i,j] = 1;
	  }
	}
      }
    }
  }

  # map string to a number in: nums
  #    and store inverse maping in strn
  function addStr(s)
  {
    n = 0;
    if (s in nums) {
      n = nums[s];
    }
    if (n==0)
    { n = ++ncount
      nums[s] = n;
      strn[n] = s;
    }
    return n;
  }

  # copy an array
  function copy(a,b)
  {
    split("", b); # empty b
    for(i in a) { b[i] = a[i]; }
  }
'

echo " "
echo "(EOF)"


