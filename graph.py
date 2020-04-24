import os
import re
import sys
from os import walk
import matplotlib.pyplot as plt
plt.style.use('seaborn-whitegrid')
import numpy as np


def getVals(f):
    file = open(f, 'r') 
    lines = file.readlines()
    container = []
    time_vals = []
    count_vals = []

    for line in lines:
        split = line.split(":")
        count_str = split[1].split(" ")[1]
        time_str = split[2].lstrip()
        time_vals.append(int(time_str)/60);
        count_vals.append(int(count_str));
    container.append(time_vals)
    container.append(count_vals)
    return container



def main():
    f =  os.path.abspath(sys.argv[1])

    v12 = []
    v22 = []
    h = ["String", "Fingerprint"]
    for i in range(1,len(sys.argv)-1):
        print(i)
        values = getVals(os.path.abspath(sys.argv[i]))
        if(i == 1):
            v12 = values[1]
            v22 = values[0]
        else:
        
            for j in range (0,len(values[0])):
                v22[j] = v22[j] + values[0][j]
    plt.plot(v12, v22)#, label = h[i-1])

    plt.xlabel("Number of files")
    plt.ylabel("Time taken (minutes)")
    plt.title(sys.argv[len(sys.argv)-1])
    plt.legend()
    plt.show()






main()