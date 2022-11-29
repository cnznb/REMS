# -*- encoding = utf-8 -*-
"""
@description: 4.通过执行git语句获取每个commit id对应的old文件，存于相应目录
@date: 2022/7/21
@File : get_old_file.py
@Software : PyCharm
"""
import re
import os
import json
import subprocess
from tqdm import tqdm
from builtins import str

to = "D:/dataset/"  # 数据集目录

files = os.listdir(to)
iii = 0
for f in tqdm(files):
    f_list = os.listdir(to + f)
    flag = 0
    for f_l in f_list:
        if f_l.endswith('old.java'):
            flag = 1
            break
    fc = open(to + f + '/' + f + '.json', 'r', encoding='UTF-8')
    c = fc.read()
    cc = c.replace('}{', '}<SPLIT>{')
    split_data = cc.split('<SPLIT>')
    fc.close()
    parsed_data = [json.loads(bit_data) for bit_data in split_data]
    if flag == 1:
        lists = list()
        for i in range(0, len(parsed_data)):
            file_name = parsed_data[i]["leftSideLocations"][0]["codeElement"]
            start = parsed_data[i]["leftSideLocations"][0]["startLine"]
            end = parsed_data[i]["leftSideLocations"][0]["endLine"]
            lists.append((int(start), int(end)))
            if i+1 < len(parsed_data) and parsed_data[i+1]["leftSideLocations"][0]["codeElement"] == file_name:
                continue
            else:
                xs = os.listdir(to + f + '/edge/' + file_name)
                for xxs in xs:
                    fs = open(to + f + '/edge/' + file_name + '/' + xxs, 'r', encoding='UTF-8')
                    cc = fs.read()
                    xss = re.findall('(\d+) (\d+)\n', cc)
                    fs.close()
                    if len(xss) == 0:
                        continue
                    ix = 0
                    for li in lists:
                        if li[0] <= int(xss[0][0]) <= li[1]:
                            ix = 1
                            break
                    if ix == 0:
                        print(to + f + '/edge/' + file_name + '/' + xxs)
                        os.remove(to + f + '/edge/' + file_name + '/' + xxs)
                lists.clear()
    else:
        lists = list()
        for i in range(0, len(parsed_data)):
            file_name = parsed_data[i]["leftSideLocations"][0]["filePath"].split('/')[-1].split('.')[0]
            start = parsed_data[i]["leftSideLocations"][0]["startLine"]
            end = parsed_data[i]["leftSideLocations"][0]["endLine"]
            lists.append((int(start), int(end)))
            if i + 1 < len(parsed_data) and parsed_data[i + 1]["leftSideLocations"][0]["codeElement"].split('/')[-1].split('.')[0] == file_name:
                continue
            else:
                xs = os.listdir(to + f + '/edge/' + file_name)
                for xxs in xs:
                    if os.path.getsize(to + f + '/edge/' + file_name + '/' + xxs) == 0:
                        os.remove(to + f + '/edge/' + file_name + '/' + xxs)
                        continue
                    fs = open(to + f + '/edge/' + file_name + '/' + xxs, 'r', encoding='UTF-8')
                    cc = fs.read()
                    xss = re.findall('(\d+) (\d+)\n', cc)
                    fs.close()
                    ix = 0
                    for li in lists:
                        if li[0] <= int(xss[0][0]) <= li[1]:
                            ix = 1
                            break
                    if ix == 0:
                        os.remove(to + f + '/edge/' + file_name + '/' + xxs)
                lists.clear()
    # 批量shell指令
    for i in range(len(parsed_data)):
        file_path = parsed_data[i]["leftSideLocations"][0]["filePath"]
        splits = file_path.split('/')
        subprocess.Popen(
            "git show " + f + "^^:" + file_path + " > " + to + f + '/' + splits[-1],
            cwd=os.path.dirname(to + f), shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
