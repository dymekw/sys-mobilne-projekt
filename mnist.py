from __future__ import print_function
import shutil
import os.path
import tensorflow as tf
import numpy as np
from tensorflow.examples.tutorials.mnist import input_data
from random import shuffle

EXPORT_DIR = './model'

if os.path.exists(EXPORT_DIR):
    shutil.rmtree(EXPORT_DIR)

#mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)

# Parameters
learning_rate = 0.0001
training_iters = 1000000
batch_size = 128
display_step = 10

# Network Parameters
n_input = 6
n_classes = 6
dropout = 0.75  # Dropout, probability to keep units

# tf Graph input
x = tf.placeholder(tf.float32, [None, n_input])
y = tf.placeholder(tf.float32, [None, n_classes])
keep_prob = tf.placeholder(tf.float32)  # dropout (keep probability)


# Create some wrappers for simplicity
def conv2d(x, W, b, strides=1):
    # Conv2D wrapper, with bias and relu activation
    x = tf.nn.conv2d(x, W, strides=[1, strides, strides, 1], padding='SAME')
    x = tf.nn.bias_add(x, b)
    return tf.nn.relu(x)


def maxpool2d(x, k=2):
    # MaxPool2D wrapper
    return tf.nn.max_pool(x, ksize=[1, k, k, 1], strides=[1, k, k, 1],
                          padding='SAME')
  
def get_iris_data():
    CANCER_TRAINING = "./data2.csv"

    f = open(CANCER_TRAINING)
    f.readline()  # skip the header
    data = np.loadtxt(f)
    
    shuffle(data)
    
    target = data[:,-1].astype(int)
    data = data[:,0:-1]


    # Convert into one-hot vectors
    num_labels = len(np.unique(target))
    #print(num_labels)
    #print(data.shape)
    all_Y = np.eye(num_labels)[target]  # One liner trick!
    return data, all_Y


# Create Model
def conv_net(x, weights, biases, dropout):
    # Reshape input picture
    x = tf.reshape(x, shape=[-1, 2, 3, 1])

    # Convolution Layer
    conv1 = conv2d(x, weights['wc1'], biases['bc1'])
    # Max Pooling (down-sampling)
    conv1 = maxpool2d(conv1, k=2)

    # Convolution Layer
    conv2 = conv2d(conv1, weights['wc2'], biases['bc2'])
    # Max Pooling (down-sampling)
    conv2 = maxpool2d(conv2, k=2)
    #print("conv2")
    #print(conv2.shape)
    # Fully connected layer
    # Reshape conv2 output to fit fully connected layer input
    fc1 = tf.reshape(conv2, [-1, weights['wd1'].get_shape().as_list()[0]])
    fc1 = tf.add(tf.matmul(fc1, weights['wd1']), biases['bd1'])
    fc1 = tf.nn.relu(fc1)
    # Apply Dropout
    fc1 = tf.nn.dropout(fc1, dropout)

    # Output, class prediction
    out = tf.add(tf.matmul(fc1, weights['out']), biases['out'])
    return out

# Store layers weight & bias
weights = {
    # 5x5 conv, 1 input, 32 outputs
    'wc1': tf.Variable(tf.random_normal([2, 2, 1, 16])),
    # 5x5 conv, 32 inputs, 64 outputs
    'wc2': tf.Variable(tf.random_normal([2, 2, 16, 32])),
    # fully connected, 7*7*64 inputs, 1024 outputs
    'wd1': tf.Variable(tf.random_normal([32, 64])),
    # 1024 inputs, 10 outputs (class prediction)
    'out': tf.Variable(tf.random_normal([64, n_classes]))
}

biases = {
    'bc1': tf.Variable(tf.random_normal([16])),
    'bc2': tf.Variable(tf.random_normal([32])),
    'bd1': tf.Variable(tf.random_normal([64])),
    'out': tf.Variable(tf.random_normal([n_classes]))
}

# Construct model
pred = conv_net(x, weights, biases, keep_prob)

# Define loss and optimizer
#print(pred.shape)
#print(y.shape)
cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=pred, labels=y))
optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(cost)

# Evaluate model
correct_pred = tf.equal(tf.argmax(pred, 1), tf.argmax(y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))

