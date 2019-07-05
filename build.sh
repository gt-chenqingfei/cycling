#!/bin/bash

PROG=$(basename $0)

if [ $# -lt 3 ]; then
    echo "Usage: $PROG <range> <channel-prefix> <output>"
    exit
fi

RANGE=$1
CHANNEL_ID=$2
OUT_DIR=$3
WIDTH=$(echo $RANGE | awk '{printf $NF}' | wc -c | sed 's/ //g')
VERSION_NAME=$(grep -o "android:versionName=\"[0-9\.]\+\"" AndroidManifest.xml  | awk -F'"' '{print $2}')

for i in $RANGE; do
    n=$(printf "%0${WIDTH}d" $i)
    sed -i -e "s/\(android:value=\)\"%CHANNEL_ID%\"/\1\"${CHANNEL_ID}${n}\"/g" AndroidManifest.xml

    verdir=${OUT_DIR}/sk${VERSION_NAME}
    fname=${verdir}/com.beastbikes.android-v${VERSION_NAME}-${CHANNEL_ID}${n}

    ./build/release.sh \
        && mkdir -p ${verdir} \
        && cp -v bin/com.beastbikes.android-v${VERSION_NAME}-*-release.apk ${fname}.apk \
        && git checkout AndroidManifest.xml

    md5=$(md5sum ${fname}.apk)

    echo $md5 | awk '{print $1}' > ${fname}.md5sum \
        && echo $md5 | awk '{printf "%s,%s\n",$1,$2}' >> ${verdir}/md5sum.csv
done
