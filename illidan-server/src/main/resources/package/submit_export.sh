#!/bin/sh

source /etc/profile
cd `dirname $0`
pwd=`pwd`
echo $pwd


ARGS=`getopt -o hd:ht:md:mt:pd:pt:s:e:sh:eh:dt:p:f --long hiveDb:,hiveTable:,mysqlDb:,mysqlTable:,phoenixDb:,phoenixTable:,startDate:,endDate:,startHour:,endHour:,dateType:,platForm:,filterCondition: -- "$@"`

#将规范化后的命令行参数分配至位置参数（$1,$2,...)
eval set -- "${ARGS}"

while true
do
    case "$1" in
		-hd|--hiveDb)
            hiveDb=$2;
            shift 2;;
        -ht|--hiveTable)
              hiveTable=$2;
              shift 2;;
        -md|--mysqlDb)
              mysqlDb=$2;
              shift 2;;
        -mt|--mysqlTable)
              mysqlTable=$2;
              shift 2;;
        -pd|--phoenixDb)
              phoenixDb=$2;
              shift 2;;
        -pt|--phoenixTable)
              phoenixTable=$2;
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
        -p|--platForm)
            platForm=$2;
            shift 2;;
        -f|--filterCondition)
            filterCondition="$2";
            shift 2;;
        --)
            shift;
            break;;
        *)
            exit 1
            ;;
    esac
done
echo "hiveDb is $hiveDb "
echo "hiveTable is $hiveTable "
echo "mysqlDb is $mysqlDb "
echo "mysqlTable is $mysqlTable "
echo "phoenixDb is $phoenixDb "
echo "phoenixTable is $phoenixTable "
echo "startHour is $startHour "
echo "endHour is $endHour "
echo "dateType is $dateType "
echo "filterCondition is  $filterCondition"
echo " platForm is $platForm"

if [ "$platForm" = "" ];then
    if [ "filterCondition" = "" ];then
        filterCondition=" where 1=1 "
    fi
   echo "filterCondition is  $filterCondition"
  java -cp ./*:/data/apps/azkaban/illidan_export/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --phoenixDb $phoenixDb --phoenixTable $phoenixTable --filterCondition "${filterCondition}"
  if [ $? -ne 0 ];then
     echo "  export is fail ..."
     exit 1
  fi
 exit 0
fi

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
        day_p=`date -d "$startTimeParam" +%Y%m%d`
        hour_p=`date -d "$startTimeParam" +%H`
        filterCondition=" where day_p = '$day_p' and hour_p = '$hour_p'"
        java -cp ./*:/data/apps/azkaban/illidan_export/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --phoenixDb $phoenixDb --phoenixTable $phoenixTable --filterCondition "${filterCondition}"
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
        filterCondition=" where day_p = '${startDate}' "
        java -cp ./*:/data/apps/azkaban/illidan_export/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --phoenixDb $phoenixDb --phoenixTable $phoenixTable --filterCondition "${filterCondition}"
        if [ $? -ne 0 ];then
           echo "  java -cp {$startDate} is fail ..."
           exit 1
        fi
        startDate=`date -d "1 days "$startDate +%Y%m%d`
    done
fi