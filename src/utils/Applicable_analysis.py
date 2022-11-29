# -*- encoding = utf-8 -*-
"""
@description: 通过JDeodorant的简化行为保存和功能有用性前提条件自动检查数据集是否满足重构条件
@date: 2022/9/20
@File : Applicable_analysis.py
@Software : PyCharm
"""
import os
import shutil
import sys

from tqdm import tqdm

dataset_path = sys.argv[1]  # 数据集路径


def judge(path, file):
    # 获取待重构方法所在代码行范围
    with open(path + '/method_range.csv', 'r') as mr:
        method_range = mr.readlines()
        method_lines = [int(x)-1 for x in method_range[0].split(',')]
    # 获取待重构方法中待提取代码行范围
    with open(path + '/del_range.csv', 'r') as dr:
        del_range = dr.readlines()
        del_lines = [int(x)-1 for x in del_range[0].split(',')]
    # Functional Usefulness Preconditions: 重构候选方法不应该包含来自原始方法的全部语句
    if del_lines[1] - del_lines[0] + 1 >= method_lines[1] - method_lines[0] - 1:
        return False
    # 读取java文件，获取每行代码
    with open(path + '/' + file, 'r') as dr:
        code_lines = dr.readlines()
    # 遍历每行代码
    splits_method = set()
    splits_extract = set()
    return_var = ''
    extract_lines = set()
    ex_extract_lines = set()
    for line in range(len(code_lines)):
        if line < method_lines[0]:
            continue
        if line > method_lines[1]:
            break
        # 保存函数中代码单词片段
        splits_line = code_lines[line].split(' ')
        for i in range(len(splits_line)):
            splits_method.add(splits_line[i])
            if splits_line[i] == 'return' and i + 1 < len(splits_line):
                return_var = splits_line[i+1]
            if del_lines[0] <= line <= del_lines[1]:
                splits_extract.add(splits_line[i])
        if del_lines[0] <= line <= del_lines[1]:
            extract_lines.add(code_lines[line])
        else:
            ex_extract_lines.add(code_lines[line])
    # Functional Usefulness Preconditions: 重构候选对象不应该包含原始方法返回的变量
    if return_var in splits_extract:
        return False
    # Behaviour Preservation Preconditions: 重构候选对象不应该包含任何break、continue或return语句，因为它们会改变方法的行为
    if 'break' in splits_extract or 'continue' in splits_extract or 'return' in splits_extract:
        return False
    # Behaviour Preservation Preconditions: 重构候选对象和重命名方法不应该包含任何影响对象状态的重复语句，因为重复语句将被执行两次
    if extract_lines.intersection(ex_extract_lines):
        return False
    return True


if __name__ == '__main__':
    files = os.listdir(dataset_path)
    # 遍历数据集
    for ff in tqdm(files):
        for f in os.listdir(dataset_path + '/' + ff):
            # 不满足重构条件的数据项要删除
            if f.split('.')[-1] == 'java' and not judge(dataset_path + '/' + ff, f):
                shutil.rmtree(dataset_path + '/' + ff)