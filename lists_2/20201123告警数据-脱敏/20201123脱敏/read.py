import csv
import os

def walkFile(file):
    for root, dirs, files in os.walk(file):

        # root 表示当前正在访问的文件夹路径
        # dirs 表示该文件夹下的子目录名list
        # files 表示该文件夹下的文件list

        # 遍历文件
        for f in files:
            print(os.path.join(root, f))

        # 遍历所有的文件夹
        for d in dirs:
            print(os.path.join(root, d))


if __name__ == "__main__":
    headers = ['网元类型','网元子设备编号','网元名称',
    '设备告警流水号','告警名称','告警类型','告警级别',
    '告警状态','发生时间','确认时间','清除时间',
    '(反)确认操作员','清除操作员','定位信息','链路标识',
    '链路名称','链路类型','告警标识','告警ID','对象实例类型',
    '自动清除','告警清除类型','影响业务标识','到达网管UTC时间',
    '列表流水号','关联日志id','代理ID','根源告警ID','告警是否被隐藏（屏蔽）'];

    rows = [
        [1,'xiaoming','male',168,23],
        [1,'xiaohong','female',162,22],
        [2,'xiaozhang','female',163,21],
        [2,'xiaoli','male',158,21]
    ];

    with open('test.csv','w', newline = '', encoding = 'utf-8')as f:
        f_csv = csv.writer(f);
        f_csv.writerow(headers);
        f_csv.writerows(rows);

    walkFile("./")