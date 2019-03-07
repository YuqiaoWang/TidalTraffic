import xlrd
import os
import sys
import json

parent_path = os.path.abspath(os.path.join(sys.path[0], '..'))

config_path = os.path.abspath(os.path.join(parent_path, 'model_train'))
json_file_name = os.path.abspath(
    os.path.join(config_path, 'model_config.json'))
json_file = open(json_file_name, encoding='utf-8')
config_content = json.load(json_file)
area_name = config_content['model']['area_name']                # 区域名称
service_ratio = config_content['model']['service_ratio']        # 业务量
input_num = config_content['param']['input']                    # 输入层神经元个数
output_num = config_content['param']['output']                  # 输出层神经元个数
hidden_layer = config_content['param']['hidden']['layer']       # 隐藏层层数
hidden_neurons = config_content['param']['hidden']['neuron']    # 隐藏层各层神经元个数

target_path = os.path.abspath(os.path.join(parent_path, 'model_save'))
target_path = os.path.abspath(os.path.join(target_path, 'area'))
target_path = os.path.abspath(os.path.join(
    target_path, 'ratio_' + str(service_ratio)))
target_path = os.path.abspath(os.path.join(target_path, 'test'))

target_file_name = area_name

for i in range(0, hidden_layer):
    if (i == 0):
        target_file_name = str(area_name) + '_' + str(hidden_neurons[0])
    else:
        target_file_name = target_file_name + '_' + str(hidden_neurons[i])

bookname = os.path.abspath(os.path.join(
    target_path, target_file_name + '.xls'))
book = xlrd.open_workbook(bookname)
sheet = book.sheet_by_name('prediction')
col_data = sheet.col_values(0)
true_list = []
for i in col_data:
    if i > 2:
        true_list.append(1)
    else:
        true_list.append(0)

tp = 0
tn = 0
fp = 0
fn = 0
real_true = [11, 35, 36, 59, 83]
for j in range(0, len(true_list)):
    if (j in real_true) and (true_list[j] == 1):
        tp += 1
    if (j in real_true) and (true_list[j] == 0):
        fp += 1
    if (j not in real_true) and (true_list[j] == 0):
        tn += 1
    if (j not in real_true) and (true_list[j] == 1):
        fn += 1

print('tn: ' + str(tn))
print('fn: ' + str(fn))
print('tp: ' + str(tp))
print('fp: ' + str(fp))
