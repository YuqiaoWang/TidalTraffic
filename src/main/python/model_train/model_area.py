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


# 添加层
def add_layer(inputs, in_size, out_size, activation_function=None, weight_name=None, bias_name=None):
    # add one more layer and return the output of this layer
    Weights = tf.Variable(tf.random_normal(
        [in_size, out_size]), name=weight_name)
    biases = tf.Variable(tf.zeros([1, out_size]) + 0.1, name=bias_name)
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
json_file_name = 'model_config.json'  # 模型配置的json文件（当前路径下）
json_file = open(json_file_name, encoding='utf-8')
config_content = json.load(json_file)
model_type = config_content['model']['type']  # 用与区别模型是area还是link
area_name = config_content['model']['area_name']  # 区域名称
service_ratio = config_content['model']['service_ratio']  # 业务量
input_num = config_content['param']['input']  # 输入层神经元个数
output_num = config_content['param']['output']  # 输出层神经元个数
hidden_layer = config_content['param']['hidden']['layer']  # 隐藏层层数
hidden_neurons = config_content['param']['hidden']['neuron']  # 隐藏层各层神经元个数

# 1.训练数据的读取处理
# 1.1 文件读取
parent_path = os.path.abspath(os.path.join(sys.path[0], '..'))
source_path = parent_path
for i in range(3):
    source_path = os.path.abspath(os.path.join(source_path, '..'))
data_path = os.path.join(source_path, 'data')
data_path = os.path.join(data_path, 'load')
file_name = 'training_data_' + area_name + '.xls'  # 训练数据文件名
file_path = os.path.join(data_path, file_name)  # 训练数据文件路径
book = xlrd.open_workbook(file_path)
sheet_name = 'ratio' + str(service_ratio)  # 表单名
if sheet_name not in book.sheet_names():  # 鲁棒性判断，防止表单不存在
    raise RuntimeError('sheet does not exist!')
sheet = book.sheet_by_name(sheet_name)
# 1.2 数据拼凑(shape)
x_data_raw = []
y_data_raw = []
rols = sheet.nrows  # 行数，即训练数据样本数量
for i in range(1, rols):
    xdata = sheet.row_values(i)[: input_num]  # 单个输入样本数据
    ydata = sheet.row_values(i)[input_num:]  # 单个输出样本数据
    x_data_raw.append(xdata)
    y_data_raw.append(ydata)
x_data = np.array(x_data_raw)
y_data = np.array(y_data_raw)

# 2.定义节点准备接收数据
# define placeholder for inputs to network
xs = tf.placeholder(tf.float32, [None, input_num], name='input_x')
ys = tf.placeholder(tf.float32, [None, output_num], name='input_y')

# 3.定义神经层：隐藏层和预测层
layers_params = []
current_layer_param = []
for i in range(0, hidden_layer):
    if i == 0:
        current_layer_param = add_layer(
            xs, input_num, hidden_neurons[0], activation_function=tf.nn.relu, weight_name='weight_' + str(i), bias_name='bias_' + str(i))
    else:
        current_layer_param = add_layer(current_layer_param[0], hidden_neurons[i-1], hidden_neurons[i],
                                        activation_function=tf.nn.sigmoid, weight_name='weight_' + str(i), bias_name='bia_' + str(i))
    layers_params.append(current_layer_param)

output_layer = add_layer(layers_params[-1][0], hidden_neurons[-1], output_num, activation_function=None,
                         weight_name='weight_' + str(hidden_layer), bias_name='bias_' + str(hidden_layer))
layers_params.append(output_layer)
prediction = output_layer[0]  # 输出层
weight_last = output_layer[1]  # 最后一层隐藏层到输出层的weights
bias_last = output_layer[2]  # 最后一层阴藏层到输出层的bias


# 4.定义loss表达式
# the error between prediction and real data
loss = tf.reduce_mean(tf.reduce_sum(
    tf.square(ys-prediction), reduction_indices=[1]))
