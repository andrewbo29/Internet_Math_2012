tuningLambda <- function(lambda) {
  print(lambda)
  
  nnet <- nnet(x=train_data, y=train_y, size=number_neurons, MaxNWts=4000, decay=lambda)
  predict <- predict(object=nnet, newdata=cv_data)
  accuracy_cv <- (sum(abs(cv_y - predict)) / length(cv_examp))
  
  return (accuracy_cv)
}

tuningNumberNeurons <- function(number) {
  print(number)
  
  nnet <- nnet(x=train_data, y=train_y, size=number, MaxNWts=7000, decay=fix_lambda)
  predict <- predict(object=nnet, newdata=cv_data)
  accuracy_cv <- (sum(abs(cv_y - predict)) / length(cv_examp))
  
  return (accuracy_cv)
}

library(nnet)

setwd("D:/Интернет-математика 2012/features_train")

featuresNames <- c("Session_Time", "Number_of_Q", "Number_of_C", "Number_of_QCC_groups",
                   "Mean_Time_QCS", "Max_Time_QCS", "Mean_Time_QC", "Max_Time_QC", "Min_Time_QC",
                   "Mean_Time_CC", "Max_Time_CC", "Part_Q_without_any", "User_Mean_Time", "Number_of_S",
                   "Min_Time_CC")

feature <- read.table(featuresNames[1])
data <- data.frame(feature)
for (i in 2:length(featuresNames)) {
  feature <- read.table(featuresNames[i])
  data <- data.frame(data, feature)
}

for (i in 1:length(featuresNames)) {
  data[is.na(data[,i]),i] <- 0
}

y <- read.table("Output_Train")

# usingFeatures <- c(1:5,7,10,12,13,14)
usingFeatures <- c(1:14)

randPermExamp <- sample(data[,1])
permData <- data[randPermExamp,]
permY <- y[randPermExamp,]

train_examp <- 1:100000
train_data <- permData[train_examp, usingFeatures]
train_y <- permY[train_examp]

cv_examp <- 100001:200000
cv_data <- permData[cv_examp, usingFeatures]
cv_y <- permY[cv_examp]

number_neurons <- 300
lambda <- c(0.001, 0.005,0.01,0.05,0.1,0.5, seq(1,7,0.5))
accuracy <- 1 - t(sapply(lambda,FUN=tuningLambda))
plot(lambda, accuracy, type="b")

fix_lambda <- 0.001
nnumber <- c(50,100,200,300,400)
accuracy_nnumber <- 1 - t(sapply(nnumber,FUN=tuningNumberNeurons))
plot(nnumber, accuracy_nnumber, type="b")

fix_lambda <- 0.5
tmp1 <- tuningNumberNeurons(400)