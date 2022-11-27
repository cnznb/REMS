import csv
import os
import torch
import numpy as np
from transformers import AutoTokenizer, AutoModel
from sklearn import preprocessing

# 使用CodeGPT生成各文件源码中被重构函数的各代码行生成的文本向量
# 其中一些文件的源码为空 或者是 有效代码行为空   这些文件不会生成对应向量文件

np.set_printoptions(suppress=True)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained(r"../../REMS/model/CodeGPT-small-java-adaptedGPT2")
model = AutoModel.from_pretrained(r"../../REMS/model/CodeGPT-small-java-adaptedGPT2")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
print('Finish Loading Pretrain Model ! !')


def get_embedding(text):
    code_tokens = tokenizer.tokenize(text)
    tokens = [tokenizer.bos_token] + code_tokens + [tokenizer.eos_token]
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
    embedding = []
    index = 0
    while (index + 512) < len(tokens_ids):
        tk = tokens_ids[index:(index + 512)]
        embedding.extend(model(torch.tensor(tk)[None, :]).last_hidden_state[0].detach().numpy().tolist())
        index += 512
    if index < len(tokens_ids):
        tk = tokens_ids[index:len(tokens_ids)]
        embedding.extend(model(torch.tensor(tk)[None, :]).last_hidden_state[0].detach().numpy().tolist())
    embedding = np.array(embedding).mean(axis=0).tolist()
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
        output_fn = set_path + '/code_embedding/CodeGPT_vec.csv'
        with open(output_fn, 'w+', encoding="utf-8", newline='') as wf:
            cw = csv.writer(wf)
            for data in vecs:
                cw.writerow(data)
    else:
        print("源码为空，略过")
