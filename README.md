# WarpDrive - Warp across directories

WarpDrive is a replacement for `cd` (However it does use `cd` to work). It tracks your visited directories and uses a "frecency" algorithm (combining frequency and recency) to determine which directory to warp to. 

## Usage

You can use WarpDrive like this:
```sh
wd someDir
```
If you had visited `someDir` before, it'll take you there. Otherwise, it will take you to a directory of that name in the working directory.

If you had visited `someDir` before, you could go there by using:
 ```sh
 wd s
 ```
 or 
 ```sh
 wd some
 ```
 
 ### More examples:
 
 ```sh
 wd parentDir childDir
 ```
 or 
 ```sh
 wd p c
 ```
 
 ## Installing
 
 `fish` is currently supported.
 
 You need to have `java` installed for WarpDrive to work. Use `brew cask install java` if you have Homebrew installed
 
 ### Fish Install
 
 WarpDrive can be **installed or updated** by
 
Having this:
 
 ```fish
 if test ! "$wd_last_added_dir"
    set -g wd_last_added_dir (pwd)
 end
 if test "$wd_last_added_dir" != (pwd) -a (pwd) != "$HOME"
    wd --add (pwd)
 end
 set -g wd_last_added_dir (pwd)
 ```
in your `fish_prompt` function and running these commands in a `fish` shell:

```fish
curl https://raw.githubusercontent.com/quackduck/WarpDrive/master/wd.fish > ~/.config/fish/functions/wd.fish # downloads the newest fish function file
curl https://raw.githubusercontent.com/quackduck/WarpDrive/master/src/WarpDrive.java > ~/WarpDrive.java # downloads the newest java source code file
cd ~ && javac WarpDrive.java && rm WarpDrive.java && cd - # compiles the java file and then deletes it
```

## Compatibility

The datafile format is the same as rupa/z, jethrokuan/z and z.lua

## Known Issues

WarpDrive is slow, quite ironically, taking ~160ms on average (This could be different for you. Do inform me about WarpDrive's performance)
This is because the JVM takes a lot of time to start up. The application itself takes 30ms.
 ## Caveats
 
  _WarpDrive stores data at_ `~/.WarpDriveData`.
 
 _WarpDrive will always take you to a directory that matches the last pattern given._
 
 In other words, if the datafile contains two directories: `/foo/bar` and `/bar/foo` and you use the command `wd f` it will take you to `/bar/foo`
 
 _WarpDrive is case sensitive. `wd Bar` is not the same as `wd bar`_
 
 ### Suggestions are welcome, file those or issues [here](https://github.com/quackduck/WarpDrive/issues)
