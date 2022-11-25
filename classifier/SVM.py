# -*- coding = utf-8 -*-
# @Time : 2022/7/11 21:24
# @Author : hjp
# @File : SVM.py
# @Software : PyCharm
import numpy as np
from sklearn.metrics import precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.svm import SVC
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
bert_list = ['cotext_cg_vec', 'gpt2_cg_vec',
             'gpt_cg_vec', 'graph_cg_vec', 'plbart_cg_vec']
pdg_list = ['walklets_cg', 'deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg']
# ff = 0
# for i in bert_list:
#     for j in pdg_list:
#         if j == 'line_cg':
#             ff = 1
#         if ff == 0:
#             continue
# fp = open("C:/Users/winner/Desktop/SVM.txt", 'a+', newline='', encoding='utf-8')
df=pd.read_csv(r"../../Cbert/set/gpt2_cg_vec/grarep_cg.csv", header=None)
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

# tuned_parameters = [{'kernel': ['rbf'], 'gamma': [1e-3],'C': [100, 1000]}
#                     # {'kernel': ['linear'], 'C': [1,10]}
#                     ]
# grid = GridSearchCV(SVC(),tuned_parameters,cv=5,scoring='roc_auc', verbose=2, n_jobs=4)
# grid.fit(X_train, y_train)
# cls = grid.best_estimator_
# print(cls, file=fp)
# cls.fit(X_train,y_train)
# y_pre=cls.predict(X_test)

svc=SVC(kernel='rbf', gamma=1e-3, C=1000)
svc.fit(X_train,y_train)
dff=pd.read_csv(r"../../Cbert/datas/gpt2_cg_vec/grarep_cg.csv", header=None)
X_test = dff.iloc[:,:-1]
y_test = dff.iloc[:,-1]
X_test, y_test = smo.fit_resample(X_test, y_test)
print(Counter(y_test))
y_pre=svc.predict(X_test)

# print(y_pre)

# print('精确率：%.3f' % precision_score(y_test, y_pre,average="macro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="macro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="macro"))
# print('精确率：%.3f' % precision_score(y_test, y_pre,average="micro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="micro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="micro"))
print('精确率：%.3f' % precision_score(y_test, y_pre,labels=None,pos_label=1,average='binary',sample_weight=None))
print('召回率：%.3f' % recall_score(y_test, y_pre,labels=None,pos_label=1,average='binary',sample_weight=None))
print('F1值：%.3f' % f1_score(y_test, y_pre,labels=None,pos_label=1,average='binary',sample_weight=None))