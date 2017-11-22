#!/bin/sh

source /etc/profile
cd `dirname $0`
pwd=`pwd`
echo $pwd


ARGS=`getopt -o t:s:e:sh:eh:dt --long taskCode:,startDate:,endDate:,startHour:,endHour:,dateType: -- "$@"`

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
        -sh|--startHour)
            startHour=$2;
            shift 2;;
        -eh|--endHour)
            endHour=$2;
            shift 2;;
        -dt|--dateType)
            dateType=$2;
            shift 2;;
        --)
            shift;
            break;;

        *)
            exit 1
            ;;
    esac
done

if [ "$dateType" = "hour" ]; then
    startDate=`date -d "$startDate" +%Y-%m-%d`
    startTimeParam=`date -d "-1 hours $startDate $startHour:00:00" +"%Y-%m-%d %H:%M:%S"`
    endDate=`date -d "$endDate" +%Y-%m-%d`
    endTimeParam=`date -d "-1 hours $endDate $endHour:00:00" +"%Y-%m-%d %H:%M:%S"`

    compareStartTimeParam=`date -d "$startTimeParam" +%Y%m%d%H%M%S`
    compareEndTimeParam=`date -d "$endTimeParam" +%Y%m%d%H%M%S`

    while [[ $compareStartTimeParam -le $compareEndTimeParam ]]
    do
        echo $startTimeParam
        java -cp ./*:/data/apps/azkaban/illidan/lib/* cn.whaley.datawarehouse.illidan.engine.Start --task $taskCode --time "$startTimeParam"
        if [ $? -ne 0 ];then
           echo "  java -cp {$startTimeParam} failed ..."
           exit 1
        fi
        startTimeParam=`date -d "1 hours $startTimeParam"  +"%Y-%m-%d %H:%M:%S"`
        compareStartTimeParam=`date -d "$startTimeParam" +%Y%m%d%H%M%S`
    done
else
    startDate=`date -d "-1 days "$startDate +%Y%m%d`
    endDate=`date -d "-1 days "$endDate +%Y%m%d`

    while [[ $startDate -le $endDate ]]
    do
        echo $startDate
        java -cp ./*:/data/apps/azkaban/illidan/lib/* cn.whaley.datawarehouse.illidan.engine.Start --task $taskCode --time $startDate
        if [ $? -ne 0 ];then
           echo "  java -cp {$startDate}  failed ..."
           exit 1
        fi
        startDate=`date -d "1 days "$startDate +%Y%m%d`
    done
fi