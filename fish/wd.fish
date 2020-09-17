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
        if set output (java -cp ~/.WarpDrive WarpDrive $argv[2..-1])
            cd $output; or return
            ls
        else
            for line in $output
                echo $line
            end
        end
        return
    end

    if set output (java -cp ~/.WarpDrive WarpDrive $argv)
        cd $output; or return
    else
        for line in $output
            echo $line
        end
    end
    set -g wd_last_added_dir (pwd)
end
