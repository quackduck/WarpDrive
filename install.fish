curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/wd.fish > ~/.config/fish/functions/wd.fish # downloads the newest fish function file
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > ~/WarpDrive.java # downloads the newest java source code file
cd ~ && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it
