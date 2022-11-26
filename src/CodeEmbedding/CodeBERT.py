import csv
import os

import torch
import numpy as np
from transformers import AutoTokenizer, AutoModel
from sklearn.decomposition import PCA
from sklearn import preprocessing
import json
# import pandas as pd

# 使用CodeBERT生成各文件源码中被重构函数的各代码行生成的文本向量
# 其中一些文件的源码为空 或者是 有效代码行为空   这些文件不会生成对应向量文件

print("开始测试")
np.set_printoptions(suppress=True)
pca = PCA(n_components=128)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained("D:/pythonProject/Cbert/codebert-base")
model = AutoModel.from_pretrained("D:/pythonProject/Cbert/codebert-base")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
print('Finish Loading Pretrain Model ! !')


def get_embedding(text):
    cl_tokens = tokenizer.tokenize(text)
    tokens = [tokenizer.cls_token] + cl_tokens + [tokenizer.sep_token]
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
    # print(len(tokens_ids))
    embedding = []
    index = 0
    while (index + 512) < len(tokens_ids):
        embedding.extend(
            model(torch.tensor(tokens_ids[index:(index + 512)])[None, :]).last_hidden_state[0].detach().numpy()[0].tolist())
        index += 512
    if index < len(tokens_ids):
        embedding.extend(
            model(torch.tensor(tokens_ids[index:len(tokens_ids)])[None, :]).last_hidden_state[0].detach().numpy()[0].tolist())
    embedding = np.array(embedding).reshape((-1, 768)).mean(axis=0).tolist()
    return embedding


def isValidLine(number, valid):
    for r in valid:
        if r[0] <= number <= r[1]:
            return True
    return False


if __name__ == '__main__':
    set_path = "F:/sets"
    file_list = os.listdir(set_path)
    for file_name in file_list:
        file_path = set_path + "/" + file_name
        files = os.listdir(file_path)  # sets\{文件名}\下的各个文件
        f_name = ''
        for f in files:
            if f.endswith('.java'):
                f_name = f
                break
        if not os.path.exists(file_path + '/bert_vec'):
            os.mkdir(file_path + '/bert_vec')
        f_name = file_path + "/" + f_name
        print(f_name)
        f_open = open(f_name, "r", encoding='utf-8')
        codes = f_open.readlines()
        # r_open = open(file_path + "/method_range.csv", "r", encoding="utf-8")
        # line_ranges = r_open.readlines()[0].split(',')
        # r_open.close()
        f_open.close()
        # print(line_ranges)
        if len(codes) == 0:
            print("源码为空，略过")
            continue
        vecs_out_file = file_path + "/bert_vec/CodeBERT_vec.txt"
        os.remove(vecs_out_file)
        # vecs_out = open(vecs_out_file, "w")
        lines_out_file = file_path + "/bert_vec/CodeBERT_lines.txt"
        lines_out = open(lines_out_file, "w")
        vecs = []
        lines = []
        line_number = 0
        for line in codes:
            line_number += 1
            if line in ['\n', '\r\n'] or line.strip() == "":
                    # or not isValidLine(line_number, validLines)\
                continue
            else:
                vec = get_embedding(line)
                lines.append(line_number)
                vecs.append(vec)
        output_fn = file_path + '/bert_vec/CodeBERT_vec.csv'
        with open(output_fn, 'w+', encoding="utf-8", newline='') as wf:
            cw = csv.writer(wf)
            for data in vecs:
                cw.writerow(data)
        # json.dump(vecs, vecs_out)
        # vecs_out.close()
        json.dump(lines, lines_out)
        lines_out.close()

