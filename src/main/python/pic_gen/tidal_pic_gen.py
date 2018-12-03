import numpy as np
import matplotlib.pyplot as plt
import sys
import os

#parent_path = os.getcwd()
parent_path = sys.path[0]
for i in range(4):
    parent_path = os.path.abspath(os.path.join(parent_path, '..'))


sys.path.append(parent_path)
new_path = parent_path
new_path = os.path.join(parent_path, 'target')
new_path = os.path.join(new_path, 'generated-sources')
file_path1 = new_path + '\\' + 'area1loadCount.txt'
file_path3 = new_path + '\\' + 'area3loadCount.txt'
file_area1 = open(file_path1)
file_area3 = open(file_path3)
dataMat_area1 = []
dataMat_area3 = []
for line in file_area1.readlines():
    dataMat_area1.append(float(line))

for line in file_area3.readlines():
    dataMat_area3.append(float(line))

x = np.linspace(1, 360, num=360)
plt.plot(x, dataMat_area1, label='area1', c='b')
plt.plot(x, dataMat_area3, label='area3', c='g')
plt.legend()
plt.show()
