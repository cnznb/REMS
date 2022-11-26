import os

import numpy as np
import pandas as pd

from sklearn.neighbors import KNeighborsClassifier
from sklearn.datasets import load_iris
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.metrics import classification_report, precision_score, recall_score, f1_score
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from imblearn.over_sampling import SMOTE
from collections import Counter
import datetime
dir = '''
deepwalk_cg.csv
grarep_cg.csv
line_cg.csv
node2vec_cg.csv
prone_cg.csv
sdne_cg.csv
walklets_cg.csv
'''
bert_list = ['graph_cg_vec', 'plbart_cg_vec']
pdg_list = ['walklets_cg', 'deepwalk_cg', 'grarep_cg', 'line_cg', 'node2vec_cg', 'prone_cg', 'sdne_cg']
ff = 0
# for i in bert_list:
#     for j in pdg_list:
        # if j == 'prone_cg':
        #     ff = 1
        # if ff == 0:
        #     continue
# fp = open("C:/Users/winner/Desktop/KNN.txt", 'a+', newline='', encoding='utf-8')
df=pd.read_csv(r"../../Cbert/set/bert_cg_vec/grarep_cg.csv", header=None)
# print(i + " " + j, file=fp)
target=df.iloc[:,-1]
data=df.iloc[:,:-1]
X=data
y=target
fp = open("C:/Users/winner/Desktop/KNN.csv", 'a+', newline='', encoding='utf-8')
# 定义SMOTE模型，random_state相当于随机数种子的作用
smo = SMOTE(random_state=42)
X, y = smo.fit_resample(X, y)
print(Counter(y))
print(X)
print(y)
X_train = X
y_train = y
# X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)
# n_neighbors：邻居的数量。
# weights：权重计算方式。可选值为uniform与distance。
#    uniform：所有样本统一权重。
#    distance：样本权重与距离成反比。
# tuned_parameters = {
#         # "n_neighbors": range(1,11),
#         "n_neighbors": range(1,20),
#         "weights":['uniform','distance']
# }
# # 生成模型
# print("Start trainging : " + "\n")
# grid = GridSearchCV(KNeighborsClassifier(),tuned_parameters,cv=5,scoring='roc_auc', verbose=2, n_jobs=4)
# grid.fit(X_train, y_train)
# cls = grid.best_estimator_
# print(cls)
# cls.fit(X_train,y_train)
# y_pre=cls.predict(X_test)
al = dict()
nur = dict()
knn = KNeighborsClassifier(n_neighbors=3,weights='distance')
knn.fit(X_train, y_train)
for x in os.listdir('F:/sets'):
    dff = pd.read_csv("F:/sets/" + x + "/cg_vec/bert_cg_vec/grarep_cg.csv", header=None)
    X_test = dff.iloc[:, :-1]
    y_test = dff.iloc[:, -1]
    with open("F:/sets/" + x + '/method_range.txt', 'r') as ddr:
        sw = ddr.readlines()
    sw = [int(x) for x in sw[0].split(' ')]
    # X_test, y_test = smo.fit_resample(X_test, y_test)
    # print(Counter(y_test))
    aver = 0.0
    i = 0
    while i < 100:
        while 1:
            starttime = datetime.datetime.now()
            y_pre = knn.predict(X_test)
            endtime = datetime.datetime.now()
            if (endtime - starttime).microseconds != 0:
                aver += (endtime - starttime).microseconds
                break
        i += 1
        # print((endtime - starttime).microseconds)
        # print(len(y_pre))
    ll = sw[1] - sw[0] + 1
    if ll not in al:
        al[ll] = 0.0
        nur[ll] = 0
    al[ll] += aver/100
    nur[ll] += 1
    # print(y_pre)
    # print(classification_report(y_test, y_pre))

    # print('精确率：%.3f' % precision_score(y_test, y_pre,average="macro"))
    # print('召回率：%.3f' % recall_score(y_test, y_pre,average="macro"))
    # print('F1值：%.3f' % f1_score(y_test, y_pre,average="macro"))
    # print('精确率：%.3f' % precision_score(y_test, y_pre,average="micro"))
    # print('召回率：%.3f' % recall_score(y_test, y_pre,average="micro"))
    # print('F1值：%.3f' % f1_score(y_test, y_pre,average="micro"))
    print('精确率：%.3f' % precision_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
    print('召回率：%.3f' % recall_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
    print('F1值：%.3f' % f1_score(y_test, y_pre, labels=None, pos_label=1, average='binary', sample_weight=None))
for x in sorted(al):
    fp.write(str(x)+','+str(al[x]/nur[x])+'\n')
