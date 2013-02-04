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

(defn- unpack-and-view [group artifact version jar]
  (let [path (jar-file group artifact version)]
    (if (.exists path)
      (->> (unpack path) (sh/sh editor))
      (main/abort (format "No jar was found for %s. Try running `lein deps`." jar)))))

(defn open
  "Unpacks a jar in ~/.lein-open/:name and opens it an editor.
The editor defaults to emacs but can be configured with $LEIN_OPEN_EDITOR."
  [project jar & [version]]
  (if-let [pair (->> project
                    :dependencies
                    (filter (fn [[full-name version]]
                              (if (.contains jar "/")
                                (= (str full-name) jar)
                                (= (name full-name) jar))))
                    first)]
    (let [[full-name version] pair
          group (namespace full-name)
          artifact (name full-name)]
      (unpack-and-view group artifact version jar))
    (if (and version (.contains jar "/"))
      (let [[group artifact] (clojure.string/split jar #"/")]
        (unpack-and-view group artifact version jar))
      (main/abort (format "Jar %s not found in this project or your maven repo." jar)))))