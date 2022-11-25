# -*- coding = utf-8 -*-
# @Time : 2022/7/8 15:42
# @Author : hjp
# @File : RF.py
# @Software : PyCharm
# -*- coding = utf-8 -*-
import numpy as np
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn.ensemble import RandomForestClassifier
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from sklearn.metrics import classification_report, precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV
from imblearn.over_sampling import SMOTE
from collections import Counter

import pandas as pd
dir = '''
deepwalk_cg.csv
grarep_cg.csv
line_cg.csv
node2vec_cg.csv
prone_cg.csv
sdne_cg.csv
walklets_cg.csv
'''
bert_list = [
             'gpt_cg_vec', 'graph_cg_vec', 'plbart_cg_vec']
pdg_list = ['walklets_cg', 'deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg']
# ff = 0
# for i in bert_list:
#     for j in pdg_list:
        # if j == 'line_cg':
        #     ff = 1
        # if ff == 0:
        #     continue
# fp = open("C:/Users/winner/Desktop/RF.txt", 'a+', newline='', encoding='utf-8')
df=pd.read_csv(r"../../Cbert/data/bert_cg_vec/node2vec_cg.csv", header=None)
# print(i + " " + j, file=fp)
target=df.iloc[:,-1]
data=df.iloc[:,:-1]
X=data
y=target
print(Counter(y))
# 定义SMOTE模型，random_state相当于随机数种子的作用
smo = SMOTE(random_state=42)
X, y = smo.fit_resample(X, y)
print(Counter(y))
X_train = X
y_train = y
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)
#
# tuned_parameters = [
#     {'n_estimators': [80, 90, 100, 50, 60, 70, 40, 20, 30, 65], 'max_features': [8, 12, 10, 15, 17, 5]},
#     {'bootstrap': [False], 'n_estimators': [3, 7, 10], 'max_features': [2, 3, 4]},
# ]
# # # # 生成模型
# # # print("Start trainging : " + "\n")
# rf = GridSearchCV(RandomForestClassifier(),tuned_parameters,cv=5,scoring='roc_auc', verbose=2, n_jobs=4)
# rf.fit(X_train, y_train)
# cls = rf.best_estimator_
# print(cls)
# cls.fit(X_train,y_train)
# y_pre=cls.predict(X_test)

rf = RandomForestClassifier(max_features=14)
rf.fit(X_train, y_train)
dff = pd.read_csv(r"../../Cbert/datas/bert_cg_vec/node2vec_cg.csv", header=None)
X_test = dff.iloc[:,:-1]
y_test = dff.iloc[:,-1]
X_test, y_test = smo.fit_resample(X_test, y_test)
print(Counter(y_test))
y_pre = rf.predict(X_test)

# print(y_pre)
#print(classification_report(y_test, y_pre))

# print('精确率：%.3f' % precision_score(y_test, y_pre,average="macro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="macro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="macro"))
# print('精确率：%.3f' % precision_score(y_test, y_pre,average="micro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="micro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="micro"))
print('精确率：%.3f' % precision_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
print('召回率：%.3f' % recall_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
print('F1值：%.3f' % f1_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
print('*' * 10)