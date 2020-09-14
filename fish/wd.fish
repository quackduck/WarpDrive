function wd --description 'Warp across directories'
    if test "$argv[1]" = "--update"
        set_color bryellow
        echo "Updating WarpDrive"
        set_color normal
        curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/install.fish | fish
        return
    end

    if test "\"$argv[1]\"" = "\"-v\"" -o "\"$argv[1]\"" = "\"--version\""
        echo "WarpDrive" (cat ~/.WarpDrive/version.txt)
        return
    end

    if test "\"$argv[1]\"" = "\"-c\"" -o "\"$argv[1]\"" = "\"--check\""
        set wd_newest_version (curl -sS https://raw.githubusercontent.com/quackduck/WarpDrive/master/version.txt)
        if test $wd_newest_version != (cat ~/.WarpDrive/version.txt)
            echo "Newer version: WarpDrive" $wd_newest_version "is available"
            echo "You currently have: WarpDrive" (cat ~/.WarpDrive/version.txt)
            echo "Run `wd --update` to update to the latest version"
        else
            echo "You currently have the newest version"
        end
        return
    end

    if test "\"$argv[1]\"" = "\"-s\"" -o "\"$argv[1]\"" = "\"--ls\""
        set wd_cd_to (java -cp ~/.WarpDrive WarpDrive $argv[2..-1])
        cd $wd_cd_to
        if test "$status" != "0"
            return 1
        end
        if test "$wd_cd_to" != "." -a "$wd_cd_to" != ""
            ls
        end
        return
    end

    cd (java -cp ~/.WarpDrive WarpDrive $argv)
    set -g wd_last_added_dir (pwd)
end
