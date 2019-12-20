library(rpart)
library(rpart.plot)
library(caret)
library(randomForest)
library(gbm)
library(caTools)
library(dplyr)
library(ggplot2)
library(ROCR)
library(tidyverse)
library(softImpute)
library(stringr)
library(glmnet)
library(pls)
library(randomForest)
library(caret)
library(leaps)
library(ranger)
library(tm)
library(SnowballC)
library(MASS)
library(caTools)
library(dplyr)
library(rpart)
library(rpart.plot)

OSR2 <- function(predictions, test, train) {
  SSE <- sum((test - predictions)^2)
  SST <- sum((test - mean(train))^2)
  r2 <- 1 - SSE/SST
  return(r2)
}

tableAccuracy <- function(label, pred) {
  t = table(label, pred)
  a = sum(diag(t))/length(label)
  return(a)
}
tableTPR <- function(label, pred) {
  t = table(label, pred)
  return(t[2,2]/(t[2,1] + t[2,2]))
}
tableFPR <- function(label, pred) {
  t = table(label, pred)
  return(t[1,2]/(t[1,1] + t[1,2]))
}

insurance = read.csv("car_insurance_claim.csv")

drops <- c("ID","BIRTH", "CLM_AMT")
insurance = insurance[ , !(names(insurance) %in% drops)]

insurance$CLAIM_FLAG <- as.factor(insurance$CLAIM_FLAG)
#insurance$INCOME = as.numeric((insurance$INCOME))
insurance$INCOME <- as.numeric(gsub('\\$|,', '', insurance$INCOME))
#insurance$HOME_VAL = as.numeric(as.character(insurance$HOME_VAL))
insurance$HOME_VAL <- as.numeric(gsub('\\$|,', '', insurance$HOME_VAL))
#insurance$BLUEBOOK = as.numeric(as.character(insurance$BLUEBOOK))
insurance$BLUEBOOK <- as.numeric(gsub('\\$|,', '', insurance$BLUEBOOK))
#insurance$OLDCLAIM = as.numeric(as.character(insurance$OLDCLAIM))
insurance$OLDCLAIM <- as.numeric(gsub('\\$|,', '', insurance$OLDCLAIM))

insurance$PREVCLAIM = as.numeric(insurance$OLDCLAIM != 0)
insurance$HOME = as.numeric(insurance$HOME_VAL != 0)

agegrouper = function(x) {
  if (x <= 18) {
    return('Youth')
  }
  if (x >= 65) {
    return('Senior')
  }
  else {return('Adult')
  }
}

insurance$AgeGroup = sapply(insurance$AGE, FUN = agegrouper)
insurance = as.data.frame(insurance)
a = insurance[insurance$AgeGroup != 'Adult',]
#Only 84 of 8163 total observations are either <=18yo or >=65yo
insurance = insurance[complete.cases(insurance), ]

set.seed(123) 
train.ids = sample(nrow(insurance), 0.75*nrow(insurance))
dataTrain = insurance[train.ids,]
dataTest = insurance[-train.ids,]

LogisticModel = glm(CLAIM_FLAG ~ . ,
                    data = dataTrain, 
                    family = 'binomial')
summary(LogisticModel)
predictions = predict(LogisticModel, newdata= dataTest, type="response")
tableAccuracy(dataTest$CLAIM_FLAG, predictions > 0.5)
#.795
tableTPR(dataTest$CLAIM_FLAG, predictions > 0.5)
#0.4562384
tableFPR(dataTest$CLAIM_FLAG, predictions > 0.5)
#0.0831117

#CART
cpVals = data.frame(cp = seq(0, .2, by=.001))
set.seed(123)
train.cart = train(CLAIM_FLAG ~ . ,
                   data = dataTrain,
                   method = "rpart",
                   tuneGrid = cpVals,
                   trControl = trainControl(method = "cv", number=10),
                   metric = "Accuracy")
train.cart$bestTune
#cp = 0.003 is best

train.cart = train(CLAIM_FLAG ~ . ,
                   data = dataTrain,
                   method = "rpart",
                   cp = .003)
#predict on test
pred.best.cart = predict(train.cart, newdata = dataTest, type = 'raw')
summary(pred.best.cart)
tableAccuracy(dataTest$CLAIM_FLAG, pred.best.cart) 
#.744
tableTPR(dataTest$CLAIM_FLAG, pred.best.cart)
#0.264432
tableFPR(dataTest$CLAIM_FLAG, pred.best.cart)
#0.08444149

#Random Forest
set.seed(123)
mod.rf <- randomForest(CLAIM_FLAG ~ . ,
                       data = dataTrain,
                       method = "rf",
                       tuneGrid = data.frame(mtry=1:16),
                       trControl = trainControl(method="cv",
                       number=5, verboseIter = TRUE),
                       metric = "Accuracy")
#mtry of 5 was best

mod.rf <- randomForest(CLAIM_FLAG ~ . ,
                       data = dataTrain,
                       mtry = 5)

pred.rf <- predict(mod.rf, newdata = dataTest, type = 'class') # just to illustrate
tableAccuracy(dataTest$CLAIM_FLAG, pred.rf)
#.788
tableTPR(dataTest$CLAIM_FLAG, pred.rf)
#0.3854749
tableFPR(dataTest$CLAIM_FLAG, pred.rf)
#0.0724734

blend = (.2)*as.numeric(as.character(pred.best.cart)) + (.4)*as.numeric(as.character(pred.rf)) + predictions
summary(blend)
tableAccuracy(dataTest$CLAIM_FLAG, blend > .888)
# 0.7907888
tableTPR(dataTest$CLAIM_FLAG, blend > .888)
#0.3649907
tableFPR(dataTest$CLAIM_FLAG, blend > .888)
#0.05718085

highestAcc = 0
probval = 0
cartconst = 0
rfconst = 0
logconst = 0

blendoutput <- function(cc, rc, pred.best.cart, pred.rf, predictions) {
  return((cc*as.numeric(as.character(pred.best.cart)) +
     rc*as.numeric(as.character(pred.rf))) +
    predictions)
}

for (cc in seq(0, 1, .1)) {
  print(cc)
  for (rc in seq(0, 1, .1)){
    for (p in seq(0, 3, 0.002)){
      blended = blendoutput(cc, rc, pred.best.cart, pred.rf, predictions)
      if (tableAccuracy(dataTest$CLAIM_FLAG, blended > p) > highestAcc){
        highestAcc = tableAccuracy(dataTest$CLAIM_FLAG, blend > p)
        cartconst = cc
        rfconst = rc
        logconst = lc
        probval = p
      }
    }
  }
}


