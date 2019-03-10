import tensorflow as tf
import numpy as np
import sys
import platform
import os


class parameter:
    def __init__(self, areaId, hidden_layer, hidden_neuron, service_ratio):
        self.areaId = areaId
        self.graph = tf.Graph()
        #self.sess = tf.Session(graph=self.graph)
        self.sess = tf.Session()
        self.hidden_layer = hidden_layer
        self.hidden_neuron = hidden_neuron
        self.service_ratio = service_ratio
        '''
        self.Weights1 = tf.Variable(tf.truncated_normal(shape=(31, 40)), name='weights1_area'+areaId)
        self.biases1 = tf.Variable(tf.truncated_normal(shape=(1, 40)), name='biases1_area'+areaId)
        # 隐藏层到输出层的权重偏置
        self.Weights2 = tf.Variable(tf.truncated_normal(shape=(40, 10)), name='weights2_area'+areaId)
        self.biases2 = tf.Variable(tf.truncated_normal(shape=(1, 10)), name='biases2_area'+areaId)
        self.Weights3 = tf.Variable(tf.truncated_normal(shape=(10, 16)), name='weights3_area'+areaId)
        self.biases3 = tf.Variable(tf.truncated_normal(shape=(1, 16)), name='biases3_area'+areaId)
        self.xs = tf.placeholder(tf.float32, [None, 31])
        '''
    '''
    def __enter__(self):
        return self.sess;

    def __exit__(self, type, value, traceback):
        selft.close();
    '''

    def restore(self):
        # 1.定义变量
        # 此程序用来做流量预测模型的参数恢复
        # 输入层到隐藏层的权重偏置
        '''
        Weights1 = tf.Variable(tf.truncated_normal(shape=(31, 40)), name='weights1_area'+self.areaId)
        biases1 = tf.Variable(tf.truncated_normal(shape=(1, 40)), name='biases1_area'+self.areaId)
        # 隐藏层到输出层的权重偏置
        Weights2 = tf.Variable(tf.truncated_normal(shape=(40, 10)), name='weights2_area'+self.areaId)
        biases2 = tf.Variable(tf.truncated_normal(shape=(1, 10)), name='biases2_area'+self.areaId)
        Weights3 = tf.Variable(tf.truncated_normal(shape=(10, 16)), name='weights3_area'+self.areaId)
        biases3 = tf.Variable(tf.truncated_normal(shape=(1, 16)), name='biases3_area'+self.areaId)
        xs = tf.placeholder(tf.float32, [None, 31])
        '''
        '''
        with tf.Session() as sess:
            new_saver = tf.train.import_meta_graph('./model_save/tidal-model.ckpt.meta')
            new_saver.restore(sess, tf.train.latest_checkpoint('./model_save/'))
            all_vars = tf.get_collection('vars')
            print(all_vars)
            for v in all_vars:
                print(v)
                print(v.name)
                v_ = v.eval() #sess.run(v)
                print(v_)
        '''
        with self.graph.as_default():
            layers = self.hidden_layer
            '''
            for i in range(layers):
                weight_name = 'weight_' + str(i)
                bias_name = 'bias_' + str(i)
                if i == 0:
                    up_neuron = 16
                    down_neuron = self.hidden_neuron[i]
                    Weights = tf.Variable(tf.truncated_normal(shape=(up_neuron, down_neuron), name='weights' + str(i) + '_area' + self.areaId))
                    biases = tf.Variable(tf.truncated_normal(1, down_neuron), name='biases' + str(i) + 'area' + self.areaId)
                else:
                    up_neuron = self.hidden_neuron[i - 1]
                    down_neuron = self.hidden_neuron[i]
                    Weights = tf.Variable(tf.truncated_normal(shape=(
                        up_neuron, down_neuron), name='weights' + str(i) + '_area' + self.areaId))
                    biases = tf.Variable(tf.truncated_normal(
                        1, down_neuron), name='biases' + str(i) + 'area' + self.areaId)
            '''
            '''
            Weights1 = tf.Variable(tf.truncated_normal(
                shape=(31, 20)), name='weight_0')
            biases1 = tf.Variable(tf.truncated_normal(
                shape=(1, 20)), name='bias_0')
            # 隐藏层到输出层的权重偏置
            Weights2 = tf.Variable(tf.truncated_normal(
                shape=(20, 15)), name='weight_1')
            biases2 = tf.Variable(tf.truncated_normal(
                shape=(1, 15)), name='bias_1')
            Weights3 = tf.Variable(tf.truncated_normal(
                shape=(15, 16)), name='weight_2')
            biases3 = tf.Variable(tf.truncated_normal(
                shape=(1, 16)), name='bias_2')
            '''
            xs = tf.placeholder(tf.float32, [None, 31])

            # 2.恢复
            #saver = tf.train.Saver()
            # with tf.Session() as sess:
            sess = tf.Session()
            model_storage_path = os.path.abspath(
                os.path.join(sys.path[0], 'model_save'))
            model_storage_path = os.path.abspath(
                os.path.join(model_storage_path, 'area'))
            model_storage_path = os.path.abspath(
                os.path.join(model_storage_path, 'ratio_' + str(self.service_ratio)))
            model_name = os.path.abspath(os.path.join(
                model_storage_path, 'area1_20_15-100000'))
            #modelName = "model_area" + self.areaId + ".ckpt-95000"
            #modelPath = "model_save/100erlang/" + modelName
            #saver.restore(sess, "model_save/tidal-model.ckpt-95000")
            saver = tf.train.import_meta_graph(model_name+'.meta')
            #saver.restore(self.sess, model_name)
            saver.restore(
                sess, tf.train.latest_checkpoint(model_storage_path))
            graph = tf.get_default_graph()
            w1 = graph.get_tensor_by_name('weight_0:0')
            b1 = graph.get_tensor_by_name('bias_0:0')
            w2 = graph.get_tensor_by_name('weight_1:0')
            b2 = graph.get_tensor_by_name('bias_1:0')
            w3 = graph.get_tensor_by_name('weight_2:0')
            b3 = graph.get_tensor_by_name('bias_2:0')

            '''
            print('w1:')
            print(sess.run(Weights1))
            print('b1:')
            print(sess.run(biases1))
            print('w2:')
            print(sess.run(Weights2))
            print('b2:')
            print(sess.run(biases2))
            '''
            l1 = restore_layer(xs, sess.run(w1), sess.run(
                b1), activation_function=tf.nn.relu)
            l2 = restore_layer(l1, sess.run(w2), sess.run(
                b2), activation_function=tf.nn.sigmoid)
            prediction = restore_layer(l2, sess.run(
                w3), sess.run(b3), activation_function=None)
            model_param = [sess, prediction, xs]
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


