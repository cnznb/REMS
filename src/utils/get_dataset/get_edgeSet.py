# -*- encoding = utf-8 -*-
"""
@description: 6.2 利用joern工具生成的dot文件生成CPG的控制流和数据流信息, 其可看作一个边集合，用于图嵌入的输入
@date: 2022/7/24
@File : get_edgeSet.py
@Software : PyCharm
"""
import re
import os
import sys
import shutil
from tqdm import tqdm

source_dir = sys.argv[1]  # 数据集路径


# 获取CPG边集
def create_Graph(s1, s2, path, idx):
    dic = dict()
    for o in s1:
        dic[int(o[0])] = int(o[1])
    g = list()
    for edge in s2:
        u = dic[int(edge[0])]
        v = dic[int(edge[1])]
        if u == v:
            continue
        g.append((u, v))
    g.sort(key=lambda x: (x[0],x[1]))
    fi = open(path + '/edgelist' + str(idx) +'.txt', 'w')
    for i in range(0, len(g)):
        if i+1 < len(g) and g[i] != g[i+1]:
            print(g[i][0], g[i][1], file=fi)
        elif i+1 == len(g):
            print(g[i][0], g[i][1], file=fi)
    fi.close()


# 提取cpg的dot文件
def work(st, path, idx):
    # 正则抽取对应关系：（编号，行号)
    s1 = re.findall("(\d+).*?<SUB>(\d+)", st)
    if len(s1) == 0:
        return
    # 正则抽取边集：（编号，编号）
    s2 = re.findall("\"(\d+)\" -> \"(\d+)\".*\n", st)
    if len(s2) == 0:
        return
    # 找到函数出口行号
    s3 = re.findall("(\d+).*?\(METHOD.*?<SUB>(\d+)", st)
    if len(s3) == 0:
        return
    # 字典存编号行号对应关系
    create_Graph(s1, s2, path, idx)


def scan(path, to):
    # 从本地读入文件
    i = 0
    files = os.listdir(path)
    for f in files:
        fs = open(path + '/' + f, 'r', encoding='UTF-8')
        s = fs.read()
        # 抽取并修改不合法行
        ss = re.findall("\"(\d+)\".*?\)>.*?\n", s)
        for x in ss:
            s = s.replace('\"'+x+'\"', 'w')
        # print(f)
        work(s, to, i)
        i = i + 1


file = os.listdir(source_dir)
for ff in tqdm(file):
    if not os.path.exists(source_dir + '/' + ff + '/CpgEdge'):
        os.mkdir(source_dir + '/' + ff + '/CpgEdge')
    for f in os.listdir(source_dir+'/'+ff):
        if f.split('.')[-1] == 'java':
            scan(source_dir + '/' + ff + '/cpg', source_dir + '/' + ff + "/CpgEdge")


