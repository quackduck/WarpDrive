function wd --description 'Warp across directories'
  if test ! "$wd_source_containing_dir"
    set wd_source_containing_dir ~
  end
  if test "$argv[1]" = "--update"
    set_color bryellow; echo "Updating WarpDrive"; set_color normal;
    curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/install.fish | fish
  else
      if test "$argv[1]" = "-s"
        cd (java -cp $wd_source_containing_dir WarpDrive $argv[2..-1])
        ls
      else
        cd (java -cp $wd_source_containing_dir WarpDrive $argv)
      end
      set -g wd_last_added_dir (pwd)
  end
end
