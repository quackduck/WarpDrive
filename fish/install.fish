if test -d ~/WarpDrive
  rm -r ~/WarpDrive
end

git clone -q --depth 1 -b (curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/version.txt) https://github.com/quackduck/WarpDrive.git ~/WarpDrive 2> /dev/null; or
  echo "An error occurred while downloading files."; echo "Please report this error at https://github.com/quackduck/WarpDrive/issues"; exit

mkdir -p ~/.config/fish/conf.d
mkdir -p ~/.config/fish/functions
mkdir -p ~/.WarpDrive

cp ~/WarpDrive/fish/wd_on_prompt.fish ~/.config/fish/conf.d
cp ~/WarpDrive/fish/wd.fish ~/.config/fish/functions
cp ~/WarpDrive/version.txt ~/.WarpDrive/version.txt
cp ~/WarpDrive/src/WarpDrive.java ~/.WarpDrive/WarpDrive.java
cd ~/.WarpDrive && javac WarpDrive.java && rm WarpDrive.java && cd -

if test (uname) = "Darwin"
  mkdir -p /usr/local/share/man/man1
  mkdir -p /usr/local/share/fish/vendor_completions.d
  cp ~/WarpDrive/man/man1/wd.1 /usr/local/share/man/man1/wd.1
  cp ~/WarpDrive/fish/completion/wd.fish /usr/local/share/fish/vendor_completions.d/wd.fish
else
  echo "Your password is needed to install the man page and fish completions for WarpDrive"
  sudo fish -c "
    mkdir -p /usr/local/share/man/man1
    mkdir -p /usr/local/share/fish/vendor_completions.d
    cp ~/WarpDrive/man/man1/wd.1 /usr/local/share/man/man1/wd.1
    cp ~/WarpDrive/fish/completion/wd.fish /usr/local/share/fish/vendor_completions.d/wd.fish"
end

rm -r ~/WarpDrive
echo "WarpDrive has been installed! Hooray! Run `wd --help` for help.
Do let quackduck know you have WarpDrive by email at <igoel.mail@gmail.com> or by starring the repo"