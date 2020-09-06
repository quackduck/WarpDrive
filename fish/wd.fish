function wd --description 'Warp across directories'
  if test ! "$wd_source_containing_dir"
    set wd_source_containing_dir ~/.WarpDrive
  end
  if test "$argv[1]" = "--update"
    set_color bryellow; echo "Updating WarpDrive"; set_color normal;
    curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/install.fish | fish
    return
  end
  if test "$argv[1]" = "-s"
      set wd_cd_to (java -cp $wd_source_containing_dir WarpDrive $argv[2..-1])
      cd $wd_cd_to
      set wd_remember_status_of_cd $status
    if test "$wd_remember_status_of_cd" != "0"
      return "$wd_remember_status_of_cd"
    end
    if test "$wd_cd_to" != "." -a "$wd_cd_to" != ""
      ls
    end
    return
  end

  cd (java -cp $wd_source_containing_dir WarpDrive $argv)
  set -g wd_last_added_dir (pwd)
end
