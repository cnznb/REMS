# -*- encoding = utf-8 -*-
"""
@description: 使用CodeT5生成各文件源码中被重构函数的各代码行生成的文本向量
@date: 2022/9/4
@File : CodeT5.py
@Software : PyCharm
"""
import csv
import os
import sys
import torch
import numpy as np
from transformers import AutoTokenizer, AutoModel
from sklearn import preprocessing

model_path = sys.argv[1]  # CodeT5预训练模型目录
dataset_path = sys.argv[2]  # dataset目录

# 预训练
np.set_printoptions(suppress=True)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModel.from_pretrained(model_path)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
print('Finish Loading Pretrain Model ! !')


def get_embedding(text):
    tokens = tokenizer.tokenize(text+"</s>")
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
    embedding = []
    index = 0
    while (index + 512) < len(tokens_ids):
        output = model.encoder(input_ids=torch.tensor(tokens_ids[index:(index + 512)])[None, :], attention_mask=torch.ones(1, 512), return_dict=True)
        embedding.extend(output.last_hidden_state[0].detach().numpy().tolist())
        index += 512
    if index < len(tokens_ids):
        output = model.encoder(input_ids=torch.tensor(tokens_ids[index:len(tokens_ids)])[None, :], attention_mask=torch.ones(1, len(tokens_ids) - index), return_dict=True)
        embedding.extend(output.last_hidden_state[0].detach().numpy().tolist())
    embedding = np.array(embedding).mean(axis=0).tolist()
    return embedding


if __name__ == '__main__':
    set_path = dataset_path
    files = os.listdir(set_path)  # dataset下的各个文件
    f_name = ''
    # 找到目录下的java格式文件
    for f in files:
        if f.endswith('.java'):
            f_name = f
            break
    if not os.path.exists(set_path + '/code_embedding'):
        os.mkdir(set_path + '/code_embedding')
    f_name = set_path + "/" + f_name
    # 读取java文件中各行代码
    f_open = open(f_name, "r", encoding='utf-8')
    codes = f_open.readlines()
    f_open.close()
    if len(codes) != 0:
        vecs = []
        lines = []
        line_number = 0
        # 遍历寻找有意义的代码行
        for line in codes:
            line_number += 1
            if line in ['\n', '\r\n'] or line.strip() == "":
                continue
            else:
                # 得到每行的代码嵌入向量表示
                vec = get_embedding(line)
                lines.append(line_number)
                vecs.append(vec)
        # 将各行代码嵌入向量写入文件
        output_fn = set_path + '/code_embedding/CodeT5_vec.csv'
        with open(output_fn, 'w+', encoding="utf-8", newline='') as wf:
            cw = csv.writer(wf)
            for data in vecs:
                cw.writerow(data)
    else:
        print("源码为空，略过")