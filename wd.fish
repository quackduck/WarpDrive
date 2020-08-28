function wd --description 'Warp across directories'
  if test $argv[1] = "--update"
    set_color bryellow; echo "Updating WarpDrive"
    curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/install.fish | fish
  else
  cd (java -cp ~ WarpDrive $argv)
  set -g wd_last_added_dir (pwd)
  end
end
