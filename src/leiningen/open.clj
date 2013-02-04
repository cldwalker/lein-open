(ns leiningen.open
  (:require [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            clojure.string))

(def editor (or (System/getenv "LEIN_OPEN_EDITOR") "emacs"))
(def maven-repository [(System/getProperty "user.home") ".m2" "repository"])
(def lein-open-home (io/file (System/getProperty "user.home") ".lein-open"))

(defn- maven-file [& args]
  (apply io/file (concat maven-repository args)))

(defn- jar-file [group artifact version]
  (maven-file group artifact version (format "%s-%s.jar" artifact version)))

;; from https://github.com/Raynes/fs/blob/master/src/me/raynes/fs.clj
(defn- delete-dir
  "Delete a directory tree."
  [root]
  (when (.isDirectory root)
    (doseq [path (map #(io/file root %) (.list root))]
      (delete-dir path)))
  (.delete root))

(defn- unpack [path]
  (let [jar-dir (->> path .getName (re-find #"(.*)\.jar") second
                     (io/file lein-open-home))
        commands ["unzip" "-d" (.getPath jar-dir) (.getPath path)]]
    (.mkdirs jar-dir)
    (when (.exists jar-dir) (delete-dir jar-dir))
    (apply sh/sh commands)
    (.getPath jar-dir)))

(defn- unpack-and-view [group artifact version dep]
  (let [path (jar-file group artifact version)]
    (if (.exists path)
      (->> (unpack path) (sh/sh editor))
      (main/abort (format "No jar was found for %s. Try running `lein deps`." dep)))))

(defn open
  "Unpacks a project's dependency in ~/.lein-open/:name and opens it an editor.
The editor defaults to emacs but can be configured with $LEIN_OPEN_EDITOR."
  [project dependency & [version]]
  (if-let [dep (->> project
                    :dependencies
                    (filter (fn [[full-name version]]
                              (if (.contains dependency "/")
                                (= (str full-name) dependency)
                                (= (name full-name) dependency))))
                    first)]
    (let [[full-name version] dep
          group (namespace full-name)
          artifact (name full-name)]
      (unpack-and-view group artifact version dependency))
    (if (and version (.contains dependency "/"))
      (let [[group artifact] (clojure.string/split dependency #"/")]
        (unpack-and-view group artifact version dependency))
      (main/abort (format "Jar %s not found in this project or your maven repo." dependency)))))