class Allele:
    def __init__(self, fullname, name, dom = True):
        self.fullname = fullname
        self.name = name
        self.dom = dom
    def domChange(self):
        if self.dom == True:
            self.dom = False
        else:
            self.dom = True
    def dominance(self):
        return self.dom
#////////////////////////////////////////////////////////
class Fly:
    def __init__(self, allele1, allele2):
        self.al1 = allele1
        self.al2 = allele2
        self.name1 = allele1.name
        self.name2 = allele2.name
        self.sex = 'n'
        self.autosomal = True
        self.sexlinked = False
        self.homozygous = True
        if allele1.name != allele2.name:
            self.homozygous = False

    def sex(self, x):
            self.sex = x

    def sexlinked(self):
        self.sexlinked = True
        self.autosomal = False
    def autosomal(self):
        self.autosomal = True
        self.sexlinked = False

#/////////////////////////////////////////////////////////

class Punnett:
    def __init__(self, fly1, fly2, generation):
        self.gen = generation
        self.p1 = fly1
        self.p2 = fly2
        self.p1square = [fly1.al1, fly1.al2]
        self.p2square = [fly2.al1, fly2.al2]
        self.offFlies = []
        self.offGenos =[]
        self.homozygotes = []
        self.heterozygotes = []
        self.nextGenSquares = []

    def mate(self):
        x1 = Fly(self.p1square[0], self.p2square[0])
        x2 = Fly(self.p1square[1], self.p2square[0])
        x3 = Fly(self.p1square[0], self.p2square[1])
        x4 = Fly(self.p1square[1], self.p2square[1])
        flies = [x1, x2, x3, x4]
        homotemp = []
        heterotemp = []
        for fly in flies:
            if fly.homozygous:
                if fly.al1.name not in homotemp:
                    homotemp += [fly.al1.name]
                    self.offFlies += [fly]
            else:
                if fly.al1.name not in heterotemp:
                    heterotemp += [fly.al1.name, fly.al2.name]
                    self.offFlies += [fly]
        #self.offGenos = [x1, x2, x3, x4]
        self.zygotes(self.offFlies)
        self.offGenos = [(x.al1.name, x.al2.name) for x in self.offFlies]


    def zygotes(self, alist):
        for x in alist:
            if x.homozygous:
                self.homozygotes.append(x)
            else:
                self.heterozygotes.append(x)

    def printOffspring(self):
        print('F'+str(self.gen) + ' Generation:(parents '+ str(self.p1.al1.name)+'/'+str(self.p1.al2.name)+' x '+str(self.p2.al1.name)+'/'+str(self.p2.al2.name)+')')
        print('Homozygotes:')
        if len(self.homozygotes) == 0:
            print('None')
        else:
            for x in self.homozygotes:
                print(str(x.al1.name)+ '/' + str(x.al2.name))
        print('Heterozygotes:')
        if len(self.heterozygotes) == 0:
            print("None")
        else:
            for x in self.heterozygotes:
                print(str(x.al1.name)+'/'+ str(x.al2.name))

    def printOffGenos(self):
        for x in self.offGenos:
            print(x)

    def nextGen(self):
        i = 0
        squares = []
        while i < len(self.offFlies):
            for fly in self.offFlies[i:]:
                tempsquare = Punnett(self.offFlies[i], fly, self.gen+1)
                squares += [tempsquare]
                tempsquare.mate()
                #tempsquare.printOffspring()
            i+=1
        self.nextGenSquares = squares
        if len(squares) == 1:
            return squares[0]
        else:
            print(str(len(squares))+' squares')
            return squares

#///////////////////////////////////////////////////////////

wt = Allele('straight', 'sn+', True)
sn = Allele('singed', 'sn', False)
fly1 = Fly(wt, wt)
fly2 = Fly(sn, sn)
f1psquare = Punnett(fly1, fly2, 1)
f1psquare.mate()
f1psquare.printOffspring()
f2 = f1psquare.nextGen()
f2.printOffspring()

f2psquare = Punnett(f1psquare.heterozygotes[0], f1psquare.heterozygotes[0], 2)
f2psquare.mate()
f2psquare.printOffspring()
print(f2psquare.offFlies[0].homozygous)
f2psquare.printOffGenos()
print(f2psquare.homozygotes)
print(f2psquare.heterozygotes)
