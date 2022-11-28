# Introduction
This document is included in the 'Recommending Extract Method Refactoring Opportunities via Multi-view Representation of Code Property Graph' distribution，which we will refer to as REMS，This is to distinguish the recommended implementation of this Extract method refactoring from other implementations.https://anonymous.4open.science/r/REMS-8940 In this document, the environment required to make and use the REMS tool is described. Some hints about the installation environment are here, but users need to find complete instructions from other sources. They give a more detailed description of their tools and instructions for using them. My main environment is located on a computer with windows (windows 10 at the time of my writing) operating system. The fundamentals should be similar for other platforms, although the way in which the environment is configured will be different. What do I mean by environment? For example, to run python code you will need to install a python interpreter, and if you want to use node2vec you will need tensorflow.
# REMS
/src: The code files which is involved in the experiment \
/Training_CSV: The training data of feature fusion with code embedding and graph embedding \
/GEMS_test_data: The test data of feature fusion with code embedding and graph embedding \
/

# Tools
## code embedding network
CodeBERT GraphCodeBERT CodeGPT CodeT5 CoTexT PLBART
## graph embedding network 
DeepWalk LINE Node2vec GraRep SDNE ProNE walklets
# Requirement
## CodeBERT, GraphCodeBERT, CodeGPT, CodeT5, CoTexT, PLBART
python3(>=3.6) \
torch transformers \
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

