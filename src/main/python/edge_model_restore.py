import tensorflow as tf
import numpy as np
import sys
import platform
import os


class edge_parameter:
    # 默认 srcNode 比 desNode 编号小
    def __init__(self, hidden_layer, hidden_neuron, service_ratio):
        #self.srcNode = srcNode
        #self.desNode = desNode
        self.graph = tf.Graph()
        self.sess = tf.Session(graph=self.graph)
        self.hidden_layer = hidden_layer
        self.hidden_neuron = hidden_neuron
        self.service_ratio = service_ratio

    def restore(self):
        with self.graph.as_default():
            Weights1 = tf.Variable(tf.truncated_normal(
                shape=(45, 20)), name='weight_edge_0')
            biases1 = tf.Variable(tf.truncated_normal(
                shape=(1, 20)), name='bias_edge_0')
            # 隐藏层到输出层的权重偏置
            Weights2 = tf.Variable(tf.truncated_normal(
                shape=(20, 15)), name='weight_edge_1')
            biases2 = tf.Variable(tf.truncated_normal(
                shape=(1, 15)), name='bias_edge_1')
            Weights3 = tf.Variable(tf.truncated_normal(
                shape=(15, 15)), name='weight_edge_2')
            biases3 = tf.Variable(tf.truncated_normal(
                shape=(1, 15)), name='bias_edge_2')
            xs = tf.placeholder(tf.float32, [None, 45])

            # 2.恢复
            saver = tf.train.Saver()
            model_storage_path = os.path.abspath(
                os.path.join(sys.path[0], 'model_save'))
            model_storage_path = os.path.abspath(
                os.path.join(model_storage_path, 'edge'))
            model_storage_path = os.path.abspath(
                os.path.join(model_storage_path, 'ratio_' + str(self.service_ratio)))
            model_name = os.path.abspath(os.path.join(model_storage_path, 'edge_20_15-100000'))
            #modelName = "model_edge.ckpt-100000"
            #modelPath = "model_save/100erlang/" + modelName
            saver.restore(self.sess, model_name)
            l1 = restore_layer(xs, self.sess.run(Weights1), self.sess.run(
                biases1), activation_function=tf.nn.relu)
            l2 = restore_layer(l1, self.sess.run(Weights2), self.sess.run(
                biases2), activation_function=tf.nn.sigmoid)
            prediction = restore_layer(l1, self.sess.run(
                Weights3), self.sess.run(biases3), activation_function=None)
            model_param = [self.sess, prediction, xs]
            # print("从模型中恢复的biases1:")
            # print(self.sess.run(biases1))
        return model_param

    # 添加层


def restore_layer(input, weights_restore, biases_restore, activation_function=None):
    Weights = weights_restore
    biases = biases_restore
    Wx_plus_b = tf.matmul(input, Weights) + biases
    if activation_function is None:
        outputs = Wx_plus_b
    else:
        outputs = activation_function(Wx_plus_b)
    return outputs


def predict(model_param, input_data):
    # 3. 预测
    pre_sess = model_param[0]
    prediction = model_param[1]
    xs = model_param[2]
    print('输入边流量数据')
    # print(input_data)
    x_test_raw = input_data
    x_feed_pre = np.array(x_test_raw)
    x_feed = np.transpose(x_feed_pre[:, np.newaxis])
    #x_feed = x_feed_pre.reshape((1, 45))
    print(x_feed)
    out_data = pre_sess.run(prediction, feed_dict={xs: x_feed})
    print('输出流量数据')
    print(out_data.tolist())
    return out_data
