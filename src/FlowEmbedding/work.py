import subprocess
import os
import shutil

shell = "python -m openne --method node2vec --input D:\Pworkspace\REMS\src\GraphEmbedding\edge.txt" \
        " --graph-format edgelist --directed --output D:\Pworkspace\REMS\src\GraphEmbedding\output.txt --q 0.25 --p 0.25"
subprocess.run(shell, shell=True)

files = os.listdir("F:/sets/")
# for f in files:
     # shell = "python -m openne --method node2vec --input F:/sets/" + f + "/edgelist.txt --graph-format edgelist --directed --output F:/sets/" + f + "/node2vec_all.txt --q 0.25 --p 0.25"
     # deepwalk = "python -m openne --method deepWalk --input F:/sets/" + f + "/edgelist.txt --graph-format edgelist --directed --output F:/sets/" + f + "/deepwalk_vec_all.txt"
     # line = "python -m openne --method line --input F:/sets/" + f + "/edgelist.txt --graph-format edgelist --directed --output F:/sets/" + f + "/line_vec_all.txt"
     # sdne = "python -m openne --method sdne --input F:/sets/" + f + "/edgelist.txt --graph-format edgelist --directed --output F:/sets/" + f + "/sdne_vec_all.txt"
     # grarep = "python -m openne --method grarep --input F:/sets/" + f + "/edge/pdg.txt --graph-format edgelist --directed --representation-size 32 --output F:/sets/" + f + "/grarep_vec_all.txt"
     # subprocess.run(grarep, shell=True)
     # subprocess.run(deepwalk, shell=True)
     # subprocess.run(line, shell=True)
     # subprocess.run(sdne, shell=True)
     # subprocess.run(shell, shell=True)
i = 0
for f in files:
    ff = os.listdir("F:/sets/"+f)
    if not os.path.exists("F:/sets/" + f + '/pdg_vec'):
        os.mkdir("F:/sets/" + f + '/pdg_vec')
    for fx in ff:
        if fx.endswith('vec_all.txt'):
            shutil.move("F:/sets/" + f + '/'+fx, "F:/sets/" + f + '/pdg_vec')
