
#### 20181115
1. 明天把模型修改好，一是要修改业务源宿节点的概率，二是调整好业务持续时间、到达率、潮汐持续时间等几个参数[done]

#### 20181205
1. edgeLoad 的数据保存还没有做
2. 数据生成模型需要更改，目前打算让程序执行时间固定，平均业务持续时间固定，改变业务量（到达率）即改变业务数量，同时，潮汐到达时间也变成了固定值[done]

#### 20181206
1. 250erlang的数据生成完毕（20天），用作模型的训练数据，下步着手开始训练模型了[done]

#### 20181208
1. 模型保存的路径按service_ratio分，名称按隐藏层个数和神经元个数分
2. 在模型训练的model_config.json中，少了一个参数，正在努力回忆。想起来了，是model_type(用于区分area和link)[done]
3. 模型的评价指标记得查询，选2、3个即可（还要保证有分类的指标和回归的指标） --> 去查文献
4. 数据是否都要作为训练集？怎样确定训练集、验证及、测试集的数据量？ --> 查文献

##### 关于分类问题的模型评价指标

||1|0|
|-|-|-|
|1|TP|FN|
|0|FP|TN|
TP（实际为正预测为正），FP（实际为负但预测为正），TN（实际为负预测为负），FN（实际为正但预测为负）
- 召回率（Recall,TNR）：预测对的正例数占真正的正例数的比率计算公式：Recall=TP / (TP+FN)
- 准确率：反映分类器统对整个样本的判定能力，能将正的判定为正，负的判定为负，计算公式： Accuracy=(TP+TN) / (TP+FP+TN+FN)
- 精准率：指的是所得数值与真实值之间的精确程度；预测正确的正例数占预测为正例总量的比率，计算公式：Precision=TP / (TP+FP)
- 阴性预测值：阴性预测值被预测准确的比例，计算公式：NPV=TN / (TN+FN)
- F值：F-score是Precision和Recall加权调和平均数，并假设两者一样重要,计算公式：F1 Score=(2RecallPrecision) / (Recall+Precision)
- ROC图与AUC（Area Under the ROC Curve）值
    - ** ROC曲线说明：**
Sensitivity=正确预测到的正例数/实际正例总数
1-Specificity=正确预测到的负例数/实际负例总数
纵坐标为Sensitivity（True Positive Rate），横坐标为1-Specificity（True Negative Rate），ROC 曲线则是不同阈值下Sensitivity和1-Specificity的轨迹。

    - 阈值：阈值就是一个分界线，用于判定正负例的，在模型预测后我们会给每条预测数据进行打分（0<score<1）。如：指定阈值为0.6，那么评分低于0.6的会被判定为负例（不好的），评分高于0.6的即会判定为正例（好的），随着阈值的减小，判定为正例的样本相应地就会增加。

    - AUC（Area Under the ROC Curve）指标在模型评估阶段常被用作最重要的评估指标来衡量模型的准确性，横坐标为其中随机分类的模型AUC为0.5，所以模型的AUC基线值大于0.5才有意义。
模型的ROC曲线越远离对角线，说明模型效果越好，ROC曲线下的区域面积即为AUC值，AUC值越接近1模型的效果越好。随着阈值的减小，Sensitivity和1-Specificity也相应增加，所以ROC曲线呈递增态势。

##### 关于回归模型的评估
- MSE 均方误差
    - $$ \frac{1}{m} \sum_{i=1}^m(y_i - \bar{y_i})^2 $$
- RMSE 平方根误差
    - $$ \sqrt{\frac{1}{m} \sum_{i=1}^m(y_i - \bar{y_i})^2} $$
- MAE 绝对平均误差
    - $$ \frac{1}{m} \sum_{i=1}^m{|y_i - \bar{y_i}|} $$
- R方
    - $$ R^2 = 1 - \frac{SS_{residual}}{SS_{total}} $$
    - 其中分子是Residual Sum of Squares 分母是 Total Sum of Squares
    - 上面分子就是我们训练出的模型预测的所有误差。
下面分母就是不管什么我们猜的结果就是y的平均数。（瞎猜的误差）
    - $$ R^2 = 1 - \frac{MSE(\bar{y}, y)}{Var(y)} $$


#### 20181209
1. 灵活读取json参数后，按照参数创建隐藏层层数与规模[done]