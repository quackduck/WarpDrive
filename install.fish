if test ! "$wd_source_containing_dir"
    set wd_source_containing_dir ~
end
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/wd.fish > ~/.config/fish/functions/wd.fish # downloads the newest fish function file
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > "$wd_source_containing_dir"/WarpDrive.java # downloads the newest java source code file
cd $wd_source_containing_dir && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/man/man1/wd.1 > /usr/local/share/man/man1/wd.1 # downloads man page
