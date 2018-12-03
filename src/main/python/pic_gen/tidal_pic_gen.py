import numpy as np
import matplotlib.pyplot as plt
import sys
import os
import datetime
import json

# 0.获取日期
now_time = datetime.datetime.now()
date_form = now_time.strftime('%Y%m%d')

# 1.找到存储数据的目录
parent_path = sys.path[0]
for i in range(4):
    parent_path = os.path.abspath(os.path.join(parent_path, '..'))
sys.path.append(parent_path)
new_path = parent_path
new_path = os.path.join(parent_path, 'data')
new_path = os.path.join(new_path, 'load')
file_path1 = new_path + '\\' + 'area1loadCount.txt'
file_path3 = new_path + '\\' + 'area3loadCount.txt'

# 2.打开文件，读取数据
file_area1 = open(file_path1)
file_area3 = open(file_path3)
dataMat_area1 = []
dataMat_area3 = []
for line in file_area1.readlines():
    dataMat_area1.append(float(line))
for line in file_area3.readlines():
    dataMat_area3.append(float(line))

# 3.绘图
x = np.linspace(1, 360, num=360)
plt.plot(x, dataMat_area1, label='area1', c='b')
plt.plot(x, dataMat_area3, label='area3', c='g')
plt.legend()


# 5.保存数据

config_path = os.path.join(os.path.join(new_path, '..'), 'simulation-config')
json_file_path = config_path + '\\' + 'config.json'
json_file = open(json_file_path, encoding='utf-8')
config_content = json.load(json_file)
threshold = config_content['service']['threshold']
service_lambda = config_content['service']['lambda']
service_time = config_content['service']['service_time']
service_ratio = int(service_time / service_lambda)


# 4.保存 & 展示图片
pic_path = os.path.join(new_path, 'pic')
if os.path.exists(pic_path):
    plt.savefig(pic_path + '\\' + date_form +
                '_ratio'+str(service_ratio) + '.png')
else:
    os.makedirs(r'' + pic_path)
    plt.savefig(pic_path + '\\' + date_form + '.png')
plt.show()
