import xlrd
import xlwt


def transpose():
    workbook = xlrd.open_workbook(r'data/output.xls')
    sheet = workbook.sheet_by_index(0)
    data_one_line = []
    for i in range(0, sheet.nrows):
        for j in range(1, sheet.ncols):
            data_one_line.append(sheet.cell_value(i, j))

    out_workbook = xlwt.Workbook()
    out_sheet = out_workbook.add_sheet('post_process')
    row_num = 0
    row = out_sheet.row(row_num)
    for k in range(0, len(data_one_line)):
        row.write(0, data_one_line[k])
        row_num = row_num + 1
        row = out_sheet.row(row_num)
    out_workbook.save('data/output__post_process.xls')


transpose()

