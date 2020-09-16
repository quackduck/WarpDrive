echo "Uninstalling WarpDrive fully. Goodbye! Report bugs or issues at https://github.com/quackduck/WarpDrive/issues"
rm -r ~/.config/fish/conf.d/wd_on_prompt.fish ~/.config/fish/functions/wd.fish ~/.WarpDrive
if test (uname) = "Darwin"
    rm /usr/local/share/man/man1/wd.1 /usr/local/share/fish/vendor_completions.d/wd.fish
else
    echo "Your password is needed to uninstall the man page and fish completions for WarpDrive"
    sudo rm /usr/local/share/man/man1/wd.1 /usr/local/share/fish/vendor_completions.d/wd.fish
end
echo "WarpDrive has been uninstalled"