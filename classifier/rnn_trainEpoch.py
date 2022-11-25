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

"""
找到模型最优训练参数
"""
bert_list = [
             'gpt_cg_vec', 'graph_cg_vec', 'plbart_cg_vec', 'cotext_cg_vec']
pdg_list = ['deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg', 'walklets_cg']
ff = 0
# for i in bert_list:
#     for j in pdg_list:
# fp = open("D:/RNN.txt", 'a+', newline='', encoding='utf-8')
# print(i + " " + j, file=fp)
# 处理数据,
trainset = r"../../Cbert/set/bert_cg_vec/grarep_cg.csv"  # 训练集路径
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
print(Counter(y))
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)
X_train = np.array(X)
y_train = np.array(y)
print(X_train)
print(y_train)
print(X_train.shape)
X_train = X_train.reshape(X_train.shape[0], 1, X_train.shape[1])
print(X_train.shape)


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
    # model.compile(optimizer=tf.keras.optimizers.RMSprop())
    return model


model = KerasClassifier(build_fn=creat_model, verbose=1)

batch_size = [40]
epochs = [35, 40]
# batch_size = [40]
# epochs = [30]
param_grid = dict(batch_size=batch_size, epochs=epochs)
grid = GridSearchCV(estimator=model, param_grid=param_grid, n_jobs=4, cv=5)
grid.fit(X_train, y_train)
cls = grid.best_estimator_
print(grid.best_params_)
cls.fit(X_train,y_train)


testset = r"../../Cbert/datas/bert_cg_vec/grarep_cg.csv"          #测试集路径
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
X1, y1 = smo.fit_resample(X1, y1)
X_test = np.array(X1)
y_test = np.array(y1)
X_test = X_test.reshape(X_test.shape[0], 1, X_test.shape[1])
y_pre = cls.predict(X_test)
print('精确率：%.3f' % precision_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
print('召回率：%.3f' % recall_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
print('F1值：%.3f' % f1_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
# print("Best: %f using %s" % (grid_result.best_score_, grid_result.best_params_))
# means = grid_result.cv_results_['mean_test_score']
# stds = grid_result.cv_results_['std_test_score']
# params = grid_result.cv_results_['params']
# for mean, stdev, param in zip(means, stds, params):
#     print("%f (%f) with: %r" % (mean, stdev, param))
#
# history = grid_result

"""
archstudio_Init_train:
Best: 0.652360 using {'batch_size': 60, 'epochs': 30}
"""
