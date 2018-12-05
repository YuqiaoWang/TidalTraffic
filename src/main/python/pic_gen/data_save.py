# -*- coding:utf-8 -*-
import sys
import os
import xlwt
import xlrd
from xlutils.copy import copy as xl_copy


def save_data(data_mat, area_name, path_name, service_ratio):
    # todo:先判断workbook是否存在
    sheet_name = 'ratio' + str(service_ratio)    # 要写入的sheet名称
    excel_name = os.path.join(
        path_name, 'training_data_' + area_name + '.xls')  # 要写入的excel文件名称
    rows = 0  # 该表单已有数据行
    cols = 0  # 该表单已有数据列
    if os.path.isfile(excel_name):    # 如果excel文件存在
        book = xlrd.open_workbook(excel_name)
        book_sheet_names = book.sheet_names()
        workbook = xl_copy(book)  # 将只读转换为可写(book -> workbook)
        if sheet_name in book_sheet_names:    # 如果表单存在
            sheet_index = book_sheet_names.index(sheet_name)
            sheet = workbook.get_sheet(sheet_index)
            rows = book.sheet_by_name(sheet_name).nrows
            cols = book.sheet_by_name(sheet_name).ncols
        else:  # 如果表单不存在
            sheet = workbook.add_sheet(sheet_name)
    else:   # 如果excel文件不存在
        workbook = xlwt.Workbook(encoding='utf-8')
        sheet = workbook.add_sheet(sheet_name)
    # todo:此处要调用写入函数
    col = 0
    row = 0
    data_num = 0
    while row < 24:
        if (col == 0):
            sheet.write(rows + row, col, row / 24)
            col = col + 1
            continue
        if (col == 31):
            col = col + 1
            continue
        sheet.write(rows + row, col, data_mat[data_num % 360])
        col = col + 1
        data_num = data_num + 1
        if (col == 47):
            col = 0
            row = row + 1
            data_num = data_num - 30
    workbook.save(excel_name)
