# Introduction
This document is included in the 'Recommending Extract Method Refactoring Opportunities via Multi-view Representation of Code Property Graph' distribution，which we will refer to as REMS，This is to distinguish the recommended implementation of this Extract method refactoring from other implementations.https://anonymous.4open.science/r/REMS-A23C In this document, the environment required to make and use the REMS tool is described. Some hints about the installation environment are here, but users need to find complete instructions from other sources. They give a more detailed description of their tools and instructions for using them. My main environment is located on a computer with windows (windows 10 at the time of my writing) operating system. The fundamentals should be similar for other platforms, although the way in which the environment is configured will be different. What do I mean by environment? For example, to run python code you will need to install a python interpreter, and if you want to use node2vec you will need tensorflow.
# REMS
/src: The code files which is involved in the experiment \
/Training_CSV: The training data of feature fusion with code embedding and graph embedding \
/GEMS_test_data: The test data of feature fusion with code embedding and graph embedding \
/refactoring_tools: Extract method refactoring tools involved in the paper \
/dataset: The training and testing dataset before embedding \
/RQ3_questionnaire: the questionnaire and the questionnaire results of 10 participants \
/sampled_methods: sampled methods from Xu et al.’s dataset \
/tool: an intelligent extract method refactoring detection tool
# Tools
## code embedding network
CodeBERT GraphCodeBERT CodeGPT CodeT5 CoTexT PLBART
## graph embedding network 
DeepWalk LINE Node2vec GraRep SDNE ProNE walklets
# Requirement
## CodeBERT, GraphCodeBERT, CodeGPT, CodeT5, CoTexT, PLBART
python3(>=3.6) \
we use python 3.9\
torch transformers \
we use torch(1.12.0) and transformers(4.20.1)\
pre-trained model link: \
CodeBERT: https://huggingface.co/microsoft/codebert-base \
CodeGPT: https://huggingface.co/microsoft/CodeGPT-small-java-adaptedGPT2 \
GraphCodeBERT: https://huggingface.co/microsoft/graphcodebert-base \
CodeT5: https://huggingface.co/Salesforce/codet5-base-multi-sum \
CoTexT: https://huggingface.co/razent/cotext-2-cc \
PLBART: https://huggingface.co/uclanlp/plbart-base 

## DeepWalk，LINE ，Node2vec，GraRep，SDNE
hey all come from the open source project OpenNE \
numpy==1.14 networkx==2.0 scipy==0.19.1 tensorflow>=1.12.1 gensim==3.0.1 scikit-learn==0.19.0
## ProNE
numpy sklearn networkx gensim
## walklets
tqdm 4.28.1 numpy 1.15.4 pandas 0.23.4 texttable 1.5.0 gensim 3.6.0 networkx 2.4
## hyper-parameter settings
| Embedding Technique |                   Hyper-parameter settings                   |
| :-----------------: | :----------------------------------------------------------: |
|      CodeBERT       | train\_batch\_size=2048, embeddings\_size =768, learning\_rate=5e-4, max\_position\_length=512 |
|    GraphCodeBERT    | train\_batch\_size=1024, embeddings\_size =768, learning\_rate=2e-4, max\_sequence\_length=512 |
|       CodeGPT       |      embeddings\_size =768, max\_position\_length=1024       |
|       CodeT5        | train\_batch\_size=1024, embeddings\_size =768, learning\_rate=2e-4, max\_sequence\_length=512 |
|       PLBART        | train\_batch\_size=2048, embeddings\_size =768, dropout=0.1  |
|       CoTexT        | train\_batch\_size=128, embeddings\_size =768, learning\_rate=0.001, model\_parallelism=2, input\_length=1024 |
|      DeepWalk       | representation\_size=128, clf\_ratio=0.5, number\_walks=10, walk\_length=80, workers=8, window\_size=10 |
|      Node2Vec       | representation\_size=128, number\_walks=10, walk\_length=80, workers=8, window\_size=10, p=0.25, q=0.25 |
|      Walklets       | dimensions=128, walk-number=5, walk\_length=80, window\_size=5, workers=8, min\_count=1, p=1.0, q=1.0 |
|       GraRep        |  representation\_size=32, kstep=4, clf\_ratio=0.5, epochs=5  |
|        Line         | representation\_size=128, order=3, negative\_ratio=5, clf\_ratio=0.5, epochs=5 |
|        ProNE        |          dimension=128, step=10, theta=0.5, mu=0.2           |
|        SDNE         | alpha=1e-6, beta=5, nu1=1e-5, nu2=1e-4, batch\_size=200, epoch=100, learning\_rate=0.01 |
# Quickstart

> step1: We use graph embedding networks such as DeepWalk to embedding the structural information of the code dependencies of the code property graph

eg: feature1 feature2 ... featuren  0.16 0.78 ... 0.92

> step2: We use code embedding networks such as CodeBERT to embedding the semantic information of the code and get the vectors corresponding to the methods

eg: feature1 feature2 ... featurem  0.74 0.58 ... 0.67

> step3: We use Compact Bilinear Pooling for embedding fusion and get the hybrid vectors

> step4: We train the training set using classifiers commonly used in machine learning and deep learning, and optimize the hyperparameters using grid search

> step5: Model evaluation on the real-world dataset

# Datasets

train data: [why-we-refactor](https://aserg-ufmg.github.io/why-we-refactor/#/) 

real world data: JHotDraw JUnit MyWebMarket SelfPlanner WikiDev

