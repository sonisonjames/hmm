__author__ = 'sjames'

from subprocess import call
from os import system

for x in range(1, 100):
    file = str(x) + '.hmm'
    #print('Generating ' + file)
    system('java -jar jahmm.jar learn-bw -ni ' + str(x) + ' -opdf integer -r 4 -i start.hmm -is integer.seq -o ' + file)
    #print('Distance between start.hmm and ' + file + ':')
    #system('java -jar jahmm.jar distance-kl -opdf integer -r 4 -i start.hmm -ikl ' + file)
    if (x > 1):
        prev = str(x - 1) + '.hmm'
        #print('Distance between ' +  file + ' and ' + prev + ':')
        system('java -jar jahmm.jar distance-kl -opdf integer -r 4 -i ' + prev + ' -ikl ' + file)