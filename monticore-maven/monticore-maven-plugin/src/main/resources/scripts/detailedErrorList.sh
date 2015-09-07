#!/bin/bash
#
# gets the detailed list of currently defined errors
# including explanations where they exist.
#
# these explanations are encoded in comments of the
# following form:  /*##                 ##*/
# and may go over several lines
#
# Arguments
#  1: directory to handle
#  2: storage for the intermediate result 
# 
# Result:
# (1) List of error codes 
#          + text being printed
#          + explanation (given in comment
#
# Shortcomming: 
# ($!) wegen der "grep-basierten" syntaktischen Analyse werden evtl.
# Fehlermeldungen nicht richtig erkannt
#
# potential call:
# bin/detailedErrorList ~/dex ~/tmp/
#

# this is the directory, we examine:
filedir=$1
# this is a temporary directory:
tmpdir=$2

### For testing purposes only: ###
### filedir=$HOME/tmp/sources  ###
### filedir=$HOME/dex/  ###
### tmpdir=$HOME/tmp/ ###

cd $filedir
mkdir -p $tmpdir

filelist=$tmpdir/filelist.txt
fulltext=$tmpdir/fulltext.txt

rm -rf $fulltext 	# start fresh

echo " ------------------------------------------------"
echo " List of java and template files considered  :"
echo " ------------------------------------------------"
echo " "

find . -print| grep -v ".svn" \
 | grep "\.java" > $filelist.j
echo "We found  " `cat $filelist.j | wc -l` " java files. "

find . -print | grep -v ".svn" \
| grep "\.ftl" > $filelist.f
echo "We found  " `cat $filelist.f | wc -l` " ftl files. "

cat $filelist.j $filelist.f > $filelist
echo "in total: " `cat $filelist | wc -l` 

# grep the error codes 
for i in `cat $filelist`
do
   cat $i >> $fulltext
   echo >> $fulltext
   echo "EOF-OF-FILE" >> $fulltext
done

echo " ------------------------------------------------"

#
# now we use awk to grep the concatenated files:
# (to get text that spans over several lines)
#

cat $fulltext | awk '
  BEGIN { ecount = 0; ncount = 0;
          ecode = "0xNOERROR";
	  # Mode = 0: normal state
	  #      = 1: we read an unfinished error message
	  mode=0;
        }

  # handle an error code
  /"0x[A-F0-9][A-F0-9][A-F0-9][A-F0-9][A-F0-9][ "]/ {
    ecount++;
    # delete first part
    gsub(/^.*"0x/, "0x");
    # extract error code
    ecode=substr($0, 1, 7);
    # initialize arrays
    arr[ecode] = "";
    expl[ecode] = "";
    # ... and explanation: becomes rest of line
    $0=substr($0, 9);
    # continue handling explantion in other pattern
    mode = 1;
  }

  # handle lines of error message
  mode==1 { 
    explanation = $0;
    # concat the explanation to the previous parts
    arr[ecode] = arr[ecode] explanation
    # check whether explanation is complete
    mode = (explanation ~ /;/) ? 0 : 1;
  }
  
   mode==0 {
    n = index(arr[ecode], "\"##");
    if (n>0)
    arr[ecode]=substr(arr[ecode], 0, n-1);
  }

   # handle explanation "## .... ##"
  /\"##/,/##\"/ {
    # handle start of comment
    if(/\"##/) {
      gsub(/^.*\"##/, ""); # delete start
    }
    if(/##\"/) {
      gsub(/##\".*$/, ""); # delete end
    }
    expl[ecode] = expl[ecode] $0;
  }

  # reset mode at end of file
  /OF-FILE/ { ncount++; mode=0
              ecode = "0xNOERROR";
	    }
  
  # finalize collection and output
  END {
    print "Summary: #of errors: " ecount;
    print "         #of files:  " ncount;
    for (i in arr) {
       ## printf "%s :: %s\n", i, arr[i];
       # extracting the readable information 
       # core result:
       result = arr[i];
       gsub(/"[^"]*"/, " ... ", result);
	   gsub(/"[^"] *\+.*$/, " ... ", result);
       gsub(/"[^"].*$/, "", result);   
       printf "%s :: %s\n", i, result;
       # explanation in separate line:
       result = expl[i];
       gsub(/"[^"]*"/, " ... ", result);
	   gsub(/"[^"] *\+.*$/, " ... ", result);
       gsub(/"[^"].*$/, "", result);
       printf "   %s\n", result; 
    }
  }
'

echo " "
echo "(EOF)"

