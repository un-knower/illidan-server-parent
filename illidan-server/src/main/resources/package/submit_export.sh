#!/bin/sh

source /etc/profile
cd `dirname $0`
pwd=`pwd`
echo $pwd


ARGS=`getopt -o hd:ht:md:mt:s:e:p:f --long hiveDb:,hiveTable:,mysqlDb:,mysqlTable:,startDate:,endDate:,platForm:,filterCondition: -- "$@"`

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
        -s|--startDate)
            startDate=$2;
            shift 2;;
        -e|--endDate)
            endDate=$2;
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
echo "filterCondition is  $filterCondition"
echo " platForm is $platForm"

if [ "$platForm" = "" ];then
    if [ "filterCondition" = "" ];then
        filterCondition=" where 1=1 "
    fi
   echo "filterCondition is  $filterCondition"
  #java -cp  /opt/dw/guohao/illidan/lib/illidan-export-1.0.0.jar:/opt/dw/guohao/illidan/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --filterCondition "$filterCondition"
  java -cp ./*:/data/apps/azkaban/illidan_export/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --filterCondition "${filterCondition}"
  if [ $? -ne 0 ];then
     echo "  export is fail ..."
     exit 1
  fi
 exit 0
fi


startDate=`date -d "-1 days "$startDate +%Y%m%d`
endDate=`date -d "-1 days "$endDate +%Y%m%d`

while [[ $startDate -le $endDate ]]
do
echo $startDate
filterCondition=" where day_p = '${startDate}' "
 #java -cp  /opt/dw/guohao/illidan/lib/illidan-export-1.0.0.jar:/opt/dw/guohao/illidan/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --filterCondition "${filterCondition}"
 java -cp ./*:/data/apps/azkaban/illidan_export/lib/*  cn.whaley.datawarehouse.illidan.export.Start --hiveDb $hiveDb --hiveTable $hiveTable --mysqlDb $mysqlDb --mysqlTable $mysqlTable --filterCondition "${filterCondition}"
if [ $? -ne 0 ];then
   echo "  java -cp {$startDate} is fail ..."
   exit 1
fi
startDate=`date -d "1 days "$startDate +%Y%m%d`
done
