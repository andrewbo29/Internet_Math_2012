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

usingFeatures <- c(1:5,7,10,12,13,14)

nnet <- nnet(x=data[,usingFeatures], y=y, size=300, MaxNWts=7000, decay=0.5, maxit=150)

setwd("D:/Интернет-математика 2012/fearures_test")

feature_test <- read.table(featuresNames[1])
data_test <- data.frame(feature_test)
for (i in 2:length(featuresNames)) {
  feature_test <- read.table(featuresNames[i])
  data_test <- data.frame(data_test, feature_test)
}

for (i in 1:length(featuresNames)) {
  data_test[is.na(data_test[,i]),i] <- 0
}

predict <- predict(object=nnet, newdata=data_test[,usingFeatures])

outData <- data.frame(7856734:8595730, predict)
sessionOrder <- outData[order(predict,decreasing=TRUE), 1]
setwd("D:/Интернет-математика 2012/result")
write(sessionOrder, "Result.txt", sep="\n", ncolumns=length(sessionOrder))