#!/bin/sh

source /etc/profile
cd `dirname $0`
pwd=`pwd`
echo $pwd


ARGS=`getopt -o t:s:e --long taskCode:,startDate:,endDate: -- "$@"`

#将规范化后的命令行参数分配至位置参数（$1,$2,...)
eval set -- "${ARGS}"

while true
do
    case "$1" in
		-t|--taskCode)
            taskCode=$2;
            shift 2;;
        -s|--startDate)
            startDate=$2;
            shift 2;;
        -e|--endDate)
            endDate=$2;
            shift 2;;
        --)
            shift;
            break;;

        *)
            exit 1
            ;;
    esac
done

startDate=`date -d "-1 days "$startDate +%Y%m%d`
endDate=`date -d "-1 days "$endDate +%Y%m%d`

while [[ $startDate -le $endDate ]]
do
echo $startDate
java -cp ./*:/data/apps/azkaban/illidan/lib/* cn.whaley.datawarehouse.illidan.engine.Start --task $taskCode --time $startDate
if [ $? -ne 0 ];then
   echo "  java -cp {$startDate} is fail ..."
   exit 1
fi
startDate=`date -d "1 days "$startDate +%Y%m%d`
done