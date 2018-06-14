import tensorflow as tf
import numpy as np

class edge_parameter:
    #默认 srcNode 比 desNode 编号小
    def __init__(self):
        #self.srcNode = srcNode
        #self.desNode = desNode
        self.graph = tf.Graph()
        self.sess = tf.Session(graph=self.graph)

    def restore(self):
        with self.graph.as_default():
            Weights1 = tf.Variable(tf.truncated_normal(shape=(45, 40)), name='weights1_edge')
            biases1 = tf.Variable(tf.truncated_normal(shape=(1,40)), name='biases1_edge')
            #隐藏层到输出层的权重偏置
            Weights2 = tf.Variable(tf.truncated_normal(shape=(40,15)), name='weights2_edge')
            biases2 = tf.Variable(tf.truncated_normal(shape=(1, 15)), name='biases2_edge')
            xs = tf.placeholder(tf.float32, [None, 45])

            # 2.恢复
            saver = tf.train.Saver()
            modelName = "model_edge.ckpt-100000"
            modelPath = "model_save/100erlang/" + modelName
            saver.restore(self.sess, modelPath)
            l1 = restore_layer(xs, self.sess.run(Weights1), self.sess.run(biases1), activation_function=tf.nn.relu)
            prediction = restore_layer(l1, self.sess.run(Weights2), self.sess.run(biases2), activation_function=None)
            model_param = [self.sess, prediction, xs]
            print("从模型中恢复的biases1:")
            print(self.sess.run(biases1))
        return model_param
            
    #添加层
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
    #3. 预测
    pre_sess = model_param[0]
    prediction = model_param[1]
    xs = model_param[2]
    print('输入边流量数据')
    #print(input_data)
    x_test_raw = input_data
    x_feed_pre = np.array(x_test_raw)
    x_feed = x_feed_pre.reshape((1, 45))
    print(x_feed)
    out_data = pre_sess.run(prediction, feed_dict={xs : x_feed})
    print('输出流量数据')
    print(out_data)
    return out_data