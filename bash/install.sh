mkdir -p ~/.WarpDrive
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > ~/.WarpDrive/WarpDrive.java # downloads the newest java source code file
cd ~/.WarpDrive && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it
