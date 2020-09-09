if test ! "$wd_source_containing_dir"; then
    wd_source_containing_dir="~/.WarpDrive"
fi
mkdir -p $wd_source_containing_dir
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > "$wd_source_containing_dir"/WarpDrive.java # downloads the newest java source code file
cd $wd_source_containing_dir && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it
