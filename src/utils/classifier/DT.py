# -*- encoding = utf-8 -*-
"""
@description: 用DT训练并对REMS数据进行测试
@date: 2022/9/26
@File : DT.py
@Software : PyCharm
"""
import os
import sys
import numpy as np
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn.metrics import classification_report, precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
import pandas as pd
from imblearn.over_sampling import SMOTE
from collections import Counter
import datetime

_training_data_path = sys.argv[1]  # 训练数据集文件路径
_REMS_project_path = sys.argv[2]  # 测试数据集文件路径

def load_data(src):
    """
    加载训练数据集并划分训练集和测试集
    :param src: 训练集文件
    :return: 训练集的vec和label
    """
    df = pd.read_csv(src, header=None)
    X = df.iloc[:, :-1]
    y = df.iloc[:, -1]
    # 定义SMOTE模型，random_state相当于随机数种子的作用
    smo = SMOTE(random_state=42)
    X_train, y_train = smo.fit_resample(X, y)
    return X_train, y_train


def test(estimator, filepath, trainingfile):
    """
    分别测试测试目录下的所有项目向量文件并写入对应的结果文件中
    :param estimator: 训练得到的最优模型
    :param filepath: 测试集文件路径
    :param trainingfile: 训练模型所使用的训练数据集，这里仅用于对输出结果文件命名
    :return: None
    """
    dff = pd.read_csv(filepath, header=None)
    X_test = dff.iloc[:, :-1]
    y_test = dff.iloc[:, -1]
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
    # 网格搜索参数列表
    tuned_parameters = {
            'splitter': ('best', 'random'),
            'criterion': ("gini", "entropy"),
            "max_depth": [*range(20, 51, 10)],
            'min_samples_leaf': [*range(1, 15, 2)]
    }
    # 生成模型
    print("Start trainging : " + _training_data_file + "\n")
    grid = GridSearchCV(tree.DecisionTreeClassifier(), tuned_parameters, cv=5, scoring='roc_auc', verbose=2, n_jobs=4)
    model = grid.best_estimator_
    # 把数据交给模型训练
    model.fit(X_train, y_train)
    test(model, _REMS_project_file, _training_data_path)


if __name__ == '__main__':
    X_train, y_train = load_data(_training_data_path)
    train(X_train, y_train)