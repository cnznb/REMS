#!/usr/bin/python
import sys
import argparse
import os
import time
import csv
import numpy as np
from sklearn.feature_selection import chi2
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.feature_selection import SelectKBest
from sklearn.svm import SVC
from sklearn.naive_bayes import GaussianNB
from sklearn import tree
from sklearn import linear_model
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix, classification_report, accuracy_score, precision_score, recall_score, f1_score
from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import KFold
from sklearn.svm import OneClassSVM
from operator import itemgetter
from itertools import groupby
from sklearn.linear_model import RandomizedLogisticRegression
from sklearn.feature_selection import SelectPercentile, f_classif
from sklearn.feature_selection import SelectFromModel
from sklearn.ensemble import VotingClassifier
from sklearn import ensemble
from sklearn.ensemble.forest import RandomForestRegressor
from sklearn.linear_model.ridge import Ridge
from sklearn.linear_model import LinearRegression
from sklearn.svm.classes import SVR
import pickle

index_selected = range(0, 48)
index_range = np.array([1, 49], dtype=np.int32)

class RftClassifier(object):
    '''classifier for refactoring project'''

    def __init__(self, pname):
        self.classifier = 'GB'
        self.clf = None
        self.pname = pname
        self.normalizer = None

    def _union_shuffled_copies(self, a, b):
        '''shuffle two arrays together'''
        assert len(a) == len(b)
        p = np.random.permutation(len(a))
        return a[p], b[p]

    def _load(self, path, dtype='float', delimiter=',', skiprows=1, usecols=index_range):
        '''load data from a certain file'''
        data = np.loadtxt(open(path, "rb"), dtype=dtype, delimiter=delimiter,
                          skiprows=skiprows, usecols=range(*usecols))
        where_are_NaNs = np.isnan(data)
        data[where_are_NaNs] = 0
        print 'data shape', data.shape
        return data

    def get_pred_id(self):
        path_pred = self.pname + '_candidates.csv'
        with open(path_pred, 'rb') as fpred:
            pred_ids = []
            i = 0
            for line in fpred:
                if 'TRUE' in line or 'true' in line:
                    pred_ids.append(i - 1)
                i += 1
        return pred_ids

    def _write_prob(self, res_list):
        dir_results = self.pname + '_results'
        print 'write results in %s' % dir_results
        with open(self.pname+'_prob.csv', 'w') as out:
            writer = csv.writer(out)
            for res in res_list:
                for i in range(res[0].shape[0]):
                    lin = [str(item) for item in res[0][i]]
                    lin.append('%.16f' % res[1][i])
                    writer.writerow(lin[1:])

    def predict(self):
        path_fea = self.pname + '_features.csv'
	# load model from file
	self.clf = pickle.load(open("gems.pickle.dat", "rb"))
        print 'data loading...'
        candidates = self._load(path_fea, usecols=index_range + 3)
	self.normalizer = np.loadtxt('normailize.out', delimiter=',')
        print self.normalizer.shape
	candidates = candidates / self.normalizer
        candidates = candidates[:, index_selected]
        group = self._load(path_fea, dtype='int32', usecols=(0, 3))
        pred_ids = self.get_pred_id()
        # print "true or false: ", pred_ids
        print "predicting..."
        n_sample = group[:, 0]
        min = n_sample.min()
        max = n_sample.max()
        res_list = []
        for num in range(min, max + 1):
            index, = np.where(n_sample == num)
            if index.shape[0] != 0:
                true_index = [val for val in pred_ids if val in index]
                if len(true_index) != 0:
                    n_candidates = candidates[true_index]
                    predict_candidates = self.clf.predict_proba(n_candidates)
                    p = predict_candidates[:, 1]
                    orders = np.argsort(-p).tolist()
                    candidates_ordered = np.array(true_index)[orders]
                    res_list.append((group[candidates_ordered], p[orders]))
        self._write_prob(res_list)
        print 'prob results saved successfully!'

    def _print(self, pred):
        '''print results of classifier'''
        print confusion_matrix(self.test_label, pred)
        print classification_report(self.test_label, pred)


def refactoring(args):
    '''invoke the above class'''
    rft_clf = RftClassifier(args.pname)
    # train and predict
    rft_clf.predict()

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='load data and normalize')
    parser.add_argument('-pname', default='test', help='name of the data set to predict')
    ARGS = parser.parse_args()
    refactoring(ARGS)
