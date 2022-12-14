# CodeBERT+GraRep 的P R F1值
CB_GR<-array(c(0.545,0.607,0.671,0.684,0.717,0.832,0.948,0.956,0.981,
               0.454,0.516,0.559,0.561,0.562,0.570,0.984,0.990,0.995, 
               0.495,0.558,0.558,0.616,0.630,0.809,0.966,0.973,0.988),
          dim=c(9,3),
          dimnames=list(c('1','2','3','4','5','6','7','8','9'), c('P','R','F1')))
# GraphCodeBERT+Node2Vec 的P R F1值
GCB_NV<-array(c(0.517,0.561,0.589,0.623,0.633,0.647,0.794,0.916,0.958,
                0.537,0.562,0.566,0.600,0.606,0.864,0.935,0.969,0.977, 
                0.527,0.561,0.577,0.611,0.619,0.740,0.859,0.942,0.967),
              dim=c(9,3),
              dimnames=list(c('1','2','3','4','5','6','7','8','9'), c('P','R','F1')))
# CodeGPT+GraRep 的P R F1值
CG_GR<-array(c(0.519,0.685,0.699,0.763,0.806,0.913,0.949,0.960,0.984,
               0.452,0.521,0.563,0.564,0.577,0.578,0.585,0.586,0.975, 
               0.483,0.592,0.624,0.649,0.673,0.708,0.724,0.728,0.979),
               dim=c(9,3),
               dimnames=list(c('1','2','3','4','5','6','7','8','9'), c('P','R','F1')))
print(CB_GR)
print(GCB_NV)
print(CG_GR)
treatment <- c(CB_GR[,1]) # 取CB_GR第一列也就是其P值
control <- c(GCB_NV[,1]) # 取GCB_NV第一列也就是其P值
res = cliff.delta(treatment,control,return.dm = TRUE)
print(res)
