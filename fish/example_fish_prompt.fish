function fish_prompt --description 'example prompt'

    if test ! "$wd_last_added_dir"
      set -g wd_last_added_dir (pwd)
    end
    if test "$wd_last_added_dir" != (pwd) -a (pwd) != "$HOME"
        wd --add (pwd)
    end
    set -g wd_last_added_dir (pwd)

    set_color brblue
    echo -n $USER
    echo -n @
    echo -n (hostname -s)
    set_color bryellow
    set -l pretty_dirs (dirs | string match -r '.+' | string trim)
    echo -n \ [$pretty_dirs]
    echo -n " "
    if test (string length $pretty_dirs) -ge 40
      echo
    end
    set_color brgreen
    echo -n ">  "
    set_color normal
end
