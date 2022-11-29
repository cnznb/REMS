## 工具
[JOERN](https://docs.joern.io/home/)

## 数据集制作步骤
1. 根据本目录的refactorings.json文件，解析并提取仅由Extract Method操作修改的数据项，获取对应的commit id和相应的github项目地址
2. 将所有相关的github项目clone下来存于自建项目的tmp目录下
3. 使用解析工具[RefactoringMiner](https://github.com/tsantalis/RefactoringMiner)，利用commit id将所有Extract Method的重构信息存于json文件中，每一个commit id会有一个文件夹
4. 通过执行git语句获取每个commit id对应old文件，存于相应目录
5. 提取出json文件中每一项被重构的函数的起止行和内部被重构的行信息，分别存于method_line.csv、del_line.csv
6. 使用joern工具，生成old文件的PDG数据(dot文件)，通过代码转化为行与行之间的边集(edgelist.txt)，将其存于sets文件夹下，再用代码筛去未被重构的函数边集

