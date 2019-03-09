# -*- coding:utf-8 -*-
import tensorflow as tf
import numpy as np
import sys
import platform
import datetime
import json
import os
import xlrd
import xlwt
from xlutils.copy import copy as xl_copy
import sklearn.metrics as metrics

# 添加层


def add_layer(inputs, in_size, out_size, activation_function=None, weight_name=None, bias_name=None):
    # add one more layer and return the output of this layer
    Weights = tf.Variable(initial_value=tf.random_normal(
        [in_size, out_size]), name=weight_name)
    biases = tf.Variable(initial_value=tf.zeros(
        [1, out_size])+0.1, name=bias_name)
    Wx_plus_b = tf.matmul(inputs, Weights) + biases
    if activation_function is None:
        outputs = Wx_plus_b
    else:
        outputs = activation_function(Wx_plus_b)
    parameters = [outputs, Weights, biases]
    return parameters



# 0. 获取系统和配置信息
now_time = datetime.datetime.now()
date_form = now_time.strftime('%Y%m%d')
system_name = platform.system()
# 模型配置的json文件（当前路径下）
json_file_name = 'edge_model_config.json'
json_file = open(json_file_name, encoding='utf-8')
config_content = json.load(json_file)
# 用与区别模型是area还是link
model_type = config_content['model']['type']
service_ratio = config_content['model']['service_ratio']        # 业务量
edge = config_content['edge']                                   # 边集
input_num = config_content['param']['input']                    # 输入层神经元个数
output_num = config_content['param']['output']                  # 输出层神经元个数
hidden_layer = config_content['param']['hidden']['layer']       # 隐藏层层数
hidden_neurons = config_content['param']['hidden']['neuron']    # 隐藏层各层神经元个数
step = config_content['param']['step']                          # 训练迭代次数

# 1.训练数据的读取处理
# 1.1 文件读取
parent_path = os.path.abspath(os.path.join(sys.path[0], '..'))
source_path = parent_path
for i in range(3):
    source_path = os.path.abspath(os.path.join(source_path, '..'))
data_path = os.path.join(source_path, 'data')
data_path = os.path.join(data_path, 'load')
data_path = os.path.join(data_path, 'edgeload')
file_name = 'training_data_edge.xls'                        # 训练数据文件名
file_path = os.path.join(data_path, file_name)              # 训练数据文件路径
book = xlrd.open_workbook(file_path)
train_sheet_name = 'ratio' + str(service_ratio)             # [训练集]表单名
test_sheet_name = 'ratio' + str(service_ratio) + '_test'    # [测试集]表单名
if train_sheet_name not in book.sheet_names():              # 鲁棒性判断，防止表单不存在
    raise RuntimeError('train data sheet does not exist!')
if test_sheet_name not in book.sheet_names():
    raise RuntimeError('test data sheet does not exist!')
train_sheet = book.sheet_by_name(train_sheet_name)          # 拿到训练数据表单
test_sheet = book.sheet_by_name(test_sheet_name)  # 拿到测试集数据表单

# 1.2 数据拼凑(shape)
x_train_data_raw = []
y_train_data_raw = []
train_rols = train_sheet.nrows                      # 训练数据样本数量
test_rols = test_sheet.nrows                        # 测试数据样本数量
# 1.2.1 训练数据
for i in range(1, train_rols):
    xdata = train_sheet.row_values(i)[: input_num]  # 单个输入样本数据
    ydata = train_sheet.row_values(i)[input_num:]   # 单个输出样本数据
    x_train_data_raw.append(xdata)
    y_train_data_raw.append(ydata)
x_train_data = np.array(x_train_data_raw)
y_train_data = np.array(y_train_data_raw)
# 1.2.2 测试数据
x_test_data_raw = []
y_test_data_raw = []
for i in range(0, test_rols):
    xdata = test_sheet.row_values(i)[: input_num]   # 单个输入样本数据
    ydata = test_sheet.row_values(i)[input_num:]    # 单个输出样本数据
    x_test_data_raw.append(xdata)
    y_test_data_raw.append(ydata)
x_test_data = np.array(x_test_data_raw)
y_test_data = np.array(y_test_data_raw)


# 2.定义节点准备接收数据
# define placeholder for inputs to network
with tf.name_scope('input'):
    xs = tf.placeholder(dtype=tf.float32, shape=[
                        None, input_num], name='input_x')
    ys = tf.placeholder(dtype=tf.float32, shape=[
                        None, output_num], name='input_y')

