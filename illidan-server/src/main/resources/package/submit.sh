#!/bin/sh
if [ $# -ne 3 ]; then
    echo " 请输出 3 个参数  ${taskCode} ${startDate} ${endDate}"
	echo " usage : `basename  $0`  ${taskCode} ${startDate} ${endDate} "
    exit 1
fi

cd `dirname $0`
pwd=`pwd`
echo $pwd
source /etc/profile
taskCode=$1
startDate=$2
startDate=`date -d "-1 days "$startDate +%Y%m%d`
endDate=$3
endDate=`date -d "-1 days "$endDate +%Y%m%d`

while [[ $startDate -le $endDate ]]
do
echo $startDate
java -cp ./*:/data/apps/azkaban/illidan/lib/* cn.whaley.datawarehouse.illidan.engine.Start --task $taskCode --time $startDate
startDate=`date -d "1 days "$startDate +%Y%m%d`
done