#cross_entropy = (-1) * tf.reduce_sum(ys * tf.log(prediction))
#loss2 = tf.reduce_mean(cross_entropy)
#cross_entropy = tf.reduce_mean(-tf.reduce_sum(ys * tf.log(prediction), reduction_indices=[1]))
#cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels= ys, logits=prediction))
#cross_ent = -tf.reduce_mean(tf.reduce_sum(ys * tf.log(prediction), reduction_indices=[1]))
#cross_ent = tf.reduce_mean(tf.nn.sparse_softmax_cross_entropy_with_logits())

# 5.选择optimizer使loss达到最小
# 这一行定义了用什么方式去减少loss,学习率是0.005
#train_step = tf.train.GradientDescentOptimizer(0.005).minimize(cross_entropy)
train_step = tf.train.GradientDescentOptimizer(0.005).minimize(loss)
#lr = 1e-4
#train_step = tf.train.AdamOptimizer(lr).minimize(cross_entropy)

# import step 对所有变量进行初始化
init = tf.global_variables_initializer()
#sess = tf.Session()
sess = tf.InteractiveSession()
sess.run(init)  # 上面定义的都没有运算，直到sess.run 才会开始运算


saver = tf.train.Saver(max_to_keep=1)

# 迭代1000次学习， sess.run optimizer
step = 100000
for i in range(step):
    # training train_step 和 loss 都是由 placeholder 定义的运算，所以这里要用 feed 传入参数
    sess.run(train_step, feed_dict={xs: x_data, ys: y_data})
    if i % 5000 == 0:
        # to see the step improvement
        #print(sess.run(loss, feed_dict={xs: x_data, ys: y_data}))
        #currentLoss = sess.run(loss, feed_dict={xs: x_data, ys: y_data})
        currentLoss = sess.run(loss, feed_dict={xs: x_data, ys: y_data})
        print('epoch:%d, val_loss:%f' % (i, currentLoss))

        # saver.save(sess, 'model_save/100erlang/model_area1.ckpt', global_step=i)


# 6.验证部分
# for i in range (800, 820):
#    print(sess.run(prediction, feed_dict={xs:x_data[i][:, np.newaxis]}))
out_workbook = xlwt.Workbook()
origin_sheet = out_workbook.add_sheet('origin_data')
out_sheet = out_workbook.add_sheet('prediction')
for i in range(0, 24):
    #    x_feed = np.transpose(x_data[i + 96][:,np.newaxis])
    x_feed = np.transpose(x_data[i][:, np.newaxis])
    if i is 1:
        print(x_feed)
    xl_out_data = sess.run(prediction, feed_dict={xs: x_feed})
    xl_write_data = xl_out_data.tolist()
    # 保存验证集结果数据
    row = out_sheet.row(i)
    # for j in range(0, 16):
    #    row.write(j, xl_write_data[0][j])
    # 保存验证集原始数据
    #row = origin_sheet.row(i)
    # for j in range(0, 31):
    #    row.write(j, x_data_raw[xi][j])
# out_workbook.save('data/100erlang/area1_validation.xls')
print('验证集数据已保存')

# print("b1:")
# print(sess.run(biases1))
# print("b2:")
# print(sess.run(biases2))


# 7.参数保存
model_save_path = os.path.join(parent_path, 'model_save')
model_save_path = os.path.join(model_save_path, model_type)
model_save_path = os.path.join(model_save_path, 'ratio_' + str(service_ratio))
if not os.path.exists(model_save_path):
    os.makedirs(r'' + model_save_path)
model_file_name = ''
for i in (0, hidden_layer):
    if (i == 0):
        model_file_name = str(area_name) + '/' + str(hidden_neurons[0])
    else:
        model_file_name = model_file_name + '/' + hidden_neurons[i]
model_file_name = os.path.join(model_save_path, model_file_name)
saver = tf.train.Saver()
with tf.Session() as sess:
    sess.run(tf.global_variables_initializer())
    #saver.save(sess, './model_save/tidal-model.ckpt', global_step=step)
    saver.save(sess, model_file_name, global_step=step)
print('模型参数已保存')


sess.close()
#x_feed = np.transpose(x_data[11][:,np.newaxis])
#print(sess.run(prediction, feed_dict={xs : x_feed}))
# for i in range(0:24):
#    x_feed = np.transpose(x_data[i][:, np.newaxis])
#    xl_write_data = sess.run(prediction, feed_dict={xs : x_feed})
