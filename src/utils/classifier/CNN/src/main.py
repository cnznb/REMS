# coding:utf-8

import time
import numpy as np
import os
import sys
import platform
import csv
import tensorflow as tf
from sklearn import metrics
import keras_metrics as km
from imblearn.over_sampling import SMOTE
from collections import Counter
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.metrics import f1_score, precision_score, recall_score, accuracy_score
# o_path = os.getcwd()  # 返回当前工作目录
# sys.path.append(o_path)  # 添加自己指定的搜索路径
# sys.path.append("../")
# sys.path.append(os.path.join(str(o_path), "character_recognition"))


# import utils.sample_input as sample_input


epoch = 30
batch_size = 64
label_threshold = 0.5
bert_list = ['bert_cg_vec', 'codet5_cg_vec', 'gpt2_cg_vec',
             'gpt_cg_vec', 'graph_cg_vec', 'plbart_cg_vec', 'cotext_cg_vec']
pdg_list = ['deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg', 'walklets_cg']
# ff = 0
# for i in bert_list:
#     for j in pdg_list:
# lr = 0.001
# fp = open("C:/Users/winner/Desktop/CNN.txt", 'a+', newline='', encoding='utf-8')
# print(i + " " + j, file=fp)
# 处理数据,
model_name = "D:/save_model/gpt2_cg_vec-deepwalk_cg/"
trainset = "D:/pythonProject/Cbert/set/gpt2_cg_vec/grarep_cg.csv"  # 训练集路径
X = []  # 向量
y = []  # 标签
lens = 0
with open(trainset) as f:
    f_csv = csv.reader(f)
    for row in f_csv:
        a = row[0:-1]
        lens = len(a)
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
X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], 1)
print(X_train.shape)

# train_data, test_data, train_label, test_label = sample_input.LoadData()
# train_data = train_data.reshape(train_data.shape[0], train_data.shape[1], 1)
# test_data = test_data.reshape(test_data.shape[0], test_data.shape[1], 1)

# print("*" * 150)
# print("Input data shape:")
# print("train data:", train_data.shape, train_label.shape)
# print("test data:", test_data.shape, test_label.shape)
# print("*" * 150)

model = tf.keras.models.Sequential(
    [
        tf.keras.layers.Conv1D(16, 4, activation="relu", input_shape=(lens, 1)),
        tf.keras.layers.MaxPooling1D(3, 3),
        tf.keras.layers.Conv1D(32, 4, activation="relu"),
        tf.keras.layers.MaxPooling1D(3, 3),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(128, activation=tf.nn.relu),
        tf.keras.layers.Dense(64, activation=tf.nn.relu),
        tf.keras.layers.Dense(1, activation=tf.nn.sigmoid),
    ]
)

# model.compile(optimizer="adam", loss="binary_crossentropy", metrics=["accuracy"])
# # # model fitting
# history = model.fit(X_train, y_train, epochs=epoch, batch_size=batch_size)
# #
# # # save model
# print("Saving model to disk \n")
# model.save(model_name)
#
load_model = tf.keras.models.load_model(model_name)

# 评估模型
# start_time = time.time()
# loss, accu = model.evaluate(X_test, y_test, verbose=0)
# # print("*" * 100)
# print("loss:", loss, "accu:", accu)
testset = "D:/pythonProject/Cbert/datas/gpt2_cg_vec/grarep_cg.csv"          #测试集路径
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
X_test = X_test.reshape(X_test.shape[0], X_test.shape[1], 1)
float_arr = X_test.astype(np.float)
raw_scores = load_model.predict(float_arr)
pred = np.where(raw_scores > label_threshold, 1, 0)
# end_time = time.time()
# use_time = end_time - start_time
pred = pred.reshape(
    pred.shape[0],
)
print('精确率：%.3f' % precision_score(y_test, pred, labels=None, pos_label=1, average='binary', sample_weight=None))
print('召回率：%.3f' % recall_score(y_test, pred, labels=None, pos_label=1, average='binary', sample_weight=None) )
print('F1值：%.3f' % f1_score(y_test, pred, labels=None, pos_label=1, average='binary', sample_weight=None))



if __name__ == "__main__":
    # main()
    pass
