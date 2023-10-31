import subprocess
import os
files = os.listdir("F:/sets/")
for f in files:
    shell = "python main.py --input F:/sets/" + f + "/pdg.csv --output F:/sets/" + f + "/pdg_vec/walklets_vec_all.csv "
    subprocess.run(shell, shell=True)
    #     ll = os.listdir("D:/work/OpenNE-master/data/sets/" + f + "/pdg_vec")
    #     for l in ll:
    #         if l.endswith('.csv'):
    #             i = i + 1
    # print(i)