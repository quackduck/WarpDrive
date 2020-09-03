if test ! "$wd_source_containing_dir"
    set wd_source_containing_dir ~
end
mkdir -p ~/.config/fish/functions
if test ! -e ~/.config/fish/functions/fish_prompt.fish
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/example_fish_prompt.fish > ~/.config/fish/functions/fish_prompt.fish # downloads the newest fish function file
end
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/wd.fish > ~/.config/fish/functions/wd.fish # downloads the newest fish function file
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > "$wd_source_containing_dir"/WarpDrive.java # downloads the newest java source code file
cd $wd_source_containing_dir && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it

# Get man page
if test (uname) = "Darwin"
  mkdir -p /usr/local/share/man/man1
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/man/man1/wd.1 > /usr/local/share/man/man1/wd.1 # downloads man page
else
  echo "Your password is needed to install the man page for WarpDrive"
  sudo mkdir -p /usr/local/share/man/man1
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/man/man1/wd.1 | sudo tee /usr/local/share/man/man1/wd.1 > /dev/null # downloads man page
end