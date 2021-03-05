#coding=utf-8

import csv
import os
import re

def walkFile(file):
	fileList = [];
	for root,dirs,files in os.walk(file):
		for f in files:
			print(os.path.join(root, f))
			fileList.append(os.path.join(root, f))
	return fileList

def toTimeFormat(time):
	if time == "":
		return ""
	strList = time.split(" ")
	pattern = re.compile(r'\d+')

	m1 = pattern.findall(strList[0])

	year = m1[0]
	month = m1[1]
	day = m1[2]

	m2 = pattern.findall(strList[1])

	hour = m2[0]
	minute = m2[1]
	second = m2[2]

	if len(month) == 1:
		month = "0" + month
	if len(day) == 1:
		day = "0" + day

	newTime = year + "年" + month + "月" + day + "日 " + hour + "时" + minute + "分" + second + "秒 GMT+08：00"
	return newTime

def judgeRowAvailable(row):
	length = len(row);

	newRow = []

	if length < 25:
		return newRow, False

	if row[12] == "":
		return newRow, False

	findObj = re.match(r'\d+', row[24])
	if findObj:
		pass
	else:
		return newRow, False

	result = False

	networkEle = row[2]
	eleId = re.search(r'\d+', networkEle)
	if eleId:
		eleId = int(eleId.group(0))
	else:
		return newRow, False

	if eleId < 1000 or eleId >= 2000:
		return newRow,False

	for i in range(0,length):
		if i < 8 or i > 10:
			newRow.append(row[i])
		else:
			newRow.append(toTimeFormat(row[i]))

	return newRow, True


def main():
	headers = ['网元类型','网元子设备编号','网元名称',
	'设备告警流水号','告警名称','告警类型','告警级别',
	'告警状态','发生时间','确认时间','清除时间',
	'(反)确认操作员','清除操作员','定位信息','链路标识',
	'链路名称','链路类型','告警标识','告警ID','对象实例类型',
	'自动清除','告警清除类型','影响业务标识','到达网管UTC时间',
	'列表流水号','关联日志id','代理ID','根源告警ID','告警是否被隐藏（屏蔽）'];

	#fileList = walkFile("./lists_2/")

	fileList = walkFile("./lists_2/")

	with open("result_csv.csv", "w", newline = '', encoding = 'utf-8') as f_write:
		write_csv = csv.writer(f_write);

		write_csv.writerow(headers);

		for file in fileList:
			with open (file, "r", encoding = "utf-8") as f_csv:
				reader = csv.reader(f_csv)
				for row in reader:
					newRow, result = judgeRowAvailable(row)
					if result:
						write_csv.writerow(newRow)
					
				
	return write_csv
if __name__ == '__main__':
	write_csv = main()
	print(write_csv)