procSession <- function(id) {
  
  print(id)
  
  numberQ <- 0
  numberC <- 0
  numberS <- 0
  numberQQ <- 0
  
  timeVector <- c()
  timeVectorQC <- c()
  timeVectorCC <- c()
  
  meanTimeQCS <- NA
  maxTimeQCS <- NA
  minTimeQCS <- NA
  meanTimeQC <- NA
  maxTimeQC <- NA
  minTimeQC <- NA
  meanTimeCC <- NA
  maxTimeCC <- NA
  minTimeCC <- NA
  
  previousTime <- 0
  currentTime <- 0
  
  isPreviousQ <- FALSE
  isPreviousC <- FALSE
  
  previousData <- NA
  
  lastQCIndex <- 0
   
  dataString <- firstSessionString
  while (length(dataString) == 0) {
    dataString <- readLines(con, 1)
  }
  data <- strsplit(dataString, "\t")[[1]]
  
  while (as.numeric(data[1]) == id) {
    
    if (data[3] != 'M') {
      
      currentTime <- as.numeric(data[2])
      differenceTime <- currentTime - previousTime
      timeVector <- c(timeVector, differenceTime)
      
      if (data[3] == 'Q') {
        numberQ <- numberQ + 1
        if (isPreviousQ) {
          numberQQ <- numberQQ + 1
        }
        isPreviousQ <- TRUE
        isPreviousC <- FALSE
        lastQCIndex <- as.numeric(data[4])
      } else {
        
        if (data[3] == 'C') {
          numberC <- numberC + 1
          if (isPreviousQ) {
            timeVectorQC <- c(timeVectorQC, differenceTime)
          } else {
            
            if (isPreviousC) {
              timeVectorCC <- c(timeVectorCC, differenceTime)
            }
          }
          
          isPreviousC <- TRUE
          isPreviousQ <- FALSE
          lastQCIndex <- as.numeric(data[4])
        } else {
          
          if (data[3] == 'S') {
            numberS <- numberS + 1
          }
        }
      }
      
      previousTime <- currentTime
    } else {
      
#       if (data[5] == 'N') {
#         y <- 0
#       } else {
#         y <- 1
#       }
#       output[id+1] <<- y
      
      currentUserId <- as.numeric(data[4])
      if (currentUserId != userId) {
        userId <<- currentUserId
        userIdMatrixRow <<- userIdMatrixRow + 1
        userIdMatrix[userIdMatrixRow,1] <<- userId
      }
#       userIdMatrix[userIdMatrixRow,2] <<- userIdMatrix[userIdMatrixRow,2] + y
      userIdMatrix[userIdMatrixRow,3] <<- userIdMatrix[userIdMatrixRow,3] + 1
    }
    
    previousData <- data
    dataString <- readLines(con, 1)
    while (length(dataString) == 0) {
      dataString <- readLines(con, 1)
    }
    data <- strsplit(dataString, "\t")[[1]]
  }
  
  firstSessionString <<- dataString
  
  allTime <- as.numeric(previousData[2])
  userIdMatrix[userIdMatrixRow,4] <<- userIdMatrix[userIdMatrixRow,4] + allTime
  
  groupQCNumber <- lastQCIndex + 1
  
  if (!is.null(timeVector)) {
    meanTimeQCS <- mean(timeVector, na.rm=TRUE)
    maxTimeQCS <- max(timeVector, na.rm=TRUE)
    minTimeQCS <- min(timeVector, na.rm=TRUE)
  }
  
  if (!is.null(timeVectorQC)) {
    meanTimeQC <- mean(timeVectorQC, na.rm=TRUE)
    maxTimeQC <- max(timeVectorQC, na.rm=TRUE)
    minTimeQC <- min(timeVectorQC, na.rm=TRUE)
  }
  
  if (!is.null(timeVectorCC)) {
    meanTimeCC <- mean(timeVectorCC, na.rm=TRUE)
    maxTimeCC <- max(timeVectorCC, na.rm=TRUE)
    minTimeCC <- min(timeVectorCC, na.rm=TRUE)
  }
  
  partQQ <- numberQQ / numberQ
  
  result <- c("Session_Time"=allTime, "Number_of_Q"=numberQ, "Number_of_C"=numberC, "Number_of_S"=numberS,
           "Number_of_QCC_groups"=groupQCNumber, "Mean_Time_QCS"=meanTimeQCS, "Max_Time_QCS"=maxTimeQCS, "Min_Time_QCS"=minTimeQCS,
           "Mean_Time_QC"=meanTimeQC, "Max_Time_QC"=maxTimeQC, "Min_Time_QC"=minTimeQC, "Mean_Time_CC"=meanTimeCC,
           "Max_Time_CC"=maxTimeCC, "Min_Time_CC"=minTimeCC, "Part_Q_without_any"=partQQ)
  
  return (result)
}

