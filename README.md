## Description

Open a jar in an editor easily.

## Install

Add `[lein-open "0.1.0"]` to your ~/.lein/profiles.clj:

    {:user {:plugins [[lein-open "0.1.0"]]}}

## Usage

Within a lein project, view a jar dependency in an editor:

```sh
# View the table jar
$ lein open table
```

You can also view any jar in your maven repo by specifying it's full name and version:

```sh
$ lein open ring/ring-core 1.1.0
```

## Motivation

It's helpful to read dependency code and not have to depend on editor configuration.
This is quite similar to `bundler open` in [bundler](http://gembundler.com/).

## Limitations

This plugin can only open code in a non-console editor e.g. gvim or X-emacs. Attempting
to open a console editor will result in the error "emacs: standard input is not a tty\n".
To resolve this in mac osx, reinstall emacs with X support: `brew install emacs --with-x`.

## Bugs/Issues

Please report them [on github](http://github.com/cldwalker/lein-open/issues).

## Credits
* sunblaze for a bug fix
* dcuddeback for editor tweak

## TODO
* Download and open from clojars
* Download and first clojure project on github

## License

See LICENSE.TXT
