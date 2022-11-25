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
pca = PCA(n_components=128)
ss = preprocessing.StandardScaler()
print('Start To Load Pretrain Model ... ...')
tokenizer = AutoTokenizer.from_pretrained("../codebert-base")
model = AutoModel.from_pretrained("../codebert-base")
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


def get_embedding(file_name):
    output = subprocess.run([r"E:\srcML\srcml.exe", file_name], capture_output=True, check=False)
    root = etree.fromstring(output.stdout)
    com_texts = []
    contents = []
    for com in root.xpath('//*[local-name() = "comment"]'):
        com_text = "".join(com.xpath('.//text()'))
        com_texts.append(com_text)
    for bad in root.xpath('//*[local-name() = "comment"]'):
        bad.getparent().remove(bad)
    for funcs in root.xpath('.//*[local-name() = "function"]'):
        content = "".join(funcs.xpath('.//text()'))
        contents.append(content)
    nl_str = "\n".join(com_texts)
    cl_str = "\n".join(contents)
    nl_tokens = tokenizer.tokenize(nl_str)
    code_tokens = tokenizer.tokenize(cl_str)
    tokens = [tokenizer.cls_token] + nl_tokens + [tokenizer.sep_token] + code_tokens + [tokenizer.sep_token]
    tokens_ids = tokenizer.convert_tokens_to_ids(tokens)
    print(len(tokens_ids))
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
    project_list = [r"C:\Users\Qushao\Desktop\apache-camel-1.6.0",
                    r"C:\Users\Qushao\Desktop\apache-cassandra-incubating-0.3.0-src",
                    r"C:\Users\Qushao\Desktop\apache-openjpa-1.0.1-source",
                    r"C:\Users\Qushao\Desktop\hbase-0.1.0",
                    r"C:\Users\Qushao\Desktop\hive-0.7.0"]
    for project in project_list:
        files_list = read_fileproject(project)
        output_fn = "../output/codebert/" + project.split("\\")[-1] + ".txt"
        output_file = open(output_fn, "w")
        file_name = []
        vecs = []
        result_dict = {}
        print("--------------------" + project + "--------------------\n")
        print("--------------------Start To Embedding--------------------\n")
        for el in files_list:
            vec = get_embedding(el)
            vecs.append(vec)
            fn = el[24:len(el)]
            file_name.append(fn)
        print("--------------------Finish Embedding--------------------\n")
        print("--------------------Start To PCA--------------------\n")
        pca_data = np.around(pca.fit_transform(np.array(ss.fit_transform(vecs)).astype(np.float64)), decimals=6)
        print("--------------------Finish PCA--------------------\n\n\n")
        for i in range(0, len(pca_data)):
            result_dict[file_name[i]] = pca_data[i].tolist()
        json.dump(result_dict, output_file)
        output_file.close()