userSessionMerge<- function(id) {
  print(id)
  
  userProb <- NA
  userMeanTime <- NA
  result <- rep(NA, 2)
  
  dataString <- firstSessionString
  while (length(dataString) == 0) {
    dataString <- readLines(con, 1)
  }
  data <- strsplit(dataString, "\t")[[1]]
  
  if (data[3] == 'M' && as.numeric(data[1]) == id) {
    userId <- as.numeric(data[4])
    while (userIdMatrix[userIdStep,1] != userId) {
      userIdStep <<- userIdStep + 1
    }
    if (userIdMatrix[userIdStep,3] != 0) {
      userProb <- userIdMatrix[userIdStep,2] / userIdMatrix[userIdStep,3]
      userMeanTime <- userIdMatrix[userIdStep,4] / userIdMatrix[userIdStep,3]
    }
    
    result <- c("User_Prob"=userProb, "User_Mean_Time"=userMeanTime)
  }
  
  while (as.numeric(data[1]) == id) {
    dataString <- readLines(con, 1)
    while (length(dataString) == 0) {
      dataString <- readLines(con, 1)
    }
    data <- strsplit(dataString, "\t")[[1]]
  }
  
  firstSessionString <<- dataString
  
  return (result)
}

getUserId <- function(id) {
  print(id)
  
  dataString <- firstSessionString
  while (length(dataString) == 0) {
    dataString <- readLines(con, 1)
  }
  data <- strsplit(dataString, "\t")[[1]]
  
  if (data[3] == 'M' && as.numeric(data[1]) == id) {
    result <- as.numeric(data[4])
  }
  
  while (as.numeric(data[1]) == id) {
    dataString <- readLines(con, 1)
    while (length(dataString) == 0) {
      dataString <- readLines(con, 1)
    }
    data <- strsplit(dataString, "\t")[[1]]
  }
  
  firstSessionString <<- dataString
  
  return (result)
}

# fileName <- "datasetTest"
# fileName <- "train"
fileName <- "test"
con <- file(fileName, "r") 

sessionNumber <- 7856733
userNumber <- 956536

# sessionNumber <- 100
# userNumber <- 100

# userId <- 488348
userId <- 25719
userIdMatrix <- matrix(0, userNumber, 4)
userIdMatrixRow <- 1
userIdMatrix[1,1] <- userId

featuresNames <- c("Session_Time", "Number_of_Q", "Number_of_C", "Number_of_S", "Number_of_QCC_groups",
                  "Mean_Time_QCS", "Max_Time_QCS", "Min_Time_QCS", "Mean_Time_QC", "Max_Time_QC", "Min_Time_QC",
                  "Mean_Time_CC", "Max_Time_CC", "Min_Time_CC", "Part_Q_without_any")
userFeaturesNames <- c("User_Prob", "User_Mean_Time")

output <- vector(length=sessionNumber)
firstSessionString <- readLines(con, 1)
featuresTable <- t(sapply(7856734:8595729, FUN=procSession))

close(con)

for (i in 1:length(featuresNames)) {
  write.table(featuresTable[,i], featuresNames[i])
}
write.table(output, "Output_Train")

remove(featuresTable)

con <- file(fileName, "r") 

firstSessionString <- readLines(con, 1)
userIdStep <- 1
userFeaturesTable <- t(sapply(7856734:8595729, FUN=userSessionMerge))

close(con)

for (i in 1:length(userFeaturesNames)) {
  write.table(userFeaturesTable[,i], userFeaturesNames[i])
}