mkdir -p ~/.config/fish/conf.d/
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/wd_on_prompt.fish > ~/.config/fish/conf.d/wd_on_prompt.fish
mkdir -p ~/.config/fish/functions
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/wd.fish > ~/.config/fish/functions/wd.fish # downloads the newest fish function file
mkdir -p ~/.WarpDrive
curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > ~/.WarpDrive/WarpDrive.java # downloads the newest java source code file
cd ~/.WarpDrive && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it

# Get man page
if test (uname) = "Darwin"
  mkdir -p /usr/local/share/man/man1
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/man/man1/wd.1 > /usr/local/share/man/man1/wd.1 # downloads man page
  mkdir -p /usr/local/share/fish/vendor_completions.d
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/completion/wd.fish > /usr/local/share/fish/vendor_completions.d/wd.fish
else
echo "Your password is needed to install the man page and fish completions for WarpDrive"
sudo fish -c "
  mkdir -p /usr/local/share/man/man1
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/man/man1/wd.1 > /usr/local/share/man/man1/wd.1 # downloads man page
  mkdir -p /usr/local/share/fish/vendor_completions.d
  curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/completion/wd.fish > /usr/local/share/fish/vendor_completions.d/wd.fish
  "
end