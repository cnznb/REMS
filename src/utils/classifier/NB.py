# -*- encoding = utf-8 -*-
"""
@description: 用NB训练并对REMS数据进行测试
@date: 2022/9/27
@File : NB.py
@Software : PyCharm
"""
import numpy as np
import os
from sklearn.datasets import load_iris
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from sklearn.metrics import classification_report, precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.naive_bayes import GaussianNB, MultinomialNB, BernoulliNB
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from imblearn.over_sampling import SMOTE
from collections import Counter
import pandas as pd
import datetime

_training_data_path = '312'  # 训练数据集文件路径
training_model_path = '' #
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
    # 1.预处理用户的class文件
    dff = pd.read_csv(filepath, header=None)
    X_test = dff.iloc[:, :-1]
    y_test = dff.iloc[:, -1]
    X_test, y_test = SMOTE(random_state=42).fit_resample(X_test, y_test)
    output = open("REMS_Test_" + trainingfile.split("\\")[-1].split(".")[0] + ".txt", "a+")
    # 测试数据
    testing_project = filepath.split("\\")[-1].split(".")[0]
    print("Start testing : " + testing_project + "\n")
    y_pre = estimator.predict(X_test)
    # 0 1 1 0 0 0 0 0 0 0 this method
    # 请加载模型：
    # 请输入您的class文件路径：
    # ....
    # 请输入您需要进行extract method重构预测的方法名：
    # ...
    # 正在进行预处理..
    # 推荐重构class文件哪些行
    # precision = precision_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
    # recall = recall_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
    # f1 = f1_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None)
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
    # tuned_parameters = {
    #     "alpha": np.arange(0.1, 2, 0.1)
    # }
    # # 生成模型
    # print("Start trainging : " + "\n")
    # grid = GridSearchCV(BernoulliNB(alpha=0.4), tuned_parameters, cv=5, scoring='roc_auc', verbose=2, n_jobs=4)
    model = BernoulliNB(alpha=0.4)
    # 把数据交给模型训练
    model.fit(X_train, y_train)
    # 处理测试class文件，用graphcodebert预训练模型生成向量
    test(model, _REMS_project_path, _training_data_path)


if __name__ == '__main__':
    # 1.加载预训练模型：用户输入的是模型本地路径(你去训练模型，保存起来，怎么写问chatgpt)
    # 2.加载class文件：让用户输入class文件路径(把代码文本存在数组里)
    # 3.指定method: 用户输入mehtod名(你用javalang包能够找到每个方法的名字和起始行，然后你用括号匹配找到方法结束行)
    # 4.用graphcodebert生成这个指定方法每行的向量
    # 5.把这些向量作为输入，用预训练模型去测试，得到01数组，1代表这行需要重构提取出去，然后你需要润色一下结果，展示给用户
    # javalang  start_line  括号匹配找到 end_line


    # X_train, y_train = load_data(_training_data_path)
    # train(X_train, y_train)