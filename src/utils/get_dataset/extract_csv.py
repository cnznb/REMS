# -*- encoding = utf-8 -*-
"""
@description: 5.提取出json文件中每一项被重构的函数的起止行和内部被重构的行信息，分别存于method_line.csv、del_line.csv
@date: 2022/7/30
@File : extract_csv.py
@Software : PyCharm
"""
import re
import os
import sys
import json
import csv
import shutil
from tqdm import tqdm

source_path = sys.argv[1]  # 待处理原数据集路径
target_path = sys.argv[2]  # 规格化数据集设定路径

def work():
    files = os.listdir(source_path)
    idx = 100000
    for f in tqdm(files):
        f_list = os.listdir(source_path + f)
        flag = 0
        for f_l in f_list:
            if f_l.endswith('old.java'):
                flag = 1
                break
        fc = open(source_path + f + '/' + f + '.json', 'r', encoding='UTF-8')
        c = fc.read()
        cc = c.replace('}{', '}<SPLIT>{')
        split_data = cc.split('<SPLIT>')
        fc.close()
        parsed_data = [json.loads(bit_data) for bit_data in split_data]
        for i in range(len(parsed_data)):
            if not os.path.exists(target_path + str(idx)):
                os.mkdir(target_path + str(idx))
            start = parsed_data[i]["leftSideLocations"][0]["startLine"]
            end = parsed_data[i]["leftSideLocations"][0]["endLine"]
            if flag == 1:
                file_name = parsed_data[i]["leftSideLocations"][0]["codeElement"]
                del_s = parsed_data[i]["leftSideLocations"][0]["startColumn"]
                del_e = parsed_data[i]["leftSideLocations"][0]["endColumn"]
            else:
                file_name = parsed_data[i]["leftSideLocations"][0]["filePath"].split('/')[-1].split('.')[0]
                del_s = parsed_data[i]["leftSideLocations"][-1]["startLine"]
                del_e = parsed_data[i]["leftSideLocations"][-1]["endLine"]
            shutil.copyfile(source_path + f + '/' + file_name + '.java', target_path + str(idx) + '/' + file_name + '.java')
            lists = os.listdir(source_path + f + '/edge/' + file_name)
            if len(lists) == 1:
                shutil.copyfile(source_path + f + '/edge/' + file_name + '/' + lists[0], target_path + str(idx) + '/cpg.txt')
            else:
                for j in lists:
                    op = open(source_path + f + '/edge/' + file_name + '/' + j, 'r', encoding='UTF-8')
                    opc = re.findall('(\d+) (\d+)\n', op.read())
                    op.close()
                    if len(opc) == 0:
                        continue
                    if start <= int(opc[0][0]) <= end:
                        shutil.copyfile(source_path + f + '/edge/' + file_name + '/' + j, target_path + str(idx) + '/cpg.txt')
                        break
            method_range = str(start) + ',' + str(end)
            del_range = str(del_s) + ',' + str(del_e)
            with open(target_path + str(idx) + '/method_range.csv', 'w+') as mr:
                mr.writelines(method_range)
            with open(target_path + str(idx) + '/del_range.csv', 'w+') as dr:
                dr.writelines(del_range)
            idx = idx + 1


if __name__ == '__main__':
    work()