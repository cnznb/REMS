# -*- coding = utf-8 -*-
# @Time : 2022/7/8 15:43
# @Author : hjp
# @File : GBDT.py
# @Software : PyCharm
# -*- coding = utf-8 -*-
import numpy as np
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn.decomposition import PCA
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.metrics import classification_report, precision_score, recall_score, f1_score
from sklearn.model_selection import train_test_split, GridSearchCV

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
df=pd.read_csv(r"../../Cbert/set/plbart_cg_vec/walklets_cg.csv", header=None)
target=df.iloc[:,-1]
data=df.iloc[:,:-1]
X=data
y=target
print(X)
print(y)

# pca = PCA(n_components=100)
# pca.fit(X)
# X=pca.transform(X)
# print(X)
# lda = LDA(n_components=18)
# lda.fit(X,y)
# X=lda.transform(X)
# print(X)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=0)
# min=min(len(X_train),len(X_test))
# pca = PCA(n_components=min)
# X_train=pca.fit_transform(X_train,y_train)
# X_test=pca.fit_transform(X_test,y_test)

print(X)
tuned_parameters = {
        #'n_estimators':[150],
        #'learning_rate': [0.1],
        'learning_rate': np.linspace(0.1,1,3)
        # 'max_depth':range(14,20,5),
        # 'min_samples_split':range(1300,1900,500),
        # 'min_samples_leaf':range(60,101,10),
        # 'max_features':range(7,20,2),
        # 'subsample':[0.6,0.7,0.75,0.8,0.85,0.9]
        # 'subsample':[0.8]
}
    # 生成模型
print("Start trainging : " + "\n")
grid = GridSearchCV(GradientBoostingClassifier(),tuned_parameters,cv=5,scoring='roc_auc', verbose=2, n_jobs=4)
grid.fit(X_train, y_train)
cls = grid.best_estimator_
print(cls)
cls.fit(X_train,y_train)
y_pre=cls.predict(X_test)

# gb = GradientBoostingClassifier(n_estimators=160,learning_rate=0.45,subsample=0.8)
# gb.fit(X_train, y_train)
# y_pre = gb.predict(X_test)

print(y_pre)
#print(classification_report(y_test, y_pre))

# print('精确率：%.3f' % precision_score(y_test, y_pre,average="macro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="macro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="macro"))
# print('精确率：%.3f' % precision_score(y_test, y_pre,average="micro"))
# print('召回率：%.3f' % recall_score(y_test, y_pre,average="micro"))
# print('F1值：%.3f' % f1_score(y_test, y_pre,average="micro"))
print('精确率：%.3f' % precision_score(y_test, y_pre,average="weighted"))
print('召回率：%.3f' % recall_score(y_test, y_pre,average="weighted"))
print('F1值：%.3f' % f1_score(y_test, y_pre,average="weighted"))