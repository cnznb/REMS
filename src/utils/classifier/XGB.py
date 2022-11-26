# -*- coding = utf-8 -*-
# @Time : 2022/7/8 15:45
# @Author : hjp
# @File : XGB.py
# @Software : PyCharm
# -*- coding = utf-8 -*-
import numpy as np
from sklearn.datasets import load_iris
from sklearn.decomposition import PCA
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from xgboost import XGBClassifier
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
bert_list = ['plbart_cg_vec']
pdg_list = ['walklets_cg', 'deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg']
# ff = 0
# for i in bert_list:
#     for j in pdg_list:
        # if j == 'line_cg':
        #     ff = 1
        # if ff == 0:
        #     continue
# fp = open("C:/Users/winner/Desktop/XGB.txt", 'a+', newline='', encoding='utf-8')
df=pd.read_csv(r"../../Cbert/data/graph_cg_vec/node2vec_cg.csv", header=None)
# target=df.iloc[70:90,-1]
# data=df.iloc[70:90,:-1]
target=df.iloc[:,-1]
data=df.iloc[:,:-1]
X=data
y=target
print(Counter(y))
# 定义SMOTE模型，random_state相当于随机数种子的作用
smo = SMOTE(random_state=42)
X, y = smo.fit_resample(X, y)
print(Counter(y))
print(X)
print(y)
X_train = X
y_train = y
# pca = PCA(n_components=100)
# pca.fit(X)
# X=pca.transform(X)
# print(X)


# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)
# min=min(len(X_train),len(X_test))
# pca = PCA(n_components=64)
# X_train=pca.fit_transform(X_train,y_train)
# m=min(len(set(y_train))-1,len(X_train[0]),len(set(y_test))-1,len(X_test[0]))
# lda = LDA(n_components=8)
# X_train=lda.fit_transform(X_train,y_train)
# X_test=lda.fit_transform(X_test,y_test)
#
# pca = PCA(n_components=8)
# X_train=pca.fit_transform(X_train)
# X_test=pca.fit_transform(X_test)


# tuned_parameters = {
#         'n_estimators': range(200,300,50),
#         # 'max_depth':range(2,15,1),
#         'learning_rate': [0.1],             # np.linspace(0.1,2,5),
#         # 'subsample':np.linspace(0.7,0.9,20),
#         # 'colsample_bytree':np.linspace(0.5,0.98,10),
#         # 'min_child_weight':range(1,9,1)
# }
#     # 生成模型
# print("Start trainging : " + "\n")
# grid = GridSearchCV(XGBClassifier(eval_metric=['logloss','auc','error']),tuned_parameters,cv=5,scoring='roc_auc',
#                     verbose=2, n_jobs=3)
# grid.fit(X_train, y_train)
# cls = grid.best_estimator_
# print(cls, file=fp)
# cls.fit(X_train,y_train)
# y_pre=cls.predict(X_test)

xgb = XGBClassifier(n_estimators=250)
xgb.fit(X_train, y_train)
dff=pd.read_csv(r"../../Cbert/datas/graph_cg_vec/node2vec_cg.csv", header=None)
X_test = dff.iloc[:,:-1]
y_test = dff.iloc[:,-1]
X_test, y_test = smo.fit_resample(X_test, y_test)
print(Counter(y_test))
y_pre=xgb.predict(X_test)

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
# print('精确率：%.3f' % precision_score(y_test, y_pre,average="samples"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="samples"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="samples"))
