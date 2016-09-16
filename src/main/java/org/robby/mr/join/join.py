import random

arr = []
for i in range(0, 100):
    arr.insert(random.randint(0, len(arr)) ,i)

file = open('input1.txt', 'w+')
for i in arr:
    str = '%02d, user%02d'%(i,i) + "\n"
    file.write(str)
file.close()

arr = []
for i in range(0, 100):
    arr.insert(random.randint(0, len(arr)) ,i)

file = open('input2.txt', 'w+')
for i in arr:
    str = '%02d, %d%02d%02d'%(i,random.randint(1980,2000),random.randint(1,12),random.randint(1,29)) + "\n"
    file.write(str)
file.close()