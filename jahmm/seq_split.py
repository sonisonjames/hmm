__author__ = 'sjames'
import sys
import os


fname = sys.argv[1]
train_sz = int(sys.argv[2])
f = open(fname, 'r')
for line in f:
    obs = []
    for x in line.split(';'):
        obs.append(x.strip())

    train = open(fname + '.train', 'w')
    i = 0
    for x in range(train_sz):
        train.write('%s; ' % obs[x])
    train.close()
    os.chmod(fname + '.train', 666)

    test = open(fname + '.test', 'w')
    for x in range(train_sz, len(obs) - 1):
        test.write('%s; ' % obs[x])
    test.close()
    os.chmod(fname + '.test', 666)