def restore_specify():
    sess = tf.Session()
    saver = tf.train.import_meta_graph('model_save/tidal-model.meta')
    saver.restore(sess, 'model_save/tidal-model-100000')
    graph = tf.get_default_graph()
    Weights1 = graph.get_tensor_by_name('weights1')
    biases1 = graph.get_tensor_by_name('biases1')
    Weights2 = graph.get_tensor_by_name('weights2')
    biases2 = graph.get_tensor_by_name('biases2')
    Weights3 = graph.get_tensor_by_name('weights3')
    biases3 = graph.get_tensor_by_name('biases3')


def predict(model_param, input_data):
    # 3. 预测
    #x_test_raw = [0.416, 0.481, 0.497, 0.493, 0.504, 0.158, 0.535, 0.527, 0.481, 0.475, 0.491, 0.493, 0.495, 0.510, 0.508, 0.525, 0.522, 0.512, 0.518, 0.522, 0.525, 0.508, 0.512, 0.456, 0.447, 0.443, 0.443, 0.462, 0.477, 0.472, 0.485]
    sess = model_param[0]
    prediction = model_param[1]
    xs = model_param[2]
    print('输入流量数据')
    x_test_raw = input_data
    x_feed_pre = np.array(x_test_raw)
    x_feed = x_feed_pre.reshape((1, 31))
    print(x_feed)
    out_data = sess.run(prediction, feed_dict={xs: x_feed})
    # print('输出流量数据')
    # print(out_data)
    return out_data
