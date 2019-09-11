bases = {'a':'t', 't':'a', 'g':'c', 'c':'g', 'A':'T', 'T':'A', 'G':'C', 'C':'G', 'u':'a', 'U':'A'}
CodonToAA = {'UUU':'Phe', "UUC":"Phe", "UUA":"Leu", "UUG":"Leu", "UCU":"Ser", "UCC":"Ser", "UCA":"Ser", "UCG":"Ser",
"UAU":"Tyr", "UAC":"Tyr", "UAA":"STOP", "UAG":"STOP", "UGA":"STOP", "UGU":"Cys", "UGC":"Cys", "UGG":"Trp", "CUU":"Leu",
"CUC":"Leu", "CUA":"Leu", "CUG":"Leu", "CCU":"Pro", "CCC":"Pro", "CCA":"Pro", "CCG":"Pro", "CAU":"His", "CAC":"His",
"CAA":"Glu", "CAG":"Glu", "CGU":"Arg", "CGC":"Arg", "CGA":"Arg", "CGG":"Arg", "AUU":"Ile", "AUC":"Ile", "AUA":"Ile",
"AUG":"Met", "ACU":"Thr", "ACC":"Thr", "ACA":"Thr", "ACG":"Thr", "AAU":"Asn", "AAC":"Asn", "AAA":"Lys", "AAG":"Lys",
"AGU":"Ser", "AGC":"Ser", "AGA":"Arg", "AGG":"Arg", "GUU":"Val", "GUC":"Val", "GUA":"Val", "GUG":"Val", "GCU":"Ala",
"GCC":"Ala", "GCA":"Ala", "GCG":"Ala", "GAU":"Asp", "GAC":"Asp", "GAA":"Glu", "GAG":"Glu", "GGU":"Gly", "GGC":"Gly",
"GGA":"Gly", "GGG":"Gly"}
AAToAminoAcids = {"Phe":"Phenylalanine", "Leu":"Leucine", "Ser":"Serine", "Tyr":"Tyrosine", "STOP":"STOP", "Cys":"Cysteine",
"Trp":"Tryptophan", "Pro":"Proline", "His":"Histidine", "Glu":"Glutamine", "Arg":"Arginine", "Ile":"Isoleucine", "Met":"Methionine",
"Thr":"Threonine", "Asn":"Asparagine", "Lys":"Lysine", "Val":"Valine", "Ala":"Alanine", "Asp":"Aspartic Acid", "Glu":"Glutamic Acid",
"Gly":"Glycine"}
AminoAcidsToAA = {"Phenylalanine":"Phe", "Leucine":"Leu", "Serine":"Ser", "Tyrosine":"Tyr", "STOP":"STOP", "Cysteine":"Cys",
"Tryptophan":"Trp", "Proline":"Pro", "Histidine":"His", "Glutamine":"Glu", "Arginine":"Arg", "Isoleucine":"Ile", "Methionine":"Met",
"Threonine":"Thr", "Asparagine":"Asn", "Lysine":"Lys", "Valine":"Val", "Alanine":"Ala", "Aspartic Acid":"Asp", "Glutamic Acid":"Glu",
"Glycine":"Gly"}

def Polymerase(x):
    y = bases[x[0]].upper()
    x = x[1:]
    for i in x:
        y+=bases[i].upper()
    return y

def change(x): #'t' tp 'u'
    if x is 'T':
        return 'U'
    else:
        return x

def RNAPolymerase (x):
    return ''.join([change(x) for x in Polymerase(x)])

def mRNAfromCodingStrand(x):
    return RNAPolymerase(Polymerase(x))

def mRNAfromAntiCodingStrand(x):
    return RNAPolymerase(x)

def findStartCodon(x):
    while x[0:3] != 'AUG':
        x=x[1:]
    return x

def findStopCodon(x):
    i = 0
    while i+3 < len(x):
        if CodonToAA[x[i:i+3]] == "STOP":
            return x[:i+3]
        else:
            i+=3
    return x

def restSeqRNA(x):
    y = findStopCodon(x)
    return x[len(y):]

def mRNAToAA(x):
    x = findStartCodon(x)
    xcut = len(x) % 3
    x = x[:len(x)-xcut]
    i = 0
    seq = ''
    while i < len(x):
        aa = CodonToAA[x[i:i+3]]
        if aa == "STOP":
            i+=3
            seq += aa
        else:
            i+=3
            seq += aa
            seq+=" "
    return seq

def firstAASeqfromRNA(seq):
    print(findStopCodon(seq))
    return mRNAToAA(findStopCodon(seq))

def firstSeqAA(seq):
    i = 0
    while i < len(seq):
        if seq[i:i+4] == "STOP":
            return seq[:(i+4)]
        else:
            i+=4
    return seq

#def mRNAToAA1.0(x):
    # while x[0:3] != "AUG":
    #     if len(x) < 3:
    #         return ''
    #     x = x[1:]
    # seq = ''
    # while len(x) > 2:
    #     aa = CodonToAA[x[0:3]]
    #     seq += aa
    #     if aa is 'STOP':
    #         if (len(x)-3) < 6:
    #             return seq
    #         else:
    #             seq += mRNAToAA(x[3:])
    #     else:
    #         x = x[3:]
    # return seq

print(mRNAToAA('GAGAUGGUCCCCGGGCCCAAAUUUUAGGGGCCCAUGUAG'))
print(firstSeqAA(mRNAToAA('GAGAUGGUCCCCGGGCCCAAAUUUUAGGGGCCCAUGUAG')))
print(firstAASeqfromRNA('GAGAUGGUCCCCGGGCCCAAAUUUUAGGGGCCCAUGUAG'))
print(restSeqRNA(('GAGAUGGUCCCCGGGCCCAAAUUUUAGGGGCCCAUGUAG')))
