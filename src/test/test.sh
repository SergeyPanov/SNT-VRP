#!/usr/bin/env bash

BASIC_RUN="java -jar "$SNAPSHOT

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    --iterations)
    ITERATIONS="$2"
    shift # past argument
    shift # past value
    ;;
    --horizon)
    HORIZON="$2"
    shift # past argument
    shift # past value
    ;;
    --out)
    OUTPUT_DIR="$2"
    shift # past argument
    shift # past value
    ;;
    --input)
    DATASET_INPUT="$2"
    shift # past argument
    shift # past value
    ;;
    --snapshot)
    SNAPSHOT="$2"
    shift # past argument
    shift # past value
    ;;
    --it)
    IT="$2"
    shift # past argument
    shift # past value
    ;;
    --default)
    DEFAULT=YES
    shift # past argument
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done

if [ ! -d "$OUTPUT_DIR" ]; then
    mkdir $OUTPUT_DIR
fi


if [ -z "$ITERATIONS" ]; then
    ITERATIONS=300
fi

if [ -z "$OUTPUT_DIR" ]; then
    OUTPUT_DIR="./dataset/output/tabu"
fi

if [ -z "$DATASET_INPUT" ]; then
    DATASET_INPUT="./dataset/input"
fi

if [ -z "$HORIZON" ]; then
    HORIZON=10
fi

if [ -z "$IT" ]; then
    IT=6
fi


if [ -z "$SNAPSHOT" ]; then
    SNAPSHOT="../../target/SNT-VRP-1.0-SNAPSHOT.jar"
fi

if [ ! -d "$DATASET_INPUT" ]; then
    echo "Dataset was not found"
    exit 1
fi


cd ../../; mvn package; cd -    # Compile

if [ ! -f $SNAPSHOT ]; then
    echo "No executable jar file"
    exit 1
fi



for entry in "$DATASET_INPUT"/*
do
    echo "------------------ Processing "$entry" ------------------"
    for i in $(seq 1 $IT)
    do
        fn_without_ext=$(echo ${entry} | cut -d "/" -f 4 | cut -d "." -f 1)
        java -jar $SNAPSHOT -i $ITERATIONS -h $HORIZON -p $entry >> $OUTPUT_DIR/$fn_without_ext".out" 2>/dev/null
        echo "-------" >> $OUTPUT_DIR/$fn_without_ext".out"
    done
    IFS=$'\n' array_of_lines=("$(cat $OUTPUT_DIR/$fn_without_ext'.out'  | grep -e "Total cost:" | cut -d ' ' -f 3 |  cut -d '.' -f 1)")

    max=0
    for v in ${array_of_lines[@]}; do
        if (( $v > $max )); then max=$v; fi;
    done
    echo "Worst: "$max >>  $OUTPUT_DIR/$fn_without_ext'.out'

    min=$max
    for v in ${array_of_lines[@]}; do
        if (( $v < $min )); then min=$v; fi;
    done

    echo "Best: "$min >>  $OUTPUT_DIR/$fn_without_ext'.out'

    str=""
    for v in ${array_of_lines[@]}; do
        str=$v" "$str
    done

    echo "Average: "$(python3 -c "costs=str('${str}').split(); float_costs=[float(c) for c in costs]; print(sum(float_costs)/len(float_costs))" ) >> $OUTPUT_DIR/$fn_without_ext'.out'
done


