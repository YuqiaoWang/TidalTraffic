import tensorflow as tf
import numpy as np


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

def restore():
    
    # 1.定义变量
    # 此程序用来做流量预测模型的参数恢复
    # 输入层到隐藏层的权重偏置
    Weights1 = tf.Variable(tf.truncated_normal(shape=(31, 40)), name='weights1')
    biases1 = tf.Variable(tf.truncated_normal(shape=(1, 40)), name='biases1')
    # 隐藏层到输出层的权重偏置
    Weights2 = tf.Variable(tf.truncated_normal(shape=(40, 16)), name='weights2')
    biases2 = tf.Variable(tf.truncated_normal(shape=(1, 16)), name='biases2')

    xs = tf.placeholder(tf.float32, [None, 31])
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

    # 2.恢复
    saver = tf.train.Saver()
    #with tf.Session() as sess:
    sess = tf.Session()
    saver.restore(sess, "model_save/tidal-model.ckpt")
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
    l1 = restore_layer(xs, sess.run(Weights1), sess.run(biases1), activation_function=tf.nn.sigmoid)
    prediction = restore_layer(l1, sess.run(Weights2), sess.run(biases2), activation_function=tf.nn.sigmoid)
    model_param = [sess, prediction, xs]
    return model_param


def predict(model_param, input_data):
    #3. 预测
    #x_test_raw = [0.416, 0.481, 0.497, 0.493, 0.504, 0.158, 0.535, 0.527, 0.481, 0.475, 0.491, 0.493, 0.495, 0.510, 0.508, 0.525, 0.522, 0.512, 0.518, 0.522, 0.525, 0.508, 0.512, 0.456, 0.447, 0.443, 0.443, 0.462, 0.477, 0.472, 0.485]
    sess = model_param[0]
    prediction = model_param[1]
    xs = model_param[2]
    print('输入流量数据')
    print(input_data)
    x_test_raw = input_data
    x_feed_pre = np.array(x_test_raw)
    print('x_feed_pre:')
    print(x_feed_pre)
    x_feed = x_feed_pre.reshape((1,31))
    print('x_feed:')
    print(x_feed)
    out_data = sess.run(prediction, feed_dict={xs : x_feed})
    print('输出流量数据')
    print(out_data)
    return out_data
