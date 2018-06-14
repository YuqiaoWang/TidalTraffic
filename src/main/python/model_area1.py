import tensorflow as tf
import numpy as np
import xlrd
import xlwt

#添加层
def add_layer(inputs, in_size, out_size, activation_function=None, weight_name=None, bias_name=None):	
    # add one more layer and return the output of this layer
    Weights = tf.Variable(tf.random_normal([in_size, out_size]), name=weight_name)
    biases = tf.Variable(tf.zeros([1, out_size]) + 0.1, name=bias_name)
    Wx_plus_b = tf.matmul(inputs, Weights) + biases
    if activation_function is None:
        outputs = Wx_plus_b
    else:
        outputs = activation_function(Wx_plus_b)
    parameters = [outputs, Weights, biases]
    return parameters

# 1.训练的数据
# 从excel中读取数据
#filepath = 'data/sheet.xlsx'
#filepath = '/home/yuqiaowang/learnTensorflow/data/sheet.xlsx'
workbook = xlrd.open_workbook(r'data/100erlang/areasCount.xls')
sheet1 = workbook.sheet_by_index(0)
x_data_raw = []
y_data_raw = []
cols = sheet1.ncols
#cols = 120
for i in range(1, cols):
	xdata = sheet1.col_values(i)[:31]
	ydata = sheet1.col_values(i)[31:]
	x_data_raw.append(xdata)
	y_data_raw.append(ydata)

x_data = np.array(x_data_raw)
y_data = np.array(y_data_raw)

#x_data = np.array(sheet1.col_values(0))[:, np.newaxis]
#y_data = np.array(sheet1.col_values(2))[:, np.newaxis]



# 2.定义节点准备接收数据
# define placeholder for inputs to network
xs = tf.placeholder(tf.float32, [None, 31], name='input_x')
ys = tf.placeholder(tf.float32, [None, 16], name='input_y')

# 3.定义神经层：隐藏层和预测层
# add hidden layer 输入值是 xs(31个神经元), 在隐藏层有30个神经元
layerparameters1 = add_layer(xs, 31, 40, activation_function=tf.nn.relu, weight_name='weights1_area1', bias_name='biases1_area1')
l1 = layerparameters1[0]
Weights1 = layerparameters1[1]
biases1 = layerparameters1[2]

# 隐藏层2
layerparameters2 = add_layer(l1, 40, 10, activation_function = tf.nn.sigmoid, weight_name='weights2_area1', bias_name='biases2_area1')
l2 = layerparameters2[0]
Weights2 = layerparameters2[1]
biases2 = layerparameters2[2]

# add output layer 输入值是隐藏层l1,在预测层输出1个结果
layerparameters3 = add_layer(l2, 10, 16, activation_function=None, weight_name='weights3_area1', bias_name='biases3_area1')
prediction = layerparameters3[0]
Weights3 = layerparameters3[1]
biases3 = layerparameters3[2]

# 4.定义loss表达式
# the error between prediction and real data
loss = tf.reduce_mean(tf.reduce_sum(tf.square(ys-prediction), reduction_indices=[1]))
cross_entropy = -tf.reduce_sum(ys * tf.log(prediction))
loss2 = tf.reduce_mean(cross_entropy)
#cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels= ys, logits=prediction))
#cross_ent = -tf.reduce_mean(tf.reduce_sum(ys * tf.log(prediction), reduction_indices=[1]))
#cross_ent = tf.reduce_mean(tf.nn.sparse_softmax_cross_entropy_with_logits())

# 5.选择optimizer使loss达到最小
#这一行定义了用什么方式去减少loss,学习率是0.005
#train_step = tf.train.GradientDescentOptimizer(0.005).minimize(cross_entropy)
train_step = tf.train.GradientDescentOptimizer(0.005).minimize(loss)
lr = 1e-4
#train_step = tf.train.AdamOptimizer(lr).minimize(cross_entropy)


# import step 对所有变量进行初始化
init = tf.global_variables_initializer()
#sess = tf.Session()
sess = tf.InteractiveSession()
# 上面定义的都没有运算，直到sess.run 才会开始运算
sess.run(init)

saver=tf.train.Saver(max_to_keep=1)

# 迭代1000次学习， sess.run optimizer
step = 100000
for i in range(step):
# training train_step 和 loss 都是由 placeholder 定义的运算，所以这里要用 feed 传入参数
    sess.run(train_step, feed_dict={xs: x_data, ys: y_data})
    if i % 5000 == 0:
        # to see the step improvement
        #print(sess.run(loss, feed_dict={xs: x_data, ys: y_data}))
        #currentLoss = sess.run(loss, feed_dict={xs: x_data, ys: y_data})
        currentLoss = sess.run(loss, feed_dict={xs:x_data, ys:y_data})
        print('epoch:%d, val_loss:%f' %(i, currentLoss))
        
        saver.save(sess, 'model_save/100erlang/model_area1.ckpt',global_step=i)
        
    

# 6.验证部分
#for i in range (800, 820):
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
    #保存验证集结果数据
    row = out_sheet.row(i)
    for j in range(0, 16):
        row.write(j, xl_write_data[0][j])
    #保存验证集原始数据
    row = origin_sheet.row(i)
    for j in range(0, 31):
        row.write(j, x_data_raw[i][j])
out_workbook.save('data/100erlang/area1_validation.xls')
print('验证集数据已保存')

print("b1:")
print(sess.run(biases1))
print("b2:")
print(sess.run(biases2))


# 7.参数保存
#saver = tf.train.Saver()
#with tf.Session() as sess:
#    sess.run(tf.global_variables_initializer())
#    saver.save(sess, './model_save/tidal-model.ckpt', global_step=step)
print('模型参数已保存')


sess.close()
#x_feed = np.transpose(x_data[11][:,np.newaxis])
#print(sess.run(prediction, feed_dict={xs : x_feed}))
#for i in range(0:24):
#    x_feed = np.transpose(x_data[i][:, np.newaxis])
#    xl_write_data = sess.run(prediction, feed_dict={xs : x_feed})
        
