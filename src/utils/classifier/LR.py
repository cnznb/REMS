# -*- encoding = utf-8 -*-
"""
@description: 用LR训练并对REMS数据进行测试
@date: 2022/9/26
@File : LR.py
@Software : PyCharm
"""
import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV
import pandas as pd
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
    X_test, y_test = SMOTE(random_state=42).fit_resample(X_test, y_test)
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
            'C': np.append(np.arange(0.01, 0.1, 0.01), [0.105]),
            'max_iter':range(120, 201, 20),
            'tol': [0.0001, 0.001, 0.01, 0.1]
    }
    # 生成模型
    print("Start trainging : " + "\n")
    grid = GridSearchCV(LogisticRegression(), tuned_parameters, cv=5, scoring='roc_auc', verbose=2, n_jobs=4)
    # 把数据交给模型训练
    grid.fit(X_train, y_train)
    test(model, _REMS_project_path, _training_data_path)


if __name__ == '__main__':
    X_train, y_train = load_data(_training_data_path)
    train(X_train, y_train)