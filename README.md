## Description

Open a jar dependency in an editor easily.

## Install

Add `[lein-open "0.1.0-SNAPSHOT"]` to your ~/.lein/profiles.clj:

    {:user {:plugins [[lein-open "0.1.0-SNAPSHOT"]]}}

## Usage

Within a clojure project:

```sh
# Unpacks the table jar dependency for viewing
$ lein open table
/Users/me/.lein-open/table-0.3.2

# To open in an editor
$ emacs $(lein open table)
```

## Motivation

It's helpful to read dependency code and not have to depend on editor configuration.
This is quite similar to `bundler open` in [bundler](http://gembundler.com/).

## Bugs/Issues

Please report them [on github](http://github.com/cldwalker/lein-open/issues).

## TODO
* Open up unpacked jar within an editor automatically
* Open any clojar i.e. one not in a project or even downloaded.

## License

See LICENSE.TXT