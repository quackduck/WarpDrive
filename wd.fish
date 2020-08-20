function wd --description 'Warp across directories'
  cd (java -cp ~ WarpDrive $argv)
  set -g wd_last_added_dir (pwd)
end
