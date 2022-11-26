import csv
import os
import json
import torch
import re
import pandas as pd
from compact_bilinear_pooling import CompactBilinearPooling

# 将代码的文本向量与其对应的图嵌入向量拼接到一起 并且打上T/F标签
# deepwalk_vec_all.txt    line_vec_all.txt   grarep_vec_all.txt   node2vec_all.txt   prone_sparse_all.txt   sdne_vec_all.txt
# walklets_vec_all.csv
names = ['deepwalk_vec_all.txt', 'line_vec_all.txt', 'grarep_vec_all.txt', 'node2vec_all.txt', 'prone_all.txt',
         'sdne_vec_all.txt', 'walklets_vec_all.csv']
# names = ['walklets_vec_all.csv']
sets_path = "F:/sets"
modifiedFileList = os.listdir(sets_path)
output_path = "F:/sets"
for n in names:
    cnt = 0
    Graph_Embedding = n
    for modifiedFile in modifiedFileList:
        modifiedFilePath = sets_path + "/" + modifiedFile + "/"
        edge_vectors = []
        edge_line_nums = []
        edge_line2vec = {}
        edge_vec_dim = 0
        edge_line_num = 0
        del_lines = []
        # 记录del_lines.txt里的行号信息到列表del_lines里 为后续打标签做准备
        with open(modifiedFilePath + 'del_range.txt', 'r', encoding='utf-8') as dr:
            ss = dr.read()
            del_lines = re.findall("(\d+) (\d+)", ss)
            # del_lines = [int(x) for x in dr.readline().split(' ')]
        # 使用字典edge_line2vec将文件的GE向量与行号对应起来  为后续拼接向量做准备
        # 若该文件没有对应的GraphEmbedding向量 则略过
        if not os.path.exists(modifiedFilePath + "cg_vec/codet5_cg_vec"):
            os.mkdir(modifiedFilePath + "cg_vec/codet5_cg_vec")
        if not os.path.exists(modifiedFilePath + "pdg_vec/" + Graph_Embedding):
            continue
        cnt = cnt + 1
        fff = 0
        if Graph_Embedding != 'walklets_vec_all.csv':
            with open(modifiedFilePath + "pdg_vec/" + Graph_Embedding, 'r', encoding='utf-8') as ge:
                lines = ge.readlines()
                edge_line_num, edge_vec_dim = [int(x) for x in lines[0].split(' ')]
                for x in lines[1:]:
                    lists = x.split(' ')
                    edge_line_nums.append(int(lists[0]))
                    edge_line2vec[int(lists[0])] = lists[1:]
        else:
            edge_vector = pd.read_csv(modifiedFilePath + "pdg_vec/walklets_vec_all.csv", header=None)
            edge_vector_data_list = edge_vector.values.tolist()[1:]
            for line in edge_vector_data_list:
                if type(line[0]) != float:
                    fff = 1
                    break
                line_num = int(line[0])
                edge_line_nums.append(line_num)
                line_vec = line[1:]
                edge_vec_dim = len(line_vec)
                edge_line2vec[line_num] = line_vec
        if fff == 1:
            continue
        # Compact Bilinear Pooling 拼接向量
        if n != 'prone_all.txt':
            mcb = CompactBilinearPooling(768, edge_vec_dim, 768 + edge_vec_dim)
        else:
            mcb = CompactBilinearPooling(768, edge_vec_dim, 768 + 32)
        # 分别读取文件的文本向量数据(降维后)和对应的行号数据

        if not os.path.exists(modifiedFilePath + "bert_vec/Codet5_vec.csv"):
            continue
        codeBert_vector_file_path = modifiedFilePath + "bert_vec/Codet5_vec.csv"
        codeBert_vector_file = open(codeBert_vector_file_path, 'r', encoding="utf-8")
        codeBert_vector = pd.read_csv(codeBert_vector_file, header=None)
        codeBert_vector_data_list = codeBert_vector.values.tolist()
        codeBert_vector_file.close()

        codeBert_vector_line_path = modifiedFilePath + "bert_vec/CodeBERT_lines.txt"
        codeBert_vector_line_file = open(codeBert_vector_line_path, 'r', encoding="utf-8")
        codeBert_vector_line = json.loads(codeBert_vector_line_file.readline())
        codeBert_vector_line_file.close()

        codeBert_vector_valid_line_path = modifiedFilePath + "method_range.txt"
        codeBert_vector_valid_line_file = open(codeBert_vector_valid_line_path, 'r', encoding="utf-8")
        method_lines = [int(x) for x in codeBert_vector_valid_line_file.readline().split(' ')]


        # 根据行号确定向量

        datas = []
        for i in range(len(codeBert_vector_line)):
            line_num = codeBert_vector_line[i]
            c_vector = [float(v) for v in codeBert_vector_data_list[i]]

            # 如果不是有效范围内的行
            if int(line_num) < method_lines[0]:
                continue
            if int(line_num) > method_lines[1]:
                break
            # 如果这一行有对应的图嵌入向量则将其拼接到末尾，否则拼接0向量
           #  print(c_vector)
            if int(line_num) in edge_line_nums:
                g_vector = [float(v) for v in edge_line2vec[int(line_num)]]
            else:
                g_vector = [0.0] * edge_vec_dim  # 若在图嵌入的输出结果中没有该行 则将其图嵌入结果置为0向量

            z = mcb(torch.tensor([c_vector]), torch.tensor([g_vector]))
            cg_vector = z.tolist()[0]

           # cg_vector = c_vector + g_vector

            # 如果这一行被删除了 则标签置为True 否则置为False
            flag = 0
            for u in del_lines:
                if int(u[0]) <= int(line_num) <= int(u[1]):
                    flag = 1
                    break
            if flag:
                cg_vector.append(True)
            else:
                cg_vector.append(False)
            # flag = cg_vector[-1]
            datas.append(cg_vector)
        with open(output_path + "/" + modifiedFile + "/cg_vec/codet5_cg_vec/"+Graph_Embedding.split('_')[0]+"_cg.csv",
                  'w+', encoding="utf-8", newline='') as f:
            csv_writer = csv.writer(f)
            for data in datas:
                csv_writer.writerow(data)
        print(modifiedFile)
