function wd --description 'Warp across directories'
  if test ! "$wd_source_containing_dir"
    set -l wd_source_containing_dir ~
  end
  if test "$argv[1]" = "--update"
    set_color bryellow; echo "Updating WarpDrive"; set_color normal;
    curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/install.fish | fish
  else
  cd (java -cp $wd_source_containing_dir WarpDrive $argv)
  set -g wd_last_added_dir (pwd)
  end
end
