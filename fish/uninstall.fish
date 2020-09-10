if test ! "$wd_source_containing_dir"
    set wd_source_containing_dir ~/.WarpDrive
end
rm ~/.config/fish/conf.d/wd_on_prompt.fish ~/.config/fish/functions/wd.fish "$wd_source_containing_dir"/WarpDrive.class

if test (uname) = "Darwin"
  rm /usr/local/share/man/man1/wd.1 /usr/local/share/fish/vendor_completions.d/wd.fish
else
  echo "Your password is needed to uninstall the man page and fish completions for WarpDrive"
  sudo rm /usr/local/share/man/man1/wd.1 /usr/local/share/fish/vendor_completions.d/wd.fish
end