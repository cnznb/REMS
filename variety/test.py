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


def get_embedding(text):
    cl_tokens = tokenizer.tokenize(text)
    tokens = [tokenizer.cls_token] + cl_tokens + [tokenizer.sep_token]
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
    f_name = r"C:\Users\Qushao\Desktop\SVM_CodeBert.py"
    f_open = open(f_name, "r", encoding='utf-8')
    vecs_out = open("res_vecs.txt", "w")
    lines_out = open("res_lines.txt", "w")
    vecs = []
    lines = []
    line_number = 1
    for line in f_open.readlines():
        if line in ['\n', '\r\n'] or line.strip() == "":
            line_number += 1
        else:
            vec = get_embedding(line)
            lines.append(line_number)
            line_number += 1
            vecs.append(vec)
    # pca_data = np.around(pca.fit_transform(np.array(ss.fit_transform(vecs)).astype(np.float64)), decimals=6)
    # print(pca_data.shape)
    # pca_data = pca_data.tolist()
    json.dump(vecs, vecs_out)
    vecs_out.close()
    json.dump(lines, lines_out)
    lines_out.close()
