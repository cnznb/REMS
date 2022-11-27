import csv
import os
import torch
import numpy as np
from transformers import AutoTokenizer, AutoModel
from sklearn.decomposition import PCA
from sklearn import preprocessing
import json

# 使用CodeBERT生成各文件源码中被重构函数的各代码行生成的文本向量
# 其中一些文件的源码为空 或者是 有效代码行为空   这些文件不会生成对应向量文件

print("开始测试")
np.set_printoptions(suppress=True)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained(r"../../REMS/model/codebert-base")
model = AutoModel.from_pretrained(r"../../REMS/model/codebert-base")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
print('Finish Loading Pretrain Model ! !')


def get_embedding(text):
    cl_tokens = tokenizer.tokenize(text)
    tokens = [tokenizer.cls_token] + cl_tokens + [tokenizer.sep_token]
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
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


if __name__ == '__main__':
    set_path = r"../../REMS/data_demo"
    files = os.listdir(set_path)  # data_demo/下的各个文件
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
        output_fn = set_path + '/code_embedding/CodeBERT_vec.csv'
        with open(output_fn, 'w+', encoding="utf-8", newline='') as wf:
            cw = csv.writer(wf)
            for data in vecs:
                cw.writerow(data)
        # 将有嵌入向量的代码行信息写入文件
        lines_out_file = set_path + "/code_embedding/Code_lines.txt"
        lines_out = open(lines_out_file, "w")
        json.dump(lines, lines_out)
        lines_out.close()
    else:
        print("源码为空，略过")

