# WarpDrive - Warp across directories

WarpDrive is a replacement for `cd` (However it does use `cd` to work). It tracks your visited directories and uses a "frecency" algorithm (combining frequency and recency) to determine which directory to warp to.

![Example Usage](example.png)

## Usage

You can use WarpDrive like this:
```sh
wd someDir
```
If you had visited `someDir` before (and thus `someDir` is in the datafile), it'll take you there. Otherwise, it'll just pass on the aguments to `cd` (which means that both relative and absolute paths work).

You could also do the same thing with just parts of the full path:
 ```sh
 wd s
 ```
 or 
 ```sh
 wd Dir
 ```
 ### Options 
 Currently, WarpDrive has six options:
 
 **Option -s**
 
 Runs `ls` after warping to a directory.
 ```
  wd --help
 ```
 
 **Option --help or -h**

Prints a small help message (run `man wd` for more help):

```
 wd --help
```
Output:
```
WarpDrive - Warp across directories
Usage: wd [<option>] [<pattern> ...]
Options:
   -s                              Run ls after warping to a directory
   --add, -a <path> ...            Add paths to be tracked. Paths are automatically added when visited
   --remove, --rm, -r <path> ...   Remove paths so they are no longer tracked
   --list, --ls, -l                List tracked paths and their points, sorted by most
   --update                        Update WarpDrive to the latest commit
   --help, -h                      Print this help message
Examples:
   wd
   wd /
   wd dir-in-pwd
   wd dir-that-was-visited-before
   wd grand-parent-dir parent-dir child-dir
   wd parent-dir grand-parent-dir child-dir
   wd a-part-of-the-full-name-of-some-dir
   wd /absolute/path/to/somewhere
   wd -s run-ls-after-warping
   wd --add dir-to-add
   wd --remove dir-to-remove
   wd --list
   wd --update
   wd --help
Note:
   To go to the home directory don't specify any arguments, i.e. use just `wd` (like cd)
   When specifying multiple patterns, order doesn't matter except for the last pattern given
      i.e. WarpDrive will always take you to a directory whose name matches the last pattern
   If <pattern> is specified after an option, <pattern> will be ignored unless the option is -s
   -s is accepted silently even if you use any other option (this is so that you can make an alias with -s)

Refer to https://github.com/quackduck/WarpDrive for more information
```
 
 **Option --update**
 
Updates WarpDrive to the latest commit. It is recommended to update with `wd --update` to ensure you have the latest WarpDrive.

```
wd --update
```
 
 **Option -a or --add**

Adds paths. Paths are automatically added when you visit them.

```sh
wd --add /Users /usr/local/bin
```

 **Option -r or --remove**

Removes paths. This does not "decrement" frequency. It completely deletes a path, so it is no longer tracked (Unless, of course, you visit it again).

```sh
wd --remove /Users /usr/local/bin
```
**Option -l or --list**

Shows tracked directories and their points, sorted by most.
```sh
wd --list
```
Sample output:
```
Points	Directory
150.0	/Users/ishan/Desktop/GitHub/WarpDrive
36.0	/Users/ishan/Desktop/tests/foo
6.0	/Users/ishan/.config/fish
5.0	/Users/ishan/.config
3.0	/Users/ishan/.config/fish/functions
1.0	/usr/libexec
1.0	/Library/Java/JavaVirtualMachines/openjdk-14.0.2.jdk/Contents/Home
1.0	/Library/Java/JavaVirtualMachines/openjdk-14.0.2.jdk/Contents/Home/bin
1.0	/
1.0	/Users/ishan/Downloads
1.0	/Users/ishan
1.0	/Users/ishan/.config/fish/conf.d
0.5	/Users/ishan/Desktop
0.5	/Users/ishan/Desktop/tests
```

 _Even though most of these "options" would be better implemented as standalone actions, they are implemented as regular options because they have the advantage of not being like filenames_
 
 ### Different Arguments
 
 WarpDrive recognizes seperate arguments as matching different directories in a path. They do not need to be in order.
 
 Both
 ```sh
 wd grandParentDir parentDir childDir
 ```
 or 
 ```sh
 wd parent grand child
 ```
 will have the same result. 
 
 **Note! WarpDrive will *always* take you to a directory matching the last pattern. For more info on what this means, check the second Caveat.**
 
 ## Installing
 
 `fish` is currently supported.
 
 You need to have `java` installed for WarpDrive to work. Use `brew cask install java` if you have Homebrew installed
 
 ### Fish Install
 
 WarpDrive can be installed by:
 
Adding this:
 
 ```fish
 if test ! "$wd_last_added_dir"
    set -g wd_last_added_dir (pwd)
 end
 if test "$wd_last_added_dir" != (pwd) -a (pwd) != "$HOME"
    wd --add (pwd)
 end
 set -g wd_last_added_dir (pwd)
 ```
somewhere in your `fish_prompt` function and running this command in a `fish` shell:

```fish
curl https://raw.githubusercontent.com/quackduck/WarpDrive/master/fish/install.fish | fish
```

## Compatibility

The datafile format is the same as rupa/z, jethrokuan/z, zsh-z and z.lua

## Known Issues

WarpDrive is a bit slow, compared to native shell scripts, taking ~150ms on average (tested using [Hyperfine](https://github.com/sharkdp/hyperfine)) for most commands (This could be different for you. Do inform me about WarpDrive's performance or leave a review at <igoel.mail@gmail.com>)
This is because the JVM takes a lot of time to start up. The application itself takes 30ms.
 
 ## Caveats
 
  _WarpDrive stores data at_ `~/.WarpDrive/WarpDriveData.txt`.
 
 _WarpDrive will always take you to a directory that matches the last pattern given._ In other words, if the datafile contains two directories: `/foo/bar` and `/bar/foo` and you use the command `wd f` it will take you to `/bar/foo`.
 
 _WarpDrive is case-sensitive. `wd Bar` is not the same as `wd bar`._

_Just `wd` takes you to the home directory._

_No options except `-s` can be grouped together. `wd -hl --add /` won't work but `wd -s ~` will_

_If any options except -s are specified, you stay in the same directory even if you specified a directory. `wd -l someDir` won't work_
 
 ### Suggestions are welcome, file those or issues [here](https://github.com/quackduck/WarpDrive/issues).