# 3.定义神经层：隐藏层和预测层
layers_params = []
current_layer_param = []
for i in range(0, hidden_layer):
    if i == 0:
        current_layer_param = add_layer(
            xs, input_num, hidden_neurons[0], activation_function=tf.nn.relu, weight_name='weight_edge_' + str(i), bias_name='bias_edge_' + str(i))
    else:
        current_layer_param = add_layer(current_layer_param[0], hidden_neurons[i-1], hidden_neurons[i],
                                        activation_function=tf.nn.sigmoid, weight_name='weight_edge_' + str(i), bias_name='bias_edge_' + str(i))
    layers_params.append(current_layer_param)

output_layer = add_layer(layers_params[-1][0], hidden_neurons[-1], output_num, activation_function=None,
                         weight_name='weight_edge_' + str(hidden_layer), bias_name='bias_edge_' + str(hidden_layer))
layers_params.append(output_layer)
prediction = output_layer[0]    # 输出层
weight_last = output_layer[1]   # 最后一层隐藏层到输出层的weights
bias_last = output_layer[2]     # 最后一层阴藏层到输出层的bias


# 4.定义loss表达式
# the error between prediction and real data
with tf.name_scope('loss'):
    loss = tf.reduce_mean(tf.reduce_sum(
        tf.square(ys - prediction), reduction_indices=[1]))
    tf.summary.scalar('loss', loss)

# 5.选择optimizer使loss达到最小
train_step = tf.train.GradientDescentOptimizer(0.005).minimize(loss)
lr = 1e-4

# import step 对所有变量进行初始化
init = tf.global_variables_initializer()
sess = tf.InteractiveSession()

sess.run(init)  # 上面定义的都没有运算，直到sess.run 才会开始运算

saver = tf.train.Saver(max_to_keep=1)  # 模型保存对象

# 6.训练过程
begin_time = datetime.datetime.now()
for i in range(step):
    # training train_step 和 loss 都是由 placeholder 定义的运算，所以这里要用 feed 传入参数
    sess.run(train_step, feed_dict={xs: x_train_data, ys: y_train_data})
    if i % 5000 == 0:
        currentLoss = sess.run(
            loss, feed_dict={xs: x_train_data, ys: y_train_data})
        print('epoch:%d, val_loss:%f' % (i, currentLoss))

end_time = datetime.datetime.now()

# 7.0 & 8.0 路径定义
model_save_path = os.path.join(parent_path, 'model_save')
model_save_path = os.path.join(model_save_path, model_type)
model_save_path = os.path.join(model_save_path, 'ratio_' + str(service_ratio))

# 7.测试部分
out_workbook = xlwt.Workbook()
origin_sheet = out_workbook.add_sheet('origin_data')
out_sheet = out_workbook.add_sheet('prediction')
y_test_predict = []  # 测试集对应的预测结果(回归)
tidal_predict = []  # 测试集潮汐标识位预测结果(分类)
for i in range(0, test_rols):
    x_feed = np.transpose(x_test_data[i][:, np.newaxis])
    xl_out_data = sess.run(prediction, feed_dict={xs: x_feed})
    xl_write_data = xl_out_data.tolist()  # 矩阵转二维数组

    if y_test_predict == []:
        y_test_predict = xl_write_data
    else:
        y_test_predict = np.concatenate(
            (y_test_predict, xl_write_data), axis=0)
    # 保存测试集结果数据
    row = out_sheet.row(i)
    for j in range(0, 15):
        row.write(j, xl_write_data[0][j])
    # 保存测试集原始数据
    row = origin_sheet.row(i)
    for j in range(0, 45):
        row.write(j, x_test_data_raw[i][j])

out_workbook_path = os.path.join(model_save_path, 'test')
if not os.path.exists(out_workbook_path):
    os.makedirs(r'' + out_workbook_path)
out_workbook_name = ''
for i in range(0, hidden_layer):
    if (i == 0):
        out_workbook_name = 'edge_' + str(hidden_neurons[0])
    else:
        out_workbook_name = out_workbook_name + '_' + str(hidden_neurons[i])
out_workbook_name = os.path.join(out_workbook_path, out_workbook_name+'.xls')
out_workbook.save(out_workbook_name)
print('验证集数据已保存')

# 8.模型保存
if not os.path.exists(model_save_path):
    os.makedirs(r'' + model_save_path)
model_file_name = ''
for i in range(0, hidden_layer):
    if (i == 0):
        model_file_name = 'edge_' + str(hidden_neurons[0])
    else:
        model_file_name = model_file_name + '_' + str(hidden_neurons[i])
model_file_name = os.path.join(model_save_path, model_file_name)
saver = tf.train.Saver()
with tf.Session() as sess:
    sess.run(tf.global_variables_initializer())
    saver.save(sess, model_file_name, global_step=step)
print('模型参数已保存')

training_time = end_time - begin_time
print('训练运行时间:' + str(training_time))


sess.close()