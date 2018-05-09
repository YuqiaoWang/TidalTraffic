import tensorflow as tf
import numpy as np
import xlrd
import xlwt

#定义全局参量
#输入层神经元
input_neural_num = 31
#隐藏层神经元
hidden_neural_num = 40
#输出层神经元
output_neural_num = 15

#添加层
def add_layer(inputs, in_size, out_size, activation_function=None):	
    # add one more layer and return the output of this layer
    weights = tf.Variable(tf.random_normal([in_size, out_size]))
    biases = tf.Variable(tf.zeros([1, out_size]) + 0.1)
    wx_plus_b = tf.matmul(inputs, weights) + biases
    if activation_function is None:
        outputs = wx_plus_b
    else:
        outputs = activation_function(wx_plus_b)
    parameters = [outputs, weights, biases]
    return parameters

# 1.训练的数据
# 从excel中读取数据
workbook = xlrd.open_workbook(r'data/edgeload.xlsx')
sheet1 = workbook.sheet_by_index(1)
x_data_raw = []
y_data_raw = []
for i in range(1, sheet1.ncols):
	xdata = sheet1.col_values(i)[:input_neural_num]
	ydata = sheet1.col_values(i)[input_neural_num:]
	x_data_raw.append(xdata)
	y_data_raw.append(ydata)

x_data = np.array(x_data_raw)
y_data = np.array(y_data_raw)

#x_data = np.array(sheet1.col_values(0))[:, np.newaxis]
#y_data = np.array(sheet1.col_values(2))[:, np.newaxis]



# 2.定义节点准备接收数据
# define placeholder for inputs to network
xs = tf.placeholder(tf.float32, [None, input_neural_num])
ys = tf.placeholder(tf.float32, [None, output_neural_num])

# 3.定义神经层：隐藏层和预测层
# add hidden layer 输入值是 xs(31个神经元), 在隐藏层有30个神经元
layerparameters1 = add_layer(xs, input_neural_num, hidden_neural_num, activation_function=tf.nn.relu)
l1 = layerparameters1[0]
Weights1 = layerparameters1[1]
biases1 = layerparameters1[2]

# 隐藏层2
#layerparameters2 = add_layer(l1, 30, 20, activation_function = tf.nn.relu)
#l2 = layerparameters2[0]


# add output layer 输入值是隐藏层l1,在预测层输出1个结果
layerparameters3 = add_layer(l1, hidden_neural_num, output_neural_num, activation_function=None)
prediction = layerparameters3[0]
Weights3 = layerparameters3[1]
biases3 = layerparameters3[2]
# 4.定义loss表达式
# the error between prediction and real data
loss = tf.reduce_mean(tf.reduce_sum(tf.square(ys-prediction), reduction_indices=[1]))

# 5.选择optimizer使loss达到最小
#这一行定义了用什么方式去减少loss,学习率是0.1
train_step = tf.train.GradientDescentOptimizer(0.005).minimize(loss)

# import step 对所有变量进行初始化
init = tf.initialize_all_variables()
sess = tf.Session()
# 上面定义的都没有运算，直到sess.run 才会开始运算
sess.run(init)

# 迭代1000次学习， sess.run optimizer
for i in range(100000):
# training train_step 和 loss 都是由 placeholder 定义的运算，所以这里要用 feed 传入参数
    sess.run(train_step, feed_dict={xs: x_data, ys: y_data})
    if i % 500 == 0:
        # to see the step improvement
        print(sess.run(loss, feed_dict={xs: x_data, ys: y_data}))

# 6.验证部分
#for i in range (800, 820):
#    print(sess.run(prediction, feed_dict={xs:x_data[i][:, np.newaxis]}))
out_workbook = xlwt.Workbook()
out_sheet = out_workbook.add_sheet('prediction')
for i in range(0, 24):
#    x_feed = np.transpose(x_data[i + 48][:,np.newaxis])
    x_feed = np.transpose(x_data[i][:, np.newaxis])
    xl_out_data = sess.run(prediction, feed_dict={xs: x_feed})
    xl_write_data = xl_out_data.tolist()
    row = out_sheet.row(i)
    for j in range(0, output_neural_num):
        row.write(j, xl_write_data[0][j])
out_workbook.save('data/edgePredictOutput.xls')


#x_feed = np.transpose(x_data[11][:,np.newaxis])
#print(sess.run(prediction, feed_dict={xs : x_feed}))
#for i in range(0:24):
#    x_feed = np.transpose(x_data[i][:, np.newaxis])
#    xl_write_data = sess.run(prediction, feed_dict={xs : x_feed})
        