# Initializing the variables
init = tf.initialize_all_variables()


def get_minibatch(array, step, batch_size):
  begin = ((step-1)*batch_size) % array.shape[0]
  return array[begin : begin+batch_size]
  
train_x, train_y = get_iris_data()
print(train_x.shape[0])
# Launch the graph
with tf.Session() as sess:
    sess.run(init)
    step = 1
    # Keep training until reach max iterations
    while step * batch_size < training_iters:
        #batch_x, batch_y = mnist.train.next_batch(batch_size)
        batch_x = get_minibatch(train_x, step, batch_size)
        #print(batch_x.shape)
        batch_y = get_minibatch(train_y, step, batch_size)
        #print(batch_x.shape)
        # Run optimization op (backprop)
        sess.run(optimizer, feed_dict={x: batch_x, y: batch_y,
                                       keep_prob: dropout})
        if step % display_step == 0:
            # Calculate batch loss and accuracy
            loss, acc = sess.run([cost, accuracy], feed_dict={x: batch_x,
                                                              y: batch_y,
                                                              keep_prob: 1.})
            print("Iter " + str(step * batch_size) + ", Minibatch Loss= " + \
                  "{:.6f}".format(loss) + ", Training Accuracy= " + \
                  "{:.5f}".format(acc))
        step += 1

        if (step * batch_size) % train_x.shape[0] == 0:
           print('reload data')
           train_x, train_y = get_iris_data()
    print("Optimization Finished!")

    # Calculate accuracy for 256 mnist test images
    #print("Testing Accuracy:", \
    #      sess.run(accuracy, feed_dict={x: mnist.test.images[:256],
    #                                    y: mnist.test.labels[:256],
    #                                    keep_prob: 1.}))
    WC1 = weights['wc1'].eval(sess)
    BC1 = biases['bc1'].eval(sess)
    WC2 = weights['wc2'].eval(sess)
    BC2 = biases['bc2'].eval(sess)
    WD1 = weights['wd1'].eval(sess)
    BD1 = biases['bd1'].eval(sess)
    W_OUT = weights['out'].eval(sess)
    B_OUT = biases['out'].eval(sess)

# Create new graph for exporting
g = tf.Graph()
with g.as_default():
    x_2 = tf.placeholder("float", shape=[None, 784], name="input")

    WC1 = tf.constant(WC1, name="WC1")
    BC1 = tf.constant(BC1, name="BC1")
    x_image = tf.reshape(x_2, [-1, 2, 4, 1])
    CONV1 = conv2d(x_image, WC1, BC1)
    MAXPOOL1 = maxpool2d(CONV1, k=2)

    WC2 = tf.constant(WC2, name="WC2")
    BC2 = tf.constant(BC2, name="BC2")
    CONV2 = conv2d(MAXPOOL1, WC2, BC2)
    MAXPOOL2 = maxpool2d(CONV2, k=2)

    WD1 = tf.constant(WD1, name="WD1")
    BD1 = tf.constant(BD1, name="BD1")

    FC1 = tf.reshape(MAXPOOL2, [-1, WD1.get_shape().as_list()[0]])
    FC1 = tf.add(tf.matmul(FC1, WD1), BD1)
    FC1 = tf.nn.relu(FC1)

    W_OUT = tf.constant(W_OUT, name="W_OUT")
    B_OUT = tf.constant(B_OUT, name="B_OUT")

    # skipped dropout for exported graph as there is no need for already calculated weights

    OUTPUT = tf.nn.softmax(tf.matmul(FC1, W_OUT) + B_OUT, name="output")

    sess = tf.Session()
    init = tf.initialize_all_variables()
    sess.run(init)

    graph_def = g.as_graph_def()
    tf.train.write_graph(graph_def, EXPORT_DIR, 'mnist_model_graph.pb', as_text=False)

    #Test trained model
    #y_train = tf.placeholder("float", [None, n_classes])
    #correct_prediction = tf.equal(tf.argmax(OUTPUT, 1), tf.argmax(y_train, 1))
    #accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))

    #print("check accuracy %g" % accuracy.eval(
    #        {x_2: train_x, y_train: train_y}, sess))
