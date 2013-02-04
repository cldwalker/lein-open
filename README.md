## Description

Open a jar dependency in an editor easily.

## Install

Add `[lein-open "0.1.0-SNAPSHOT"]` to your ~/.lein/profiles.clj:

    {:user {:plugins [[lein-open "0.1.0-SNAPSHOT"]]}}

## Usage

Within a clojure project:

```sh
# Unpacks the table jar dependency and opens it an editor
$ lein open table
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

## TODO
* Open any clojar i.e. one not in a project or even downloaded.

## License

See LICENSE.TXT