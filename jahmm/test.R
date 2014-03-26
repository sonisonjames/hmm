loadState <- function(file, skip, N, sep) {
  state <- read.csv(file, header=FALSE, sep=sep)
  x <- 1:(N-1)
  y <- c()
  for (i in (skip + 1):(skip + N)) {
    y <- c(y, state[1, i])
  }
  return(y)
}

plotStateSeq <- function(y, N) {
  x <- 1:(N-1)
  f <- stepfun(x, y)
  plot(f, x)
}

loadAndPlot <- function(file, skip, N, sep) {
  plotStateSeq(loadState(file, skip, N, sep), N)
}

to_bin <- function(l, x) {
  ret <- c()
  for (i in 1:(length(l) - 1)){
    #print(i)
    if(l[i] == x){
      ret <- c(ret, 1)
    }
    else {
      ret <- c(ret, 0)
    }
  }
  return(ret)
}