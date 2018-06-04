"""

"""
import tensorflow as tf
import numpy as np
from tensorflow.python.framework import graph_util


# x = np.array([[1.0, 1.3, 1.3, 1.2, 0.2, 0.1, 1.0, 3.0, 1.5],
#               [1.0, 1.3, 1.3, 1.2, 0.2, 0.1, 1.0, 3.0, 0.5],
              # [1.0, 1.3, 1.3, 1.2, 0.2, 0.1, 1.0, 3.0, 2.5]], dtype=np.float32)
# y = np.array([[0.3, 0.2, 0.5, 0.1, 0.2],[0.3, 0.2, 0.5, 0.1, 0.1],[0.3, 0.2, 0.5, 0.1, 0.15]
#               ], dtype=np.float32)

learning_rate = 0.01

state_number = 8
action_number = 6

inputs = tf.placeholder(tf.float32, shape=[None, state_number], name='x')
expected_output = tf.placeholder(tf.float32, shape=[None, action_number], name='y')

input_layer = tf.layers.dense(inputs=inputs, units=state_number, activation=tf.nn.relu)
layer1 = tf.layers.dense(inputs=input_layer, units=30, activation=tf.nn.relu)
layer2 = tf.layers.dense(inputs=layer1, units=50, activation=tf.nn.relu)
output_layer = tf.layers.dense(inputs=layer2, units=action_number, activation=None)

output = tf.identity(output_layer, name='result')

loss = tf.losses.mean_squared_error(predictions=output_layer, labels=expected_output)
train_op = tf.train.AdamOptimizer(learning_rate).minimize(loss, name='train')

sess = tf.Session()
sess.run(tf.global_variables_initializer())
saver = tf.train.Saver()
saver_def = saver.as_saver_def()
# saver.save(sess, './model.sd')
tf.train.write_graph(sess.graph_def, '.', 'model.proto', as_text=False)


# with tf.Session() as sess:
    # for i in range(1,100):
    #     _, result = sess.run([train_op, loss], feed_dict={inputs: x, expected_output: y})
    #     print(result)
    # print(sess.run([output_layer], feed_dict={'x:0': np.array(x[0]).reshape((1,9))}))
    # print(sess.run([output_layer], feed_dict={'x:0': np.array(x[1]).reshape((1,9))}))
    # print(sess.run([output_layer], feed_dict={'x:0': np.array(x[2]).reshape((1,9))}))

