# -*- encoding = utf-8 -*-
"""
@description: 用RNN训练并对REMS数据进行测试
@date: 2022/9/26
@File : RNN.py
@Software : PyCharm
"""
import matplotlib as mpl
import matplotlib.pyplot as plt
# %matplotlib inline
# 处理数据的库
import numpy as np
import sklearn
import pandas as pd
from pathlib import Path
import csv
from sklearn.model_selection import train_test_split
# 系统库
import os
import sys
import time
# TensorFlow的库
import tensorflow as tf
from tensorflow import keras
from keras.layers import Dense, LSTM, Bidirectional, Dropout, GRU, RNN
import keras
import keras_metrics as km
from sklearn.metrics import f1_score, precision_score, recall_score, accuracy_score
from sklearn.model_selection import GridSearchCV
from keras.wrappers.scikit_learn import KerasClassifier
from imblearn.over_sampling import SMOTE
from collections import Counter

_training_data_path = sys.argv[1]  # 训练数据集文件路径
_REMS_project_path = sys.argv[2]  # 测试数据集文件路径


def load_data(src):
    """
    加载训练数据集并划分训练集和测试集
    :param src: 训练集文件
    :return: 训练集的vec和label
    """
    df = pd.read_csv(src, header=None)
    X = []  # 向量
    y = []  # 标签
    with open(trainset) as f:
        f_csv = csv.reader(f)
        for row in f_csv:
            a = row[0:-1]
            b = []
            for x in a:
                b.append(float(x))
            X.append(b)
            if row[-1].upper() == "TRUE":
                y.append(1)
            elif row[-1].upper() == "FALSE":
                y.append(0)
    smo = SMOTE(random_state=42)
    X, y = smo.fit_resample(X, y)
    X_train = np.array(X)
    y_train = np.array(y)
    X_train = X_train.reshape(X_train.shape[0], 1, X_train.shape[1])
    return X_train, y_train


# 构建单层LSTM模型
def creat_model():
    model = keras.models.Sequential()
    model.add(GRU(64, dropout=0.2, recurrent_dropout=0.5))  # GRU是RNN模型的进化，这里直接用的keras库的模型，“GRU”可以直接替换成RNN
    model.add(Dropout(0.5))
    model.add(Dense(1, activation='sigmoid'))
    model.compile(optimizer=tf.keras.optimizers.RMSprop(), loss='binary_crossentropy', metrics=['acc',
                                                                                            km.f1_score(),
                                                                                            km.binary_precision(),
                                                                                            km.binary_recall()])
    return model

def test(estimator, filepath, trainingfile):
    """
    分别测试测试目录下的所有项目向量文件并写入对应的结果文件中
    :param estimator: 训练得到的最优模型
    :param filepath: 测试集文件路径
    :param trainingfile: 训练模型所使用的训练数据集，这里仅用于对输出结果文件命名
    :return: None
    """
    dff = pd.read_csv(filepath, header=None)
    X1 = []  # 向量
    y1 = []  # 标签
    with open(testset) as f:
        f_csv = csv.reader(f)
        for row in f_csv:
            a = row[0:-1]
            b = []
            for x in a:
                b.append(float(x))
            X1.append(b)
            if row[-1].upper() == "TRUE":
                y1.append(1)
            elif row[-1].upper() == "FALSE":
                y1.append(0)
    X_test = np.array(X1)
    y_test = np.array(y1)
    X_test = X_test.reshape(X_test.shape[0], 1, X_test.shape[1])
    output = open("REMS_Test_" + trainingfile.split("\\")[-1].split(".")[0] + ".txt", "a+")
    # 测试数据
    testing_project = filepath.split("\\")[-1].split(".")[0]
    print("Start testing : " + testing_project + "\n")
    y_pre = estimator.predict(X_test)
    precision = precision_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
    recall = recall_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
    f1 = f1_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
    output.write(
        "-----------------------------------------------------------------------------------------------\n")
    output.write("Testing data : " + testing_project + "\n")
    output.write("Testing time : " + datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S') + "\n")
    output.write("precision: {:.3}, recall: {:.3}, f1:{:.3}".format(precision, recall, f1) + "\n")
    output.write(
        "-----------------------------------------------------------------------------------------------\n\n\n")
    output.close()


def train(X_train, y_train):
    """
    运用网格搜索寻找最优参数，再对最优模型进行测试
    :param X_train:
    :param y_train:
    :return: None
    """
    batch_size = [10, 20, 30, 40]
    epochs = [20, 30, 35, 40]
    KC = KerasClassifier(build_fn=creat_model, verbose=1)
    param_grid = dict(batch_size=batch_size, epochs=epochs)
    grid = GridSearchCV(estimator=KC, param_grid=param_grid, n_jobs=4, cv=5)
    model = grid.best_estimator_
    # 把数据交给模型训练
    model.fit(X_train, y_train)
    test(model, _REMS_project_file, _training_data_path)


if __name__ == '__main__':
    X_train, y_train = load_data(_training_data_path)
    train(X_train, y_train)