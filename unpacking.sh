#!/bin/bash



if test $# -lt 2 || test $# -gt 4; then
	echo ""
	echo "Wrong! check the follow usages!"
	echo "usages : $0 <source_dir> <target_dir> [group] [project]"
	echo "example : $0 ./archieves ./data"
	echo "example : $0 ./archieves ./data Apache CAMEL"
	echo ""
	echo ""
	exit 1
fi

last_term=""
last_string()
{
	string=$1
	new=${string#*$2}
	if [ "$string" != "$new" ]; then
		last_string $new $2
		new=$last_term
	fi
	last_term=$new
	return
}

# start work
substring=".jar"
TARGET="$2"
SGROUP="$3"
SPROJECT="$4"


echo ""
echo ""
echo "start unpacking..."

for var in "$1"/*
do
	if test -f $var; then 
		continue
	fi

	last_string $var "/"
	group=$last_term

	if test "$SGROUP" != "" && test "$group" != "$SGROUP" ; then
		continue
	fi

	if test ! -d "$2/$group"; then
		mkdir "$2/$group"
	fi

	for entry in "$var"/*
	do
		last_string $entry "/"
		fname=$last_term

		if test -f $entry && test "${fname#*$substring}" == "$fname" 
		then	
		        project="${fname:0:-4}"
			
		
			if test "$SPROJECT" != ""  && test "$project" != "$SPROJECT"; then
				continue
			fi

		        if test ! -d "$2/$group/$project"; then
				mkdir "$2/$group/$project"
			fi

			echo "unpacking $entry to $2/$group/$project"
		        tar -xf $entry -C "$2/$group/$project"
		fi
	done
done
echo "Done!"
