# -*- encoding = utf-8 -*-
"""
@description: 6.1 通过共享文件夹，用该脚本在ubuntu上跑数据集，利用joern工具生成数据集的dot文件
@date: 2022/7/23
@File : get_cpg_dot.py
@Software : PyCharm
"""
import subprocess
from tqdm import tqdm
import os

# ubuntu默认的joern安装目录
JOERNPATH = '/opt/joern/joern-cli/'


def code_to_dot(file_path, out_dir_cpg):
    # parse source code into cpg
    print('parsing source code into cpg...')
    shell_str = "sh " + JOERNPATH + "./joern-parse " + file_path
    subprocess.call(shell_str, shell=True)
    print('exporting cpg from cpg root...')
    # 导出cpg的dot文件到指定的文件夹中
    shell_export_cpg = "sh " + JOERNPATH + "./joern-export " + "--repr cpg14 --out " + out_dir_cpg
    subprocess.call(shell_export_cpg, shell=True)


# ubuntu共享文件夹数据集目录
source_dir = '/mnt/hgfs/dataset'
dirs = os.listdir(source_dir)
flag = 0
for files in tqdm(dirs):
    in_file = os.listdir(source_dir+'/'+files)
    for f in in_file:
        if f.split('.')[-1] == 'java':
            code_to_dot(source_dir+'/'+files+'/'+f, source_dir+'/'+files+'/'+f.split('.')[0])