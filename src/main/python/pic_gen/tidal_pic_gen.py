# -*- coding:utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
import sys
import platform
import os
import datetime
import json
import xlwt
import xlrd
from xlutils.copy import copy as xl_copy
import data_save

# 0.获取日期、系统信息
now_time = datetime.datetime.now()
date_form = now_time.strftime('%Y%m%d')
system_name = platform.system()

# 1.找到存储数据的目录
parent_path = sys.path[0]
for i in range(4):
    parent_path = os.path.abspath(os.path.join(parent_path, '..'))
sys.path.append(parent_path)
new_path = parent_path
new_path = os.path.join(parent_path, 'data')
new_path = os.path.join(new_path, 'load')
# 1.0系统兼容性适应
if system_name == 'Windows':
    file_path1 = new_path + '\\' + 'area1loadCount.txt'
    file_path3 = new_path + '\\' + 'area3loadCount.txt'
else:
    file_path1 = new_path + '/' + 'area1loadCount.txt'
    file_path3 = new_path + '/' + 'area3loadCount.txt'

edge_file_path = os.path.join(new_path, 'edgeload')
edge_num = [12, 13, 18, 23, 24, 35, 46, 56, 59, 67, 78, 79, 410, 514, 812, 912, 1011, 1013, 1112, 1114, 1213, 1314]
# 2.打开文件，读取数据
file_area1 = open(file_path1)
file_area3 = open(file_path3)
dataMat_area1 = []  # 存储仿真产生的area1的360个数据
dataMat_area3 = []  # area3数据
for line in file_area1.readlines():
    dataMat_area1.append(float(line))
for line in file_area3.readlines():
    dataMat_area3.append(float(line))

#边文件
edge_data_mat = []
for i in range(len(edge_num)):
    edge_file_name = 'edge' + str(edge_num[i]) + '.txt'
    edge_file_name = os.path.join(edge_file_path, edge_file_name)
    edge_file = open(edge_file_name)
    current_mat = []
    for line in edge_file.readlines():
        current_mat.append(float(line))
    edge_data_mat.append(current_mat)


# 3.绘图
x = np.linspace(1, 360, num=360)
plt.plot(x, dataMat_area1, label='area1', c='b')
plt.plot(x, dataMat_area3, label='area3', c='g')
plt.legend()  # 图例


# 4.保存数据
# 4.1 参数读取
config_path = os.path.join(os.path.join(new_path, '..'), 'simulation-config')
# 系统兼容性适应
if system_name == 'Windows':
    json_file_path = config_path + '\\' + 'config.json'
else:
    json_file_path = config_path + '/' + 'config.json'
json_file = open(json_file_path, encoding='utf-8')
config_content = json.load(json_file)
threshold = config_content['service']['threshold']
service_lambda = config_content['service']['lambda']
service_time = config_content['service']['service_time']
service_ratio = int(service_time / service_lambda)
# 4.2写入excel(分为 area1.xls 和 area3.xls)
data_save.save_data(dataMat_area1, 'area1', new_path, service_ratio)
data_save.save_data(dataMat_area3, 'area3', new_path, service_ratio)

#写入边数据
data_save.save_edge_data(edge_data_mat, new_path, service_ratio)

# todo:先判断workbook是否存在
# sheet_name = 'ratio' + str(service_ratio)    # 要写入的sheet名称
# excel_area1_name = os.path.join(
#    new_path, 'training_data_area1.xls')  # 要写入的excel文件名称
# rows = 0  # 该表单已有数据行
# cols = 0  # 该表单已有数据列
# if os.path.isfile(excel_area1_name):    # 如果excel文件存在
#    book_area1 = xlrd.open_workbook(excel_area1_name)
#    book_area1_sheet_names = book_area1.sheet_names()
#    workbook_area1 = xl_copy(book_area1)  # 将只读转换为可写(book -> workbook)
#    if sheet_name in book_area1_sheet_names:    # 如果表单存在
#        sheet_index = book_area1_sheet_names.index(sheet_name)
#        sheet = workbook_area1.get_sheet(sheet_index)
#        rows = book_area1.sheet_by_name(sheet_name).nrows
#        cols = book_area1.sheet_by_name(sheet_name).ncols
#    else:  # 如果表单不存在
#        sheet = workbook_area1.add_sheet(sheet_name)
# else:   # 如果excel文件不存在
#    workbook_area1 = xlwt.Workbook(encoding='utf-8')
#    sheet = workbook_area1.add_sheet(sheet_name)
# todo:此处要调用写入函数
#col = 0
#row = 0
#data_num = 0
# while row < 24:
#    if (col == 0):
#        sheet.write(rows + row, col, row / 24)
#        col = col + 1
#        continue
#    if (col == 31):
#        col = col + 1
#        continue
#    sheet.write(rows + row, col, dataMat_area1[data_num % 360])
#    col = col + 1
#    data_num = data_num + 1
#    if (col == 47):
#        col = 0
#        row = row + 1
#        data_num = data_num - 30
# workbook_area1.save(excel_area1_name)

# 5.保存 & 展示图片
pic_path = os.path.join(os.path.join(new_path, '..'), 'pic')
pic_path = os.path.join(pic_path, date_form)
# 图片名称采取: '日期+业务量' 命名
# 5.0 系统兼容性适应
if system_name == 'Windows':
    figure_name = pic_path + '\\' + date_form + \
        '_ratio' + str(service_ratio) + '.png'
else:
    figure_name = pic_path + '/' + date_form + \
        '_ratio' + str(service_ratio) + '.png'

# 5.1 防止图片重名
addition_num = 1
while os.path.isfile(figure_name):  # 如果图片重名，则在名称后附加'_数字'
    if system_name == 'Windows':
        figure_name = pic_path + '\\' + date_form + \
            '_ratio' + str(service_ratio) + '_' + str(addition_num) + '.png'
    else:
        figure_name = pic_path + '/' + date_form + \
            '_ratio' + str(service_ratio) + '_' + str(addition_num) + '.png'
    addition_num = addition_num + 1
# 5.2 保存图片时防止路径不存在抛异常
if os.path.exists(pic_path):    # 如果路径存在，则直接保存
    plt.savefig(figure_name)
else:   # 如果不存在，则创建路径保存图片
    os.makedirs(r'' + pic_path)
    plt.savefig(figure_name)
plt.show()  # 展示图片
