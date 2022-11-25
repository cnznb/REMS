import csv
import os
import subprocess
from lxml import etree
import torch
import numpy as np
from transformers import AutoTokenizer, AutoModel
from sklearn.decomposition import PCA
from sklearn import preprocessing
import json

np.set_printoptions(suppress=True)
pca = PCA(n_components=16)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained("D:/pythonProject/Cbert/codet5-base-multi-sum")
model = AutoModel.from_pretrained("D:/pythonProject/Cbert/codet5-base-multi-sum")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
print('Finish Loading Pretrain Model ! !')


def read_fileproject(java_project):
    base_path = java_project
    files_list = []
    for root, dirs, files in os.walk(base_path):
        for file in files:
            filePath = os.path.splitext(file)
            if filePath[0] == '.java' or filePath[1] == '.java':
                files_list.append(os.path.join(root, file))
    return files_list


# def get_embedding(file_name):
#     output = subprocess.run([r"E:\srcML\srcml.exe", file_name], capture_output=True, check=False)
#     root = etree.fromstring(output.stdout)
#     com_texts = []
#     contents = []
#     for com in root.xpath('//*[local-name() = "comment"]'):
#         com_text = "".join(com.xpath('.//text()'))
#         com_texts.append(com_text)
#     for bad in root.xpath('//*[local-name() = "comment"]'):
#         bad.getparent().remove(bad)
#     for funcs in root.xpath('.//*[local-name() = "function"]'):
#         content = "".join(funcs.xpath('.//text()'))
#         contents.append(content)
#     nl_str = "\n".join(com_texts)
#     cl_str = "\n".join(contents)
#     text_tokens = tokenizer.tokenize(nl_str + cl_str + "</s>")
#     tokens_ids = tokenizer.convert_tokens_to_ids(text_tokens)
def get_embedding(text):
    tokens = tokenizer.tokenize(text+"</s>")
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
    # print(len(tokens_ids))
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
    project_list = "F:/sets"
    files = os.listdir(project_list)
    for project in files:
        f_list = os.listdir(project_list + '/' + project)
        f_name = ''
        for f in f_list:
            if f.endswith('.java'):
                f_name = project_list + '/' + project + '/' + f
                break
        lines = []
        vecs = []
        data_vec = []
        print("--------------------" + project + "--------------------\n")
        # print("--------------------Start To Embedding--------------------\n")
        f_open = open(f_name, "r", encoding='utf-8')
        codes = f_open.readlines()
        f_open.close()
        if len(codes) == 0:
            print("源码为空，略过")
            continue
        line_number = 0
        for line in codes:
            line_number += 1
            if line in ['\n', '\r\n'] or line.strip() == "":
                continue
            else:
                vec = get_embedding(line)
                lines.append(line_number)
                vecs.append(vec)
        # if len(lines) < 16:
        #     continue
        # print("--------------------Finish Embedding--------------------\n")
        # print("--------------------Start To PCA--------------------\n")
        # pca_data = np.around(pca.fit_transform(np.array(ss.fit_transform(vecs)).astype(np.float64)), decimals=6)
        # print("--------------------Finish PCA--------------------\n\n\n")
        # for x in pca_data:
        #     # print(x.tolist())
        #     data_vec.append(x.tolist())
        output_fn = project_list + '/' + project + '/bert_vec/Codet5_vec.csv'
        with open(output_fn, 'w+', encoding="utf-8", newline='') as wf:
            cw = csv.writer(wf)
            for data in vecs:
                cw.writerow(